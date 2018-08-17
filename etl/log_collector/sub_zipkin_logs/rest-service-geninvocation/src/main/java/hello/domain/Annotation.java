package hello.domain;

public class Annotation {

    private long timestamp;

    private String value;

    private EndPoint endpoint;

    public Annotation() {
        //Empty Constructor
    }

    public Annotation(long timestamp, String value, EndPoint endPoint) {
        this.timestamp = timestamp;
        this.value = value;
        this.endpoint = endPoint;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EndPoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndPoint endpoint) {
        this.endpoint = endpoint;
    }
}
