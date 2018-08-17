package hello.domain;

public class Anno1 {

    private String traceId;

    private String spanId;

    private String spanName;

    private String spanParentId;

    private long spanTimeStamp;

    private long spanDuration;

    private long anno_timeStamp;

    private String anno_value;

    private String anno_serviceName;

    private String ipv4;

    private int anno_port;

    public Anno1() {
    }

    public Anno1(String traceId, String spanId, String spanName, String spanParentId, long spanTimeStamp, long spanDuration, long anno_timeStamp, String anno_value, String anno_serviceName, String ipv4, int anno_port) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.spanName = spanName;
        this.spanParentId = spanParentId;
        this.spanTimeStamp = spanTimeStamp;
        this.spanDuration = spanDuration;
        this.anno_timeStamp = anno_timeStamp;
        this.anno_value = anno_value;
        this.anno_serviceName = anno_serviceName;
        this.ipv4 = ipv4;
        this.anno_port = anno_port;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getSpanName() {
        return spanName;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public String getSpanParentId() {
        return spanParentId;
    }

    public void setSpanParentId(String spanParentId) {
        this.spanParentId = spanParentId;
    }

    public long getSpanTimeStamp() {
        return spanTimeStamp;
    }

    public void setSpanTimeStamp(long spanTimeStamp) {
        this.spanTimeStamp = spanTimeStamp;
    }

    public long getSpanDuration() {
        return spanDuration;
    }

    public void setSpanDuration(long spanDuration) {
        this.spanDuration = spanDuration;
    }

    public long getAnno_timeStamp() {
        return anno_timeStamp;
    }

    public void setAnno_timeStamp(long anno_timeStamp) {
        this.anno_timeStamp = anno_timeStamp;
    }

    public String getAnno_value() {
        return anno_value;
    }

    public void setAnno_value(String anno_value) {
        this.anno_value = anno_value;
    }

    public String getAnno_serviceName() {
        return anno_serviceName;
    }

    public void setAnno_serviceName(String anno_serviceName) {
        this.anno_serviceName = anno_serviceName;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public int getAnno_port() {
        return anno_port;
    }

    public void setAnno_port(int anno_port) {
        this.anno_port = anno_port;
    }
}
