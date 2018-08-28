package hello.domain;

public class NewAnnoation {

//    anno_sr_timestamp", "anno_sr", "anno_sr_servicename", "anno_sr_ip", "anno_sr_port

    private long timestamp;

    private String value;

    private String serviceName;

    private String ipv4;

    private int port;

    public NewAnnoation() {
        this.timestamp=0;
        this.value="";
        this.serviceName="";
        this.ipv4="";
        this.port=0;
    }

    public NewAnnoation(long timestamp, String value, String serviceName, String ipv4, int port) {
        this.timestamp = timestamp;
        this.value = value;
        this.serviceName = serviceName;
        this.ipv4 = ipv4;
        this.port = port;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
