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

    private static final int TEST_COUNT = 100;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void testConfigErrorFlowOne(int step) throws Exception {
        String bookingFlowUrl = "http://localhost:10101/test/bookingflow";
        String url2 = "localhost:10101/cancelflow";
        String url3 = "localhost:10101/consignFlow";
        String url4 = "localhost:10101/voucherFlow";

        // get flow one passed services map
        Map<Integer, Map<Integer, String>> flowOnePassedServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();
        // get trace passed services with specific step
        Map<Integer, String> stepServiceMap = flowOnePassedServiceMap.get(step);
        // get the arrange list which key is order, value is list such as [1, 1, 1, 1, ...]
        HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(stepServiceMap.size());

        for (Map.Entry<Integer, List<Integer>> configCase : oneTraceAllArrangeList.entrySet()) {
            // get service names which need to be configured
            List<String> configServiceNames = getConfigServices(configCase.getValue(), stepServiceMap);
            // get the config requests
            List<NewSingleDeltaCMResourceRequest> configRequests = constructConfigRequests(configServiceNames, 0, false);

            boolean configResult;
            if (!configRequests.isEmpty()) {
                try {
                    configResult = setServiceResource(configRequests);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }

            } else { // if no service need to be configured, such as the first time to run
                configResult = true;
            }

            // execute test case
            if (configResult) {
                testBookingFlow(bookingFlowUrl);
            } else {
                logger.error("Set CPU or Memory failed!");
                Thread.sleep(300000);
                continue;
            }

            // reset the environment
            configRequests = constructConfigRequests(configServiceNames, 0, true);
            boolean resetResult = false;
            try {
                resetResult = setServiceResource(configRequests);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            // if not ready, sleep 5 min.
            if (!resetResult) {
                Thread.sleep(300000);
            }
        }
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
     * @param serviceNames service names which need to be configured
     * @param configType 0: cpu;  1: memory;  2: cpu and memory
     * @param isReset true: reset; false: set config
     * @return the constructed config requests
     */
    private List<NewSingleDeltaCMResourceRequest> constructConfigRequests(List<String> serviceNames, int configType, boolean isReset) {
        List<NewSingleDeltaCMResourceRequest> configRequests = new ArrayList<>();

        NewSingleDeltaCMResourceRequest resourceRequest;
        List<CMConfig> cmConfigs;
        CMConfig cmConfig;

        if (isReset) { // reset configurations
            switch (configType) {
                case 0: { // cpu
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("cpu", "6000m")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                case 1: {
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("memory", "15360Mi")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                case 2: {
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("cpu", "6000m"), new CM("memory", "15360Mi")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                default:
                    break;
            }
        } else { // set configurations
            switch (configType) {
                case 0: { // cpu
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("cpu", "200m")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                case 1: {
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("memory", "800Mi")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                case 2: {
                    for (String serviceName : serviceNames) {
                        resourceRequest = new NewSingleDeltaCMResourceRequest();
                        cmConfigs = new ArrayList<>();
                        cmConfig = new CMConfig();

                        resourceRequest.setServiceName(serviceName);
                        cmConfig.setType("limits");
                        cmConfig.setValues(Arrays.asList(new CM("cpu", "200m"), new CM("memory", "800Mi")));
                        cmConfigs.add(cmConfig);
                        resourceRequest.setConfigs(cmConfigs);
                        configRequests.add(resourceRequest);
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return configRequests;
    }

    private List<String> getConfigServices(List<Integer> caseOrders, Map<Integer, String> orderToServiceMap) {
        List<String> results = new ArrayList<>();

        for (int caseOrder : caseOrders) {

            if (2 == caseOrder && null != orderToServiceMap.get(caseOrders.indexOf(caseOrder))) {
                results.add(orderToServiceMap.get(caseOrders.indexOf(caseOrder)));
            }
        }

        return results;
    }
}
