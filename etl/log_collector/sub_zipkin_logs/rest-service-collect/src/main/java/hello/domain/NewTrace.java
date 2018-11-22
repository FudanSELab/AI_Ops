package hello.domain;

public class NewTrace {
    private String traceId;

    private String name;

    private String id;

    private String parentId;

    private long timestamp;

    private long duration;

    private NewAnno[] annotations;

    private NewBnno[] binaryAnnotations;

    public NewTrace() {
    }

    public NewTrace(String traceId, String name, String id, String parentId, long timestamp, long duration, NewAnno[] annotations, NewBnno[] binaryAnnotations) {
        this.traceId = traceId;
        this.name = name;
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public NewAnno[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(NewAnno[] annotations) {
        this.annotations = annotations;
    }

    public NewBnno[] getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(NewBnno[] binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }
}
