package com.train.test.controller;

import com.train.test.domain.InitCacheDataResponse;
import com.train.test.entity.instance.FlowTestResult;
import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import com.train.test.services.ConfigErrorService;
import com.train.test.services.InstanceErrorService;
import com.train.test.utils.ArrangeInStanceNum;
import com.train.test.utils.BookingFlowPassServiceMapUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

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