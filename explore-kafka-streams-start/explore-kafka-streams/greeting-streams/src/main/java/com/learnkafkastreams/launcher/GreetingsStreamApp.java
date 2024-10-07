package com.learnkafkastreams.launcher;

import com.learnkafkastreams.Topology.GreetingsTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class GreetingsStreamApp {

    private static final Logger log = LoggerFactory.getLogger(GreetingsStreamApp.class);

    public static void main(String[] args) {

        //Setting up kafka properties
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"greetings-app"); //Equivalent to consumer group, it is like a bookmark
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");

        //Specify kafka broker
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        var greetingsTopology = GreetingsTopology.buildTopology();
        var kafkaStreams =  new KafkaStreams(greetingsTopology,properties);
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        //createTopics(properties,List.of(GreetingsTopology.greetings,GreetingsTopology.greetings_Uppercase));



        try{
            kafkaStreams.start();
        }
        catch(Exception e){
            log.error("Some exception occurred: {}",e.getMessage());
            System.out.println("Some exception occurred: " + e.getMessage());
        }

    }

    private static void createTopics(Properties config, List<String> greetings) {

        try(AdminClient admin = AdminClient.create(config)) {
            var partitions = 1;
            short replication = 1;

            var newTopics = greetings
                    .stream()
                    .map(topic -> {
                        return new NewTopic(topic, partitions, replication);
                    })
                    .collect(Collectors.toList());


            try {
                var createTopicResult = admin.createTopics(newTopics);
                createTopicResult
                        .all().get();
                log.info("topics are created successfully");
            } catch (Exception e) {
                log.error("Exception creating topics : {} ", e.getMessage(), e);
            }
        }
        catch(Exception e){

            log.error("Some exception occurred: {}",e.getMessage());
        }
    }
}
