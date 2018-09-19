package scenarioTest.model;

import org.testng.ITestNGListener;
import org.testng.TestNG;

public class TestTask implements Runnable {

    private String testCaseName;

    public TestTask(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    @Override
    public void run() {

        TestNG testng = new TestNG();
        try {
            testng.setTestClasses(new Class[] {Class.forName("test_case." + testCaseName)});
            TestFlowReportor tfr = new TestFlowReportor();
            testng.addListener((ITestNGListener) tfr);
            testng.setOutputDirectory("./test-output");
            testng.run();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
