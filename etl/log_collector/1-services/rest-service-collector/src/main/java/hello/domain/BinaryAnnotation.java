package hello.domain;

public class BinaryAnnotation {

    private String key;

    private String value;

    private EndPoint endPoint;

    public BinaryAnnotation() {
        //Empty Constructor
    }

    public BinaryAnnotation(String key, String value, EndPoint endPoint) {
        this.key = key;
        this.value = value;
        this.endPoint = endPoint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPoint endPoint) {
        this.endPoint = endPoint;
    }
}
