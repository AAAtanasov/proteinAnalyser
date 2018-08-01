package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.kafka.KafkaProducerSingleton;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Date;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Starter {

	public static void main(String[] args) throws IOException {
			String tdfDicrectory = args[0];
			String targetFile = args[1];
			ApplicationProperties applicationProperties = new ApplicationProperties(tdfDicrectory, targetFile);
			boolean isKafkaProducer = applicationProperties.getIsKafkaProducer();
			Integer maxEmptyIterations = applicationProperties.getMaxEmptyIterations();
			System.out.println("Max empty iterations are : " + maxEmptyIterations);
            boolean hasProcessedSomething;
            Integer threadSleepTime = applicationProperties.getThreadSleepTime();
			DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
			MGFWriter timeWriter = new MGFWriter("times.txt");

			//TODO: calculate elapsed time: use System.nano
			int emptyIterationIndex = 0;
			try	{
				timeWriter.plainWrite("Start experiment : \n");
				timeWriter.plainWrite("----------------: \n");

				while (emptyIterationIndex < maxEmptyIterations) {
					try {
						long startTimeBruker = System.nanoTime();
						BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);

						long endTimeBruker = System.nanoTime();
						long elapsedMiliSeconds = TimeUnit.NANOSECONDS.toMillis(endTimeBruker - startTimeBruker);

						System.out.println("Elapsed miliseconds are: " + elapsedMiliSeconds);
						timeWriter.plainWrite("Bruker miliseconds elapsed: " + elapsedMiliSeconds + "\n");

						long startTimeProcessor = System.nanoTime();
						hasProcessedSomething = processData(bruker, isKafkaProducer);

						long endTimeProcessor = System.nanoTime();
						long elapsedMiliSecondsProcess = TimeUnit.NANOSECONDS.toMillis(endTimeProcessor - startTimeProcessor);

						timeWriter.plainWrite("Processor miliseconds elapsed: " + elapsedMiliSecondsProcess + "\n");

						System.out.println("Processed iteration...");

						if (hasProcessedSomething) {
							emptyIterationIndex = 0;
						} else {
							emptyIterationIndex++;
						}

						Thread.sleep(threadSleepTime);

					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						break;
					}
				}

				timeWriter.plainWrite("Start experiment : \n");
				timeWriter.plainWrite("----------------: \n");
			} catch (Exception ex ) {
				System.out.println(ex.getMessage());
				throw ex;

			} finally {
				timeWriter.close();
			}

	}

	private static boolean processData(BrukerRawFormatWrapper bruker, boolean isKafkaProducer) throws SQLException, IOException {
		String kafkaTopic = isKafkaProducer ? bruker.getApplicationProperties().getKafkaTopic() : "";
		MGFWriter writer = isKafkaProducer ? null : new MGFWriter(bruker.getApplicationProperties().getTargetFile());
		KafkaProducer<String, String> kafkaProducer = isKafkaProducer ? KafkaProducerSingleton.getSingletonInstance(bruker.getApplicationProperties()) : null;

		int iterationCount = 0;
		Connection connection = bruker.getCurrentConnection();
		PreparedStatement pstmt = connection.prepareStatement(bruker.getApplicationProperties().getInsertProcessedIdsSqlString());

		// Set auto commit to false so we can perform batch commits.
		connection.setAutoCommit(false);

		try {
			for (ISpectrum spectrumContainer : bruker.getPrecursors()) {
				iterationCount++;
				Spectrum[] spectrums = spectrumContainer.getSpectrum();

				for (Spectrum spec : spectrums){
					if (spec != null){
						//TODO: time measure, solution as jar, possible optimisation, total time, function time
						if (isKafkaProducer) {
							// Producer sends messaged
							sendMessage(spec.getSpectrumInformationAsString(),
										kafkaProducer,
										kafkaTopic);
						} else {
							writer.writeSpectrum(spec.getSpectrumInformationAsString());
						}

						//We prepare a statement to insert the already processed Frame-Precursor Pair
						pstmt.setString(1, spec.frameId.toString());
						pstmt.setString(2, spec.precursorId.toString());
						pstmt.addBatch();
					}
				}

				if ((iterationCount % bruker.getApplicationProperties().getBatchSize()) == 0) {
					System.out.println("Writing Precursor " + iterationCount + " of " + bruker.getPrecursors().size());
					//execute batch
					pstmt.executeBatch();
					connection.commit();
				}
			}

			// ensure no trailing changes are left
			pstmt.executeBatch();
			connection.commit();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			// If error occurs we wish to rollback any changes.
			connection.rollback();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();

		} finally {
			bruker.close();
			if (writer != null) {
				writer.close();
			}
		}

        return iterationCount > 0;

    }

	private static void sendMessage(String content, KafkaProducer kafkaProducer, String kafkaTopic) {
		String message = content;
		ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, message);
		kafkaProducer.send(record);
	}

}
