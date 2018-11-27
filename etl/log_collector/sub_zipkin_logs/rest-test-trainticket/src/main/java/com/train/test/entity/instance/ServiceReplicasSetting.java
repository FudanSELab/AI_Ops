package com.train.test.entity.instance;

public class ServiceReplicasSetting {

    private String serviceName;
    private int numOfReplicas;

    public ServiceReplicasSetting() {
    }

    public ServiceReplicasSetting(String serviceName, int numOfReplicas) {
        this.serviceName = serviceName;
        this.numOfReplicas = numOfReplicas;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getNumOfReplicas() {
        return numOfReplicas;
    }

    public void setNumOfReplicas(int numOfReplicas) {
        this.numOfReplicas = numOfReplicas;
    }
}
