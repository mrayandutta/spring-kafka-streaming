package kafkastreaming.util;

import kafkastreaming.model.event.Event;
import kafkastreaming.type.EventAggregate;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RollupAggregator implements Aggregator<String, Event, EventAggregate> {


    @Override
    public EventAggregate apply(String key, Event event, EventAggregate eventAggregate) {
        eventAggregate.setId(event.getId());
        Integer sum = eventAggregate.getSum();
        if(sum==null)
        {
            sum = 0;
        }
        eventAggregate.setSum(sum+event.getValue());
        return eventAggregate;
    }
}
