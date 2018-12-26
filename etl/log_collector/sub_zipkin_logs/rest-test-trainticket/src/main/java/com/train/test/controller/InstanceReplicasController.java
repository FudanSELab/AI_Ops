package com.train.test.controller;

import com.train.test.services.InstanceErrorService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class InstanceReplicasController {

    @Autowired
    private InstanceErrorService instanceErrorService;

    @RequestMapping(value = "/changeInstanceAndRun")
    public String changeInstanceAndRun() {
        return instanceErrorService.testInstanceErrorFlowOne();
    }

    @RequestMapping(value = "/testOneService")
    public String testOneService() {
        return instanceErrorService.testInstanceErrorOneService("ts-login-service", 2);
    }

}