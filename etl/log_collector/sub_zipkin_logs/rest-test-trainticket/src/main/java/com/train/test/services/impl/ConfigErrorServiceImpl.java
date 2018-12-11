package com.train.test.services.impl;

import com.train.test.entity.instance.FlowTestResult;
import com.train.test.services.ConfigErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConfigErrorServiceImpl implements ConfigErrorService {

    private static final int TEST_COUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;


    public void testConfigError() {
        String url1 = "localhost:10101/bookingflow";
        String url2 = "localhost:10101/cancelflow";
        String url3 = "localhost:10101/consignFlow";
        String url4 = "localhost:10101/voucherFlow";



    }


    private void testBookingFlow(String url) {

        for (int i = 0; i < TEST_COUNT; i++) {
            try {
                restTemplate.getForObject(url, FlowTestResult.class);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }


    private void setServiceResource() {

    }
}
