package kafkastreaming.model.event;

public class Event {
    private Integer id;
    private Integer value ;

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

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
