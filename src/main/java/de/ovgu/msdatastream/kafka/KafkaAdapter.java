package de.ovgu.msdatastream.kafka;

import de.ovgu.msdatastream.ApplicationProperties;
import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.model.Spectrum;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAdapter {
    public final static String KAFKA_URL = System.getenv("KAFKA_URL") != null ? System.getenv("KAFKA_URL")
            : "localhost:9092";

    public final static String KAFKA_TOPIC = System.getenv("KAFKA_TOPIC") != null ? System.getenv("KAFKA_TOPIC")
            : "test-topic";

    public static void main(String[] args){
        String url = "localhost:9092";
        ApplicationProperties applicationProperties = new ApplicationProperties("F:\\proteinProjData\\Roh\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d");

        KafkaProducer<String, String> kafkaProducer = KafkaProducerSingleton.getSingletonInstance(url);

        //TODO: collect end point what has been sent and what not
        //FIXME: table in sql where not in new table
        //create table - check if present - if present just insert

        try {
            int precursorcount = 0;
            BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);


            for (ISpectrum spectrum : bruker.getPrecursors()) {
                precursorcount++;

                Spectrum[] spectrums = spectrum.getSpectrum();
                for (Spectrum spec : spectrums){
                    if (spec != null){
                        sendmessage(url, getSpectrumInformationAsString(spec), kafkaProducer);
//                        bruker.insertVisitedFramesAndPrecursors(spec.frameId, spec.precursorId);
                    }
                }

                if ((precursorcount % 100) == 0) {
                    System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }

    public static void sendmessage(String url, String content, KafkaProducer kafkaProducer) {
        String message = content;
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, message);
        kafkaProducer.send(record);

    }


    private static String getSpectrumInformationAsString(Spectrum spec){
        StringBuilder tempString = new StringBuilder();
        tempString.append("\n");
        tempString.append("BEGIN IONS");
        tempString.append("\n");
        tempString.append("TITLE: FrameId=" + spec.frameId + "_PrecursorId=" + spec.precursorId); //FRAME ID + PRECURSOR ID
        tempString.append("\n");
        tempString.append("PEPMASS=" + spec.precursorMZ + "\t" + spec.precursorINT);
        tempString.append("\n");
        tempString.append("RTINSECONDS=" + spec.rtinseconds + "\n");
        tempString.append("SCANS=" + spec.scanBegin+ ", " + spec.scanEnd);
        tempString.append("\n");
        tempString.append("CHARGE=" + spec.charge + spec.polarity); //todo : FRAME CHARGE
        tempString.append("\n");
        for (int i = 0; i < spec.intensitiesArray.length; i++) {
            if (spec.intensitiesArray[i] != 0) {
                tempString.append(spec.mzArray[i] + "\t" + spec.intensitiesArray[i]);
                tempString.append("\n");
            }
        }
        tempString.append("END IONS");
        tempString.append("\n");
        tempString.append("\n");

        return tempString.toString();
    }


}
