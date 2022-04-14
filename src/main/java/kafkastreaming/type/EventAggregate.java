package kafkastreaming.type;

public class EventAggregate {
    private Integer id;
    private Integer sum ;

    public EventAggregate(Integer id, Integer sum) {
        this.id = id;
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "EventAggregate{" +
                "id=" + id +
                ", sum=" + sum +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
