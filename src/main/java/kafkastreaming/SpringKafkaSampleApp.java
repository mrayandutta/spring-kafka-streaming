package kafkastreaming;

import kafkastreaming.config.AppConfigs;
import kafkastreaming.model.event.Event;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kafkastreaming.model.employee.Employee;
import kafkastreaming.producer.KafkaProducer;

import java.util.Properties;

@SpringBootApplication
public class SpringKafkaSampleApp implements CommandLineRunner
{
	private static Logger logger = LoggerFactory.getLogger(SpringKafkaSampleApp.class.getName());
	
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
				//pushDummyDataToEmployeeTopic();
				//printTopicMessagesFromStreams();
				pushMultipleEventsToTopic();

			}

	public static void printTopicMessagesFromStreams() {

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());
		props.put(StreamsConfig.STATE_DIR_CONFIG,AppConfigs.stateStoreLocation);

		StreamsBuilder streamsBuilder = new StreamsBuilder();
		KStream<String,String> topicStream = streamsBuilder.stream(AppConfigs.topicName);
		topicStream.foreach((key, value) -> {
			System.out.println("================");
			System.out.println(value);
		});

		KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Shutting down stream");
			streams.close();
		}));
	}

	public void pushDummyDataToEmployeeTopic() {
		Employee employee1 = new Employee("Emp1", "Dept1", "100",0,0,0);
		Employee employee2 = new Employee("Emp2", "Dept2", "200",0,0,0);
		Employee employee3 = new Employee("Emp3", "Dept3", "300",0,0,0);
		kafkaProducer.sendMessageToEmployeeTopic(employee1);
		kafkaProducer.sendMessageToEmployeeTopic(employee2);
		kafkaProducer.sendMessageToEmployeeTopic(employee3);
		//kafkaProducer.sendMessage("test");
		//kafkaProducer.sendMessageWithCallback("message for callback ");
		//kafkaProducer.sendCustomizedMessage("Custom message");
	}

	public void pushMultipleEventsToTopic() {
		Event event1 = new Event(1,1);
		Event event2 = new Event(2,1);
		Event event3 = new Event(2,1);
		kafkaProducer.sendMessageToEventTopic(event1);
		kafkaProducer.sendMessageToEventTopic(event2);
		kafkaProducer.sendMessageToEventTopic(event2);

	}
}
