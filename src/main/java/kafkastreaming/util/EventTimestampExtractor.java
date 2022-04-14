package kafkastreaming.util;

import kafkastreaming.model.event.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class EventTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long prevTime) {
        Event event = (Event) record.value();
        return 0;
    }
}
