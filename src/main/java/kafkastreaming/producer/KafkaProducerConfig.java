package kafkastreaming.producer;

import java.util.HashMap;
import java.util.Map;

import kafkastreaming.model.employee.Employee;
import kafkastreaming.model.employee.EmployeeSerializer;
import kafkastreaming.model.event.Event;
import kafkastreaming.model.event.EventSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
	
	 @Bean
	 public ProducerFactory<String, Employee> employeeProducerFactory() {
	   final Map<String, Object> props = new HashMap<>(); 
	   props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); 
	   props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); 
	   props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); 
	   return new DefaultKafkaProducerFactory<>(props);
	 }

	@Bean
	public ProducerFactory<String, Event> eventProducerFactory() {
		final Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class);
		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	public KafkaTemplate<String, Event> eventKafkaTemplate() {
		return new KafkaTemplate<>(eventProducerFactory());
	}

	@Bean
	public KafkaTemplate<String, Employee> employeeKafkaTemplate() {
		return new KafkaTemplate<>(employeeProducerFactory());
	}
}
