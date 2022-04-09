package kafkastreaming.consumer;

import java.text.SimpleDateFormat;

import kafkastreaming.model.Employee;
import kafkastreaming.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;



/**
 * The Kafka multiple consumer configuration involves following classes:
 * DefaultKafkaConsumerFactory : is used to create new Consumer instances where
 * all consumer share common configuration properties mentioned in this bean.
 * ConcurrentKafkaListenerContainerFactory : is used to build
 * ConcurrentMessageListenerContainer. This factory is primarily for building
 * containers for @KafkaListener annotated methods. ConsumerConfig : holds the
 * consumer configuration keys. KafkaListener : marks a method to be the target
 * of a Kafka message listener on the specified topics
 * 
 *
 */
@Component
public class KafkaConsumer {

	Logger logger = LoggerFactory.getLogger(KafkaConsumer.class.getName());
	
	@Autowired
	KafkaProducer kafkaProducer;

	SimpleDateFormat sdf;

	// @KafkaListener(topics = "${target.topic}",
	// groupId="${spring.kafka.consumer.group-id}")
	//@KafkaListener(topics = "${target.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
	// @KafkaListener(topics = "${target.topic}",
	// groupId="${spring.kafka.consumer.group-id}",containerFactory="secondaryKafkaListenerContainerFactory")
	public void consume(String message) {
		logger.info(
				"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Consumed message with consumerFactory: " + message);
	}

	//@KafkaListener(topics = "${target.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "secondaryKafkaListenerContainerFactory")
	public void consumeSecondary(String message) {
		logger.info("*****  Consumed message with secondary consumerFactory: " + message);
	}

	@KafkaListener(topics = "${target.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")

	public void consumeAsObject(@Payload(required=false) Employee employee) {

		logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Consumed employee: " + employee);
		if(employee.getRetryCount()==0)
		{
			employee.setRetryCount(employee.getRetryCount()+1);
			kafkaProducer.sendFailedMessage(employee);
			logger.warn("Detected failure ! " + employee);
		}
	}

}
