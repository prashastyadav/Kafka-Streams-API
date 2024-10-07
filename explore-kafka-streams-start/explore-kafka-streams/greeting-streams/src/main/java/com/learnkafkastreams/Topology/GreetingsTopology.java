package com.learnkafkastreams.Topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class GreetingsTopology {

    public static String greetings = "greetings";

    public static String greetings_uppercase = "greetings_uppercase";

    public static Topology buildTopology(){

        //StreamsBuilder is a building block to define source processor,
        //stream processing logic and sink processor

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //Source processor
        //In kafka streams the serializer and deserializer are categorized as Serdes
        var greetingsStream = streamsBuilder.stream(greetings, Consumed.with(Serdes.String(),Serdes.String()));

        //Print source messages
        greetingsStream.print(Printed.<String,String>toSysOut().withLabel("greetingsStream"));

        //Processing logic
        //Uppercase operation
        var modifiedStream = greetingsStream
                                    .mapValues((readOnlyKey,value)->value.toUpperCase());

        //Print modified messages
        modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));

        //Sink processor
        modifiedStream.to(greetings_uppercase, Produced.with(Serdes.String(),Serdes.String()));


        return  streamsBuilder.build();

    }
}
