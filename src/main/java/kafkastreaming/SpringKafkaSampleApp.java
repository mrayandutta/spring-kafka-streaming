package kafkastreaming;

import java.util.Properties;

import kafkastreaming.consumer.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kafkastreaming.model.event.Event;
import kafkastreaming.producer.KafkaProducer;

@SpringBootApplication
public class SpringKafkaSampleApp implements CommandLineRunner
{
	private static Logger logger = LoggerFactory.getLogger(SpringKafkaSampleApp.class.getName());
	@Autowired
	EventConsumer eventConsumer;
	@Autowired
	KafkaProducer kafkaProducer;


	public static void main(String[] args) {
		logger.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringKafkaSampleApp.class, args);
		logger.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		pushMultipleEventsToTopic();
		eventConsumer.processEventStreams();
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
