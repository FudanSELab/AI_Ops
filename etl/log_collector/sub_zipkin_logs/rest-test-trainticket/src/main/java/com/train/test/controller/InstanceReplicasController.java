package com.train.test.controller;


import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class InstanceReplicasController {


    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/hello")
    public String testHello() {
        return "hello instance";
    }

    @RequestMapping(value = "/changeInstanceAndRun")
    public String changeInstanceAndRun() {
        // 调 interface
        String url = "http://10.141.212.140:18898/api/setReplicas";
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        List<ServiceReplicasSetting> srsList = new ArrayList<>();
        srsList.add(new ServiceReplicasSetting("ts-ticket-office-service", 2));
        ssrrDto.setClusterName("cluster1");
        ssrrDto.setServiceReplicasSettings(srsList);

        SetServiceReplicasResponse srs =
                restTemplate.postForObject(url, ssrrDto, SetServiceReplicasResponse.class);

        if (srs.isStatus()) {

        }
        // 跑 case
        return "";
    }


}
