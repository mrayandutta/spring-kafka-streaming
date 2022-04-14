package kafkastreaming.model.event;

import java.util.Date;

public class Event {
    private Integer id;
    private Integer value ;
    private Date evenTime;

    private Event() {
    }

    public Event(Integer id, Integer value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getEvenTime() {
        return evenTime;
    }

    public void setEvenTime(Date evenTime) {
        this.evenTime = evenTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
