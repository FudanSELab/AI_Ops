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

    
}
