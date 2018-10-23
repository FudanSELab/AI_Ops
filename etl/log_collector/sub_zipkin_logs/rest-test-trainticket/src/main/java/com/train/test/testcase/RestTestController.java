package com.train.test.testcase;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestTestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/testlogin")
    public void testLogin(){
        
    }
}
