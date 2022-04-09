package kafkastreaming.producer;

import java.util.HashMap;
import java.util.Map;

import kafkastreaming.model.Employee;
import kafkastreaming.model.EmployeeSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;




@Configuration
public class KafkaProducerConfig {
	
	@Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
	
	@Value(value = "${spring.profiles.active}")
	private String activeProfile;
	
	//private static final String TRUSTSTORE_JKS = "/var/private/ssl/kafka.client.truststore.jks"; 
	private static final String SASL_PROTOCOL = "SASL_SSL"; 
	private static final String SCRAM_SHA_256 = "SCRAM-SHA-256"; 
	private final String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";"; 
	private final String prodJaasCfg = String.format(jaasTemplate, "cyy3wd7r", "eAQPX5G290PkZ5CKo6drJKvqHqO6FA66");

	 @Bean 
	 //public ProducerFactory<String, String> producerFactory() { 
	 public ProducerFactory<String, Employee> producerFactory() {
	   final Map<String, Object> props = new HashMap<>(); 
	   props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); 
	   props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); 
	   props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); 
	   
	   
	   if(activeProfile.equalsIgnoreCase("remote"))
		{
		   props.put("sasl.mechanism", SCRAM_SHA_256);
		   props.put("sasl.jaas.config", prodJaasCfg);
		   props.put("security.protocol", SASL_PROTOCOL);
		}
	   
	   if(activeProfile.equalsIgnoreCase("local"))
		{
		   props.put("value.serializer", EmployeeSerializer.class.getName());
		}
	   return new DefaultKafkaProducerFactory<>(props); 
	 }
	
	@Bean
	//public KafkaTemplate<String, String> kafkaTemplate() {
	public KafkaTemplate<String, Employee> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
