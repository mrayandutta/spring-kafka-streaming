package kafkastreaming.consumer;

import kafkastreaming.model.employee.Employee;
import kafkastreaming.model.employee.EmployeeDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
	Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class.getName());
	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value(value = "${spring.profiles.active}")
	private String activeProfile;

	@Value(value = "${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	private static final String SASL_PROTOCOL = "SASL_SSL";
	private static final String SCRAM_SHA_256 = "SCRAM-SHA-256";
	private final String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
	private final String prodJaasCfg = String.format(jaasTemplate, "cyy3wd7r", "eAQPX5G290PkZ5CKo6drJKvqHqO6FA66");

	@Bean
	public ConsumerFactory<String, Employee> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		// allows a pool of processes to divide the work of consuming and processing
		// records
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		// automatically reset the offset to the earliest offset
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		if (activeProfile.equalsIgnoreCase("remote")) {
			props = addRemoteSpecificProperties(props);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		}

		if (activeProfile.equalsIgnoreCase("local")) {
			props.put("value.deserializer", EmployeeDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class);
		}

		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConsumerFactory<String, Employee> secondaryConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// allows a pool of processes to divide the work of consuming and processing
		// records
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		// automatically reset the offset to the earliest offset
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		if (activeProfile.equalsIgnoreCase("remote")) {
			props = addRemoteSpecificProperties(props);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		}
		if (activeProfile.equalsIgnoreCase("local")) {
			props.put("value.deserializer", EmployeeDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class);
		}
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Employee> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Employee> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Employee> secondaryKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Employee> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(secondaryConsumerFactory());
		return factory;
	}

	private Map<String, Object> addRemoteSpecificProperties(Map<String, Object> props) {
		props.put("sasl.mechanism", SCRAM_SHA_256);
		props.put("sasl.jaas.config", prodJaasCfg);
		props.put("security.protocol", SASL_PROTOCOL);
		return props;
	}

}
