package hello.domain;

public class Trace {

    private String traceId;

    private String id;

    private String name;

    private String parentId;

    private long timestamp;

    private long duration;

    private Annotation[] annotations;

    private BinaryAnnotation[] binaryAnnotations;

    public Trace() {
        //Empty Constructor
    }

    public Trace(String traceId, String id, String name, String parentId, long timestamp, long duration, Annotation[] annotations, BinaryAnnotation[] binaryAnnotations) {
        this.traceId = traceId;
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
        this.duration = duration;
        this.annotations = annotations;
        this.binaryAnnotations = binaryAnnotations;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public BinaryAnnotation[] getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(BinaryAnnotation[] binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }
}
