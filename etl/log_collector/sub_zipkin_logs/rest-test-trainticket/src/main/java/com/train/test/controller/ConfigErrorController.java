package com.train.test.controller;

import com.train.test.services.ConfigErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigErrorController {

    @Autowired
    private ConfigErrorService configErrorService;

    @GetMapping("/errorTest/flowOne/{step}")
    public void testConfigError(@PathVariable int step) throws Exception {
        configErrorService.testConfigErrorFlowOne(step);
    }
}
