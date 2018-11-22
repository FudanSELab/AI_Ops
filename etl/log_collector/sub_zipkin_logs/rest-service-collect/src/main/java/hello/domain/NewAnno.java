package hello.domain;

public class NewAnno {

    private long timestamp;

    private String value;

    private NewEndpoint endpoint;

    public NewAnno() {
    }

    public NewAnno(long timestamp, String value, NewEndpoint endpoint) {
        this.timestamp = timestamp;
        this.value = value;
        this.endpoint = endpoint;
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

    public NewEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(NewEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
