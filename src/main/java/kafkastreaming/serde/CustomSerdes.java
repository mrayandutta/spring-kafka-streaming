package kafkastreaming.serde;

import kafkastreaming.model.employee.Employee;
import kafkastreaming.model.event.Event;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
public final class CustomSerdes {
    private CustomSerdes() {}
    public static Serde<Event> Event() {
        JsonSerializer<Event> serializer = new JsonSerializer<>();
        JsonDeserializer<Event> deserializer = new JsonDeserializer<>(Event.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Employee> Employee() {
        JsonSerializer<Employee> serializer = new JsonSerializer<>();
        JsonDeserializer<Employee> deserializer = new JsonDeserializer<>(Employee.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}