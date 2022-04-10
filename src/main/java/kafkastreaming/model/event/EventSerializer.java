package kafkastreaming.model.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class EventSerializer implements Serializer {
	public void configure(Map map, boolean b) {
	}

	public void close() {
	}

	@Override
	public byte[] serialize(String arg0, Object arg1) {
		byte[] retVal = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retVal = objectMapper.writeValueAsString(arg1).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

}
