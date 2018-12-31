package com.train.test.services.impl;

import com.train.test.entity.config.*;
import com.train.test.entity.instance.FlowTestResult;
import com.train.test.services.ConfigErrorService;
import com.train.test.utils.ArrangeInStanceNum;
import com.train.test.utils.BookingFlowPassServiceMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ConfigErrorServiceImpl implements ConfigErrorService {

    private static final int TEST_COUNT = 50;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void testConfigErrorFlowOne(int resourceType) throws Exception {
        String bookingFlowUrl = "http://localhost:10101/test/bookingflow";
        String url2 = "localhost:10101/cancelflow";
        String url3 = "localhost:10101/consignFlow";
        String url4 = "localhost:10101/voucherFlow";

        // get flow one passed services map
        Map<Integer, Map<Integer, String>> flowOnePassedServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();

        for (int i = 0; i < flowOnePassedServiceMap.size(); i++) {

            // get trace passed services with specific step
            // Map<Integer, String> stepServiceMap = flowOnePassedServiceMap.get(step);
            Map<Integer, String> stepServiceMap = flowOnePassedServiceMap.get(i);

            // get the arrange list which key is order, value is list such as [1, 1, 1, 1, ...]
            HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(stepServiceMap.size());

            for (int j = 0; j < oneTraceAllArrangeList.size(); j++) {

                System.out.println("In step " + i + ", the " + j + " arrange list is: " + oneTraceAllArrangeList.get(j));

                // get service names which need to be configured
                Map<String, Integer> configServiceNames = getConfigServices(oneTraceAllArrangeList.get(j), stepServiceMap);

                // get the config requests
                List<NewSingleDeltaCMResourceRequest> configRequests = constructConfigRequests(configServiceNames, resourceType);

                boolean configResult;
                try {
                    configResult = setServiceResource(configRequests);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }


                // execute test case
                if (configResult) {
                    Thread.sleep(60000);
                    testBookingFlow(bookingFlowUrl);
                    Thread.sleep(60000);
                } else {
                    logger.error("Set CPU or Memory failed!");
                    Thread.sleep(300000);
                    continue;
                }

                if (j == oneTraceAllArrangeList.size() - 1) { // the last time to configure in this trace, need to configure the biggest resource to the error service
                    List<String> errorServices = BookingFlowPassServiceMapUtil.getErrorServiceInFlowOne().get(i);

                    configServiceNames = getResetConfigServices(errorServices, new ArrayList<>(stepServiceMap.values()));
                    configRequests = constructConfigRequests(configServiceNames, resourceType);

                    try {
                        configResult = setServiceResource(configRequests);

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                    if (configResult) {
                        System.out.println("Step " + i + " is completed!");
                        Thread.sleep(60000);
                    } else {
                        logger.error("Set CPU or Memory failed!");
                        Thread.sleep(300000);
                    }
                }

            }
        }

    }


    private void testBookingFlow(String url) {

        for (int i = 0; i < TEST_COUNT; i++) {
            try {
                restTemplate.getForObject(url, FlowTestResult.class);
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }


    private boolean setServiceResource(List<NewSingleDeltaCMResourceRequest> configRequests) throws Exception {
        DeltaCMResourceRequest request = new DeltaCMResourceRequest();
        request.setClusterName("cluster2");
        request.setDeltaRequests(configRequests);

        String url = "http://10.141.212.21:18898/api/deltaCMResource";
        HttpEntity requestBody = new HttpEntity<>(request);
        ResponseEntity<DeltaCMResourceResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestBody, DeltaCMResourceResponse.class);

        return null != responseEntity && responseEntity.getBody().isStatus();
    }

    /**
     *
     * @param serviceConfigMap service names which need to be configured
     * @param resourceType 0: cpu;  1: memory;  2: cpu and memory
     * @return the constructed config requests
     */
    private List<NewSingleDeltaCMResourceRequest> constructConfigRequests(Map<String, Integer> serviceConfigMap, int resourceType) {
        List<NewSingleDeltaCMResourceRequest> configRequests = new ArrayList<>();

        NewSingleDeltaCMResourceRequest resourceRequest;
        List<CMConfig> cmConfigs;
        CMConfig cmConfig;

        switch (resourceType) {
            case 0:
            case 1: { // cpu or memory
                for (Map.Entry<String, Integer> serviceConfigEntry : serviceConfigMap.entrySet()) {
                    resourceRequest = new NewSingleDeltaCMResourceRequest();
                    cmConfigs = new ArrayList<>();
                    cmConfig = new CMConfig();

                    resourceRequest.setServiceName(serviceConfigEntry.getKey());
                    cmConfig.setType("limits");
                    CM memory = getCMByArrangeValue(serviceConfigEntry.getValue(), resourceType);
                    cmConfig.setValues(Arrays.asList(memory));
                    cmConfigs.add(cmConfig);
                    resourceRequest.setConfigs(cmConfigs);
                    configRequests.add(resourceRequest);
                }
                break;
            }
            case 2: { // cpu and memory
                for (Map.Entry<String, Integer> serviceConfigEntry : serviceConfigMap.entrySet()) {
                    resourceRequest = new NewSingleDeltaCMResourceRequest();
                    cmConfigs = new ArrayList<>();
                    cmConfig = new CMConfig();

                    resourceRequest.setServiceName(serviceConfigEntry.getKey());
                    cmConfig.setType("limits");
                    CM cpu = getCMByArrangeValue(serviceConfigEntry.getValue(), 0);
                    CM memory = getCMByArrangeValue(serviceConfigEntry.getValue(), 1);
                    cmConfig.setValues(Arrays.asList(cpu, memory));
                    cmConfigs.add(cmConfig);
                    resourceRequest.setConfigs(cmConfigs);
                    configRequests.add(resourceRequest);
                }
                break;
            }
            default:
                break;
        }
        return configRequests;
    }



    /**
     *
     * @param caseOrders the arrange list, such as [1, 1, 2, 1 ...]
     * @param orderToServiceMap the order of service in step, key is order, value is service name
     * @return service config map, key is service name, value is 1 or 2, 1 -> don't configure, 2 -> configure
     */
    private Map<String, Integer>  getConfigServices(List<Integer> caseOrders, Map<Integer, String> orderToServiceMap) {
        Map<String, Integer> results = new HashMap<>();

        for (int i = 0; i< caseOrders.size(); i++) {
            results.put(orderToServiceMap.get(i), caseOrders.get(i));
        }

        return results;
    }

    private Map<String, Integer> getResetConfigServices(List<String> errorServices,  List<String> stepServices) {
        Map<String, Integer> resetConfigServices = new HashMap<>();

        for (String service : stepServices) {
            if (errorServices.contains(service)) {
                resetConfigServices.put(service, 1);
            }
            else {
                resetConfigServices.put(service, 0);
            }
        }

        return resetConfigServices;
    }

    private CM getCMByArrangeValue(int arrangeValue, int resourceType) {
        switch (resourceType) {
            case 0: { //cpu
                switch (arrangeValue) {
                    case 0: {
                        return new CM("cpu", "400m");
                    }
                    case 1: {
                        return new CM("cpu", "1000m");
                    }
                    case 2: {
                        return new CM("cpu", "200m");
                    }
                    default:{
                        return new CM("cpu", "400m");
                    }
                }
            }
            case 1: {
                switch (arrangeValue) {
                    case 0: {
                        return new CM("memory", "500Mi");
                    }
                    case 1: {
                        return new CM("memory", "1000Mi");
                    }
                    case 2: {
                        return new CM("memory", "800Mi");
                    }
                    default:{
                        return new CM("memory", "500Mi");
                    }
                }
            }
            default:
                return null;
        }

    }
}
