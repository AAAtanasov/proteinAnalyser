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
        ApplicationProperties applicationProperties = new ApplicationProperties("F:\\proteinProjData\\Roh\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d");
        String url = applicationProperties.getKafkaUrl();
        int batchSize = applicationProperties.getBatchSize();

        KafkaProducer<String, String> kafkaProducer = KafkaProducerSingleton.getSingletonInstance(applicationProperties);

        String sqlStatement = "INSERT INTO ProcessedFramePrecursorPairs (FrameId, PrecursorId) VALUES (?, ?)";

        try {
            int precursorcount = 0;
            BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);
            Connection connection = bruker.getCurrentConnection();
            PreparedStatement pstmt = connection.prepareStatement(sqlStatement);

            // Set auto commit to false so we can perform batch commits.
            connection.setAutoCommit(false);

            try {
                for (ISpectrum spectrum : bruker.getPrecursors()) {
                    precursorcount++;
                    Spectrum[] spectrums = spectrum.getSpectrum();

                    for (Spectrum spec : spectrums){
                        if (spec != null){
                            // Producer sends messaged
                            sendMessage(url, spec.getSpectrumInformationAsString(), kafkaProducer);

                            //We prepare a statement to insert the already processed Frame-Precursor Pair
                            pstmt.setString(1, spec.frameId.toString());
                            pstmt.setString(2, spec.precursorId.toString());
                            pstmt.addBatch();
                        }
                    }

                    if ((precursorcount % batchSize) == 0) {
                        System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public static void sendMessage(String url, String content, KafkaProducer kafkaProducer) {
        String message = content;
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, message);
        kafkaProducer.send(record);
    }

}
