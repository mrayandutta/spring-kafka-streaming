package kafkastreaming.model.employee;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EmployeeDeserializer implements Deserializer {
	public void configure(Map map, boolean b) {
	}

	public void close() {
	}

	@Override
	public Employee deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		Employee employee = null;
		try {
			employee = mapper.readValue(arg1, Employee.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return employee;
	}

}
