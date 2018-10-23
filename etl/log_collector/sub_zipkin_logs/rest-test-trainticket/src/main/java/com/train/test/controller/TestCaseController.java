package com.train.test.controller;

import com.train.test.bean.TestFlowReporter;
import com.train.test.bean.TestResponse;
import com.train.test.csvutil.CsvFileTest;
import com.train.test.domain.CheckResult;
import com.train.test.domain.Testcase;
import com.train.test.services.TestCaseService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.testng.ITestNGListener;
import org.testng.TestNG;

import java.io.IOException;

@RestController
public class TestCaseController {


    @Autowired
    private TestCaseService testCaseService;

    @GetMapping("/stopCopyTestCaseToHdfs")
    public String stopCollectResourceData() {
        return testCaseService.stopCollectResourceData();
    }

    @GetMapping("/startCopyTestCaseToHdfs")
    public void startCopyTestCaseToHdfs() {
         testCaseService.startCopyTestCaseToHdfs();
    }

    @GetMapping("/testBookError")
    public TestResponse testBookErrorConfig() throws ClassNotFoundException, IOException {
        return testCaseService.testBookErrorConfig();
    }

    @GetMapping("/testFlowOne")
    public TestResponse testFlowOne() throws ClassNotFoundException, IOException {
        return testCaseService.testFlowOne();
    }

    @GetMapping("/testFlowTwoPay")
    public TestResponse testFlowTwoPay() throws ClassNotFoundException, IOException {
        return testCaseService.testFlowTwoPay();
    }

    @GetMapping("/testFlowTwoRebook")
    public TestResponse testFlowTwoRebook() throws ClassNotFoundException, IOException {
        return testCaseService.testFlowTwoRebook();
    }

    @GetMapping("/testServiceFood")
    public TestResponse testServiceFood() throws ClassNotFoundException, IOException {
        return testCaseService.testServiceFood();
    }

    @GetMapping("/testFlowFour")
    public TestResponse testFlowFour() throws ClassNotFoundException, IOException {
        return testCaseService.testFlowFour();
    }
    @GetMapping("/testServiceSso")
    public TestResponse testServiceSso() throws IOException, ClassNotFoundException {
        return testCaseService.testServiceSso();
    }

    @GetMapping("/testServiceRoute")
    public TestResponse testServiceRoute() throws IOException, ClassNotFoundException {
        return testCaseService.testServiceRoute();
    }
}
