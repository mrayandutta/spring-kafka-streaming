package kafkastreaming.model.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafkastreaming.model.employee.Employee;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class EventDeserializer implements Deserializer {
	public void configure(Map map, boolean b) {
	}

	public void close() {
	}

	@Override
	public Event deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		Event object = null;
		try {
			object = mapper.readValue(arg1, Event.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return object;
	}

}
