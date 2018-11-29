package com.train.test.entity.instance;

import java.io.Serializable;
import java.util.List;

public class FlowTestResult implements Serializable {


    private static final long serialVersionUID = 1513969851023473982L;

    private TestCase testCase;
    private List<TestTrace> testTraces;

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public List<TestTrace> getTestTraces() {
        return testTraces;
    }

    public void setTestTraces(List<TestTrace> testTraces) {
        this.testTraces = testTraces;
    }
}
