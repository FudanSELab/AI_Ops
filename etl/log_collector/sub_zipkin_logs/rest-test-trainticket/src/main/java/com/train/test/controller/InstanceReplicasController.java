package com.train.test.controller;

import com.train.test.services.InstanceErrorService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class InstanceReplicasController {

    @Autowired
    private InstanceErrorService instanceErrorService;

    @RequestMapping(value = "/testAllFlow")
    public String testAllFlow() {
        instanceErrorService.testInstanceErrorFlowOne();
        instanceErrorService.testInstanceErrorCancelFlow();
        return "all OVER";
    }

    @RequestMapping(value = "/testInstanceErrorFlowOne")
    public String testInstanceErrorFlowOne() {
        return instanceErrorService.testInstanceErrorFlowOne();
    }

    @RequestMapping(value = "/testInstanceErrorCancelFlow")
    public String testInstanceErrorCancelFlow() {
        return instanceErrorService.testInstanceErrorCancelFlow();
    }


    @RequestMapping(value = "/testOneService")
    public String testOneService() {
        return instanceErrorService.testInstanceErrorOneService("ts-login-service", 2);
    }
}