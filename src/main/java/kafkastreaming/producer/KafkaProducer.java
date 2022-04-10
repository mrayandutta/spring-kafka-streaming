package kafkastreaming.producer;

import kafkastreaming.model.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import kafkastreaming.model.employee.Employee;
/**
 * 
 * 1.To enable the Kafka producer kafkaTemplate() and producerFactory() methods should be implemented in KafkaConfig class. 
 * 2.The kafkaTemplate() will return a new kafkaTemplate based on the configuration defined in producerFactory(). 
 * 3.This KafkaTemplate sends messages to Kafka topic 
 *
 */
@Component
public class KafkaProducer {
	
	Logger logger = LoggerFactory.getLogger(KafkaProducer.class.getName());

	@Autowired
	private KafkaTemplate<String, Employee> employeeKafkaTemplate;

	@Autowired
	private KafkaTemplate<String, Event> eventKafkaTemplate;

	@Value(value = "${target.topic}")
    private String employeeTopicName;

	@Value(value = "${event.topic}")
	private String eventTopicName;

	public void sendMessageToEmployeeTopic(Employee employee) {
		employeeKafkaTemplate.send(employeeTopicName, employee);
	    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  message {} sent to topic {}",employee,employeeTopicName);
	    
	}

	public void sendMessageToEventTopic(Event event) {
		eventKafkaTemplate.send(eventTopicName, event);
		logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  message {} sent to topic {}",event,eventTopicName);

	}
	


}
