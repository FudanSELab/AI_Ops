package hello.domain;

public class EndPoint {

    private String serviceName;

    private String ipv4;

    private int port;

    public EndPoint() {
        //Empty Constructor
    }

    public EndPoint(String serviceName, String ipv4, int port) {
        this.serviceName = serviceName;
        this.ipv4 = ipv4;
        this.port = port;
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
