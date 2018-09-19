package scenarioTest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scenarioTest.service.ScenarioTestService;

@RestController("/scenario")
public class ScenarioTestController {

    @Autowired
    private ScenarioTestService scenarioTestService;

    @GetMapping("/login")
    public void testLogin() {
        scenarioTestService.testLogin();
    }

    @GetMapping("/shutdown")
    public String shutDownThreadPool() {
        return scenarioTestService.shutDownThreadPool();
    }

    @GetMapping("/flowOne")
    public void testFlowOne() {
        scenarioTestService.testFlowOne();
    }

    @GetMapping("/cancelTicket")
    public void testCancelTicket() {
        scenarioTestService.testCancelTicket();
    }

    @GetMapping("/getCurrentTaskNumer")
    public String getCurrentTaskNumer() {
        return scenarioTestService.getCurrentTaskNumber();
    }
}
