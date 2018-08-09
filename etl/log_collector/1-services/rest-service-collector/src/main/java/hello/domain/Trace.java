package hello.domain;

import java.util.ArrayList;

public class Trace {

    private String traceId;

    private String id;

    private String name;

    private long timestamp;

    private long duration;

    private ArrayList<Annotation> annotations;

    private ArrayList<BinaryAnnotation> binaryAnnotations;

    public Trace() {
        //Empty Constructor
    }

    public Trace(String traceId, String id, String name, long timestamp, long duration, ArrayList<Annotation> annotations, ArrayList<BinaryAnnotation> binaryAnnotations) {
        this.traceId = traceId;
        this.id = id;
        this.name = name;
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

    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }

    public ArrayList<BinaryAnnotation> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(ArrayList<BinaryAnnotation> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }
}
