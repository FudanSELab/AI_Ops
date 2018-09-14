package scenarioTest.service;

public interface ScenarioTestService {
    void testLogin();
    String shutDownThreadPool();
    void testFlowOne();
    void testCancelTicket();
    String getCurrentTaskNumber();
}
