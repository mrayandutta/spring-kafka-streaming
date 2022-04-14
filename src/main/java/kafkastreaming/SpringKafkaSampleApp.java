package kafkastreaming;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kafkastreaming.model.event.Event;
import kafkastreaming.producer.KafkaProducer;
import kafkastreaming.serde.CustomSerdes;
import kafkastreaming.type.EventAggregate;
import kafkastreaming.util.RollupAggregator;

@SpringBootApplication
public class SpringKafkaSampleApp implements CommandLineRunner
{
	private static Logger logger = LoggerFactory.getLogger(SpringKafkaSampleApp.class.getName());
	private static final Initializer<EventAggregate> aggregateInitializer = ()-> new EventAggregate(0,0);
	@Autowired
	RollupAggregator rollupAggregator;

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value(value = "${application.id}")
	private String applicationId;

	@Value(value = "${event.topic}")
	private String eventTopicName;

	@Value(value = "${state.store.name}")
	private String stateStoreName;

	@Autowired
	KafkaProducer kafkaProducer;


	public static void main(String[] args) {
		logger.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringKafkaSampleApp.class, args);
		logger.info("APPLICATION FINISHED");
		//System.exit(1);
	}

	@Override
	public void run(String... args) throws Exception {
		pushMultipleEventsToTopic();
		printTopicMessagesFromStreams();

	}

	public void printTopicMessagesFromStreams() {

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);


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
						Materialized.<Integer,EventAggregate, KeyValueStore<Bytes, byte[]>>as(stateStoreName)
								.withKeySerde(Serdes.Integer())
								.withValueSerde(CustomSerdes.EventAggregate())
				)
				.toStream()
				.foreach((key, value) -> {
					System.out.println("After aggregation");
					System.out.println("Key:"+key+",value:"+value);
				});
		//.print(Printed.toSysOut());
		//.print(Printed.toSysOut(),Produced.with(Serdes.Integer(), CustomSerdes.EventAggregate()));



		KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Shutting down stream");
			streams.close();
		}));
	}



	public void pushMultipleEventsToTopic() {
		Event event1 = new Event(1,1);
		Event event2 = new Event(2,1);
		Event event3 = new Event(2,1);
		Event event4 = new Event(3,1);
		Event event5 = new Event(2,1);
		Event event6 = new Event(1,1);
		kafkaProducer.sendMessageToEventTopic(event1);
		kafkaProducer.sendMessageToEventTopic(event2);
		kafkaProducer.sendMessageToEventTopic(event3);
		kafkaProducer.sendMessageToEventTopic(event4);
		kafkaProducer.sendMessageToEventTopic(event5);
		kafkaProducer.sendMessageToEventTopic(event6);

	}
}
