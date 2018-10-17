package com.train.test.services;

import com.train.test.bean.TestResponse;

import java.io.IOException;

public interface TestCaseService {
    public void startCopyTestCaseToHdfs();
    public String stopCollectResourceData();
    public TestResponse testBookErrorConfig() throws IOException, ClassNotFoundException;
    public TestResponse testFlowOne() throws IOException, ClassNotFoundException;
    public TestResponse testFlowTwoPay() throws IOException, ClassNotFoundException;
    public TestResponse testFlowTwoRebook() throws IOException, ClassNotFoundException;
    public TestResponse testServiceFood() throws IOException, ClassNotFoundException;
    public TestResponse testFlowFour() throws IOException, ClassNotFoundException;
    public TestResponse testServiceSso() throws IOException, ClassNotFoundException;
    public TestResponse testServiceRoute() throws IOException, ClassNotFoundException;
    public TestResponse testServiceVoucher() throws IOException, ClassNotFoundException;
}
