package kafkastreaming.consumer;

import kafkastreaming.SpringKafkaSampleApp;
import kafkastreaming.model.event.Event;
import kafkastreaming.serde.CustomSerdes;
import kafkastreaming.type.EventAggregate;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;

@Component
public class EventConsumer {
    private static Logger logger = LoggerFactory.getLogger(EventConsumer.class.getName());
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${application.id}")
    private String applicationId;

    @Value(value = "${event.topic}")
    private String eventTopicName;

    @Value(value = "${state.keyvalue.store.name}")
    private String keyValueStateStoreName;

    @Value(value = "${state.window.store.name}")
    private String windowStateStoreName;

    private Properties getEnvironmentProperties()
    {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);
        return props;
    }
    public Topology createTopologyForEventAggregation() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<Integer, Event> topicStream = streamsBuilder.stream(eventTopicName,
                        Consumed.with(Serdes.String(), CustomSerdes.Event()))
                .map((key, value) ->new KeyValue<Integer,Event>(value.getId(), value));

        topicStream.foreach((key, value) -> {
            System.out.println("================");
            System.out.println(value.getId()+","+value.getValue());
        });

        topicStream
                //.groupByKey()
                .groupBy((k,v) -> k, Grouped.with(Serdes.Integer(),CustomSerdes.Event()))
                .aggregate(
                        ////Initializer
                        new Initializer<EventAggregate>() {
                            @Override
                            public EventAggregate apply() {
                                return new EventAggregate(null, null);
                            }
                        },
                        //Aggregator
                        new Aggregator<Integer, Event, EventAggregate>() {
                            @Override
                            public EventAggregate apply(final Integer key, final Event value,final EventAggregate aggregate) {
                                System.out.println("key:"+key);
                                System.out.println("value:"+value);
                                System.out.println("aggregate before :"+aggregate);
                                Integer existingSum = aggregate.getSum();
                                if(existingSum==null)
                                    existingSum = 0;
                                if(aggregate.getId()==null)
                                    aggregate.setId(value.getId());
                                aggregate.setSum(existingSum+value.getValue());
                                System.out.println("aggregate after :"+aggregate);
                                return aggregate;
                            }},
                        //Serializer
                        Materialized.<Integer,EventAggregate, KeyValueStore<Bytes, byte[]>>as(keyValueStateStoreName)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(CustomSerdes.EventAggregate())
                )
                .toStream()
                .foreach((key, value) -> {
                    System.out.println("After aggregation");
                    System.out.println("Key:"+key+",value:"+value);
                });

        return streamsBuilder.build();
    }

    public Topology createTopologyForEventAggregationWithWindowing() {

        Properties props = getEnvironmentProperties();
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<Integer, Event> topicStream = streamsBuilder.stream(eventTopicName,
                        Consumed.with(Serdes.String(), CustomSerdes.Event()))
                .map((key, value) ->new KeyValue<Integer,Event>(value.getId(), value));

        topicStream.foreach((key, value) -> {
            System.out.println("================");
            System.out.println(value.getId()+","+value.getValue());
        });

        topicStream
                //.groupByKey()
                .groupBy((k,v) -> k, Grouped.with(Serdes.Integer(),CustomSerdes.Event()))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .aggregate(
                        ////Initializer
                        new Initializer<EventAggregate>() {
                            @Override
                            public EventAggregate apply() {
                                return new EventAggregate(null, null);
                            }
                        },
                        //Aggregator
                        new Aggregator<Integer, Event, EventAggregate>() {
                            @Override
                            public EventAggregate apply(final Integer key, final Event value,final EventAggregate aggregate) {
                                System.out.println("key:"+key);
                                System.out.println("value:"+value);
                                System.out.println("aggregate before :"+aggregate);
                                Integer existingSum = aggregate.getSum();
                                if(existingSum==null)
                                    existingSum = 0;
                                if(aggregate.getId()==null)
                                    aggregate.setId(value.getId());
                                aggregate.setSum(existingSum+value.getValue());
                                System.out.println("aggregate after :"+aggregate);
                                return aggregate;
                            }},
                        //Serializer
                        Materialized.<Integer,EventAggregate, WindowStore<Bytes, byte[]>>as(windowStateStoreName)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(CustomSerdes.EventAggregate())
                )
                .toStream()
                .foreach((key, value) -> {
                    System.out.println("After aggregation");
                    System.out.println("Key:"+key+",value:"+value);
                });

        return streamsBuilder.build();
    }

    public void processEventStreams()
    {
        KafkaStreams streams = new KafkaStreams(createTopologyForEventAggregation(),getEnvironmentProperties());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down stream");
            streams.close();
        }));
    }

    public void processEventStreamsWithWindowing()
    {
        KafkaStreams streams = new KafkaStreams(createTopologyForEventAggregationWithWindowing(),getEnvironmentProperties());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down stream");
            streams.close();
        }));
    }
}
