package com.train.test.entity.instance;

import java.util.List;

public class SetServiceReplicasRequest {
    private String clusterName;

    private List<ServiceReplicasSetting> serviceReplicasSettings;

    public SetServiceReplicasRequest() {
    }

    public SetServiceReplicasRequest(String clusterName, List<ServiceReplicasSetting> serviceReplicasSettings) {
        this.clusterName = clusterName;
        this.serviceReplicasSettings = serviceReplicasSettings;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<ServiceReplicasSetting> getServiceReplicasSettings() {
        return serviceReplicasSettings;
    }

    public void setServiceReplicasSettings(List<ServiceReplicasSetting> serviceReplicasSettings) {
        this.serviceReplicasSettings = serviceReplicasSettings;
    }
}
