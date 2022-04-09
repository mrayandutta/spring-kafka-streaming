package kafkastreaming.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import kafkastreaming.model.Employee;
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
	//private final KafkaTemplate<String, String> kafkaTemplate;
	private final KafkaTemplate<String, Employee> kafkaTemplate;
	
	@Value(value = "${target.topic}")
    private String topicName;
	
	@Value(value = "${retry.topic}")
    private String retryTopicName;
	
	//KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
	KafkaProducer(KafkaTemplate<String, Employee> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
	
	public void send(Employee employee) {
	    kafkaTemplate.send(topicName, employee);
	    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  message {} sent to topic {}",employee,topicName);
	    
	}
	
	public void sendFailedMessage(Employee employee) {
	    kafkaTemplate.send(retryTopicName, employee);
	    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  message {} sent to topic {}",employee,topicName);
	    
	}

	/*
	
	public void sendMessage(String msg) {
	    kafkaTemplate.send(topicName, msg);
	    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  message {} sent to topic {}",msg,topicName);
	    
	}
	
	
	public void sendMessageWithCallback(String message) {
        
	    ListenableFuture<SendResult<String, String>> future = 
	      kafkaTemplate.send(topicName, message);
		
	    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

	        @Override
	        public void onSuccess(SendResult<String, String> result) {
	        	logger.info("Sent message=[{}] with offset=[{}] to partition {} ",message,result.getRecordMetadata().offset(),result.getRecordMetadata().partition());
	        }
	        @Override
	        public void onFailure(Throwable ex) {
	        	logger.info("Unable to send message=[{} ] due to : {}",message,ex.getMessage());
	        }
	    });
	}
	*/
	/**
	 * Payload : the message to be pushed to Kafka broker
	 * Topic : the topic name where sent message will be stored in Kafka broker
	 * Partition_ID : if given topic have multiple partition sender has to mention the partition id of the topic.
	 */
	public void sendCustomizedMessage(String message) {
	    Message<String> customMessage = MessageBuilder
	            .withPayload(message)
	            .setHeader(KafkaHeaders.TOPIC, topicName)
	            .setHeader(KafkaHeaders.PARTITION_ID, 1)
	            .build();
	    this.kafkaTemplate.send(customMessage);
	}

}
