package com.train.test.services;

public interface InstanceErrorService {

    String testInstanceErrorFlowOne();

    String testInstanceErrorCancelFlow();

    String testInstanceErrorOneService(String serviceName , int replicasNum);
}
