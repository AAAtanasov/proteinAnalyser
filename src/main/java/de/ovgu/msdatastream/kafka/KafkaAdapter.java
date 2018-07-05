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
        BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);

        //TODO: track which entries have been processed
        Map<String, Object> producerConfig = new HashMap<String, Object>();
        producerConfig.put("bootstrap.servers", url);
        producerConfig.put("metadata.fetch.timeout.ms", "3000");
        producerConfig.put("request.timeout.ms", "3000");
        // ... other options: http://kafka.apache.org/documentation.html#producerconfigs
        StringSerializer serializer = new StringSerializer();
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerConfig, serializer, serializer);


        try {
            for (ISpectrum spectrum : bruker.getPrecursors()) {
                int precursorcount = 0;

                Spectrum[] spectrums = spectrum.getSpectrum();
                for (Spectrum spec : spectrums){
                    if (spec != null){
                        precursorcount++;
                        if ((precursorcount % 1000) == 0) {
                            System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
                            break;
                        }
                        sendmessage(url, getSpectrumInformationAsString(spec), kafkaProducer);
                    }

                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            recievemessage(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public static void sendmessage(String url, String content, KafkaProducer kafkaProducer) {
        System.out.println("send for " + url);

        String message = content;
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, message);
        kafkaProducer.send(record);

    }

    public static void recievemessage(String url) {
        System.out.println("recieve for " + url);
        final Consumer<String, String> consumer = createConsumer(url);

        final int giveUp = 10;
        int noRecordsCount = 0;

        System.out.println("while loop beginn...");
        while (true) {
            System.out.println("loop");
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(1);
            System.out.println("get a message");
            System.out.println("found " + consumerRecords.count() + " messages in " + url);
            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp)
                    break;
                else
                    continue;
            }
            System.out.println("found " + consumerRecords.count() + " messages in " + url);
            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %d, %d)\n", record.key(), record.value(),
                        record.partition(), record.offset());
            });
            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE FOR " + url);

    }

    private static Consumer<String, String> createConsumer(String url) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        // Create the consumer using props.
        final Consumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.listTopics().forEach((x, y) -> {
            System.out.println(x);

        });
        System.out.println("consumer connection is here");
        TopicPartition tp = new TopicPartition("test-topic", 0);
        List<TopicPartition> tps = Arrays.asList(new TopicPartition("test-topic", 0)
        );

        consumer.assign(tps);
        consumer.seekToBeginning(tps);

        System.out.println("return the 09:36 consumer");
        return consumer;
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
