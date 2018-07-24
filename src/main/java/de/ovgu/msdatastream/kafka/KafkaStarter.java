package de.ovgu.msdatastream.kafka;

import de.ovgu.msdatastream.ApplicationProperties;
import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.model.Spectrum;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KafkaStarter {
    public final static String KAFKA_TOPIC = "test-topic";

    public static void main(String[] args){
        ApplicationProperties applicationProperties = new ApplicationProperties();

        boolean shouldEnd = false;


        // move to a better file locations
        // decide flow -> either kafka or mgf writer
        // calculate elapsed time: use System.nano
        while (true){
            if (shouldEnd) {
                break;
            }

            try {
                // Connect to SQLiteDB, Perform Select statement and populate object with information regarding
                // the non processed Frames and Precursors.
                BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);

                //Use the BrukerRawFormatWrapper to iterate over its ISpectrum object returned by the bruker object.
                processData(bruker);

                System.out.println("Processed iteration...");

                Thread.sleep(5000);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                //FIXME: or should it break?
                break;
            }

        }


    }

    private static void processData(BrukerRawFormatWrapper bruker) throws SQLException{
        int iterationCount = 0;
        Connection connection = bruker.getCurrentConnection();
        PreparedStatement pstmt = connection.prepareStatement(bruker.getApplicationProperties().getInsertProcessedIdsSqlString());
        KafkaProducer<String, String> kafkaProducer = KafkaProducerSingleton.getSingletonInstance(bruker.getApplicationProperties());

        // Set auto commit to false so we can perform batch commits.
        connection.setAutoCommit(false);

        try {
            for (ISpectrum spectrum : bruker.getPrecursors()) {
                iterationCount++;
                Spectrum[] spectrums = spectrum.getSpectrum();

                for (Spectrum spec : spectrums){
                    if (spec != null){
                        // Producer sends messaged
                        //write to mgf file to show
                        //TODO: time measure, txt file as properties, solution as jar, if write to mgf or kafka, evaluation of time, write in file , possible optimisation, total time, function time
                        sendMessage(bruker.getApplicationProperties().getKafkaUrl(), spec.getSpectrumInformationAsString(), kafkaProducer);

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
        } finally {
            bruker.close();
        }
    }

    private static void sendMessage(String url, String content, KafkaProducer kafkaProducer) {
        String message = content;
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, message);
        kafkaProducer.send(record);
    }

}
