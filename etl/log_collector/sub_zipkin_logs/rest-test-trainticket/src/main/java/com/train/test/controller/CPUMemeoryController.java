package com.train.test.controller;

import com.train.test.services.RestCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CPUMemeoryController {


    @Autowired
    private RestCollectService restCollectService;

    @GetMapping("/getCpuMemoryData")
    public String getResourceData() {
        // get cpu and memory
        System.out.println("-=========================================---------------");
        restCollectService.getResourceData();
        return "[GET CPU AND MEMORY]";
    }

    @GetMapping("/stopCpuMemoryData")
    public String stopCollectResourceData() {
        return restCollectService.stopCollectResourceData();
    }

}
