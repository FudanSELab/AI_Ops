package com.train.test.services.impl;

import com.train.test.csvutil.CSVUtils;
import com.train.test.queue.MsgSender;
import com.train.test.services.RestCollectService;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RestCollectServiceImpl implements RestCollectService {

    private static final String STATUS = "status";
    private static final String SERVICES = "services";
    private static final String SERVICE_NAME = "serviceName";
    private static final String REQUESTS = "requests";
    private static final String LIMITS = "limits";
    private static final String CONF_NUMBER = "confNumber";
    private static final String READY_NUMBER = "readyNumber";
    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";
    private static final String REQUEST_CPU = "r_cpu";
    private static final String REQUEST_MEMORY = "r_memory";
    private static final String LIMIT_CPU = "l_cpu";
    private static final String LIMIT_MEMORY = "l_memory";
    private static final String SERVICE_CONFIG_DATA = "serviceConfigData_instance";
    private static final String SERVICE_INSTANCE_DATA = "serviceInstanceData_instance";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String HEALTH_CHECK_READY_DELAY = "healthCheckReadyDelay";
    private static final String HEALTH_CHECK_DOWN_DELAY = "healthCheckDownDelay";

    private Boolean flag = true;

    @Autowired
    private MsgSender msgSender;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void getResourceData() {

        while (flag) {

            try {
                msgSender.sendLoginInfoToSso(System.currentTimeMillis());

                Thread.sleep(30000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public String stopCollectResourceData() {
        flag = false;

        return "Stop collecting resource data succeed!";
    }

    public void getCpuMemoryLogInReceiver(long requestTime) {

        boolean configDataFlag = true;
        boolean instanceDataFlag = true;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // request to get service config data
        LinkedHashMap<String, Object> responseServiceData =
                restTemplate.getForObject("http://10.141.212.140:18898/api/getServicesAndConfig/cluster1", LinkedHashMap.class);
        // request to get service pod data
        LinkedHashMap<String, Object> responsePodData =
                restTemplate.getForObject("http://10.141.212.140:18898/api/podMetrics/cluster1", LinkedHashMap.class);
        // request to get node data
        LinkedHashMap<String, Object> responseNodeData =
                restTemplate.getForObject("http://10.141.212.140:18898/api/nodeMetrics/cluster1", LinkedHashMap.class);


        try {
            long responseTime = System.currentTimeMillis();

            if (MapUtils.isNotEmpty(responseServiceData) && MapUtils.isNotEmpty(responsePodData) && MapUtils.isNotEmpty(responseNodeData)) {

                //System.out.println("Request time: " + dateFormat.format(requestTime));
               // System.out.println("Response time: " + dateFormat.format(responseTime));

                if (responseServiceData.get(STATUS).toString().equals(Boolean.TRUE.toString())) {
                    List<HashMap<String, Object>> servicesData = (List<HashMap<String, Object>>) responseServiceData.get(SERVICES);
                    createCSVTableAndWriteHDFSFile(constructServiceData(servicesData, requestTime, responseTime), SERVICE_CONFIG_DATA, configDataFlag);
                }

                if (responseNodeData.get(STATUS).toString().equals(Boolean.TRUE.toString())
                        && responsePodData.get(STATUS).toString().equals(Boolean.TRUE.toString())) {
                    List<HashMap<String, Object>> podsData = (List<HashMap<String, Object>>) responsePodData.get("podsMetrics");
                    List<HashMap<String, Object>> nodesData = (List<HashMap<String, Object>>) responseNodeData.get("nodesMetrics");
                    LinkedList<LinkedHashMap<String, String>> serviceInstanceData = new LinkedList<>();
                    constructServiceInstanceData(podsData, nodesData, serviceInstanceData, requestTime, responseTime);
                    createCSVTableAndWriteHDFSFile(serviceInstanceData, SERVICE_INSTANCE_DATA, instanceDataFlag);
                }

             //   System.out.println("End write time: " + dateFormat.format(System.currentTimeMillis()));
            } else {
                throw new Exception("Get Service/Pod/Node data failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void constructServiceInstanceData(List<HashMap<String, Object>> podsData, List<HashMap<String, Object>> nodesData,
                                              LinkedList<LinkedHashMap<String, String>> serviceInstanceData, long requestTime, long responseTime) {
        HashMap<String, HashMap<String, Integer>> nodeServiceInstanceCount = new HashMap<>();

        for (HashMap<String, Object> podItem : podsData) {
            LinkedHashMap<String, String> podData = new LinkedHashMap<>();

            // get service id
            String serviceId = podItem.get("serviceId").toString();
            String nodeId = podItem.get("nodeId").toString();

            // filter the database data
            if (serviceId.contains("mongo") || serviceId.contains("mysql")) {
                continue;
            }

            // add the pod data
            podData.put("service_inst_id", podItem.get("podId").toString());
            podData.put("service_inst_node_id", nodeId);
            podData.put("service_inst_service_version", podItem.get("serviceVersion").toString());
            podData.put("service_id", serviceId);
            podData.put("service_inst_up_time", new BigDecimal(responseTime - Long.parseLong(podItem.get("createTime").toString()))
                    .divide(new BigDecimal(1000), 0, RoundingMode.HALF_UP).toString());
            podData.put("service_app_thread_count", podItem.get("threadCount").toString());

            Map<String, String> podUsage = (Map<String, String>) podItem.get("usage");
            if (MapUtils.isNotEmpty(podUsage)) {
                podData.put("service_inst_cpu", null == podUsage.get("cpu") ? "" : podUsage.get("cpu"));
                podData.put("service_inst_memory", null == podUsage.get("memory") ? "" : podUsage.get("memory"));
            } else {
                podData.put("service_inst_cpu", "");
                podData.put("service_inst_memory", "");
            }

            // add the node data
            for (HashMap<String, Object> nodeItem : nodesData) {
                if (nodeId.equals(nodeItem.get("nodeId"))) {
                    Map<String, String> nodeUsage = (Map<String, String>) nodeItem.get("usage");
                    Map<String, String> nodeConfig = (Map<String, String>) nodeItem.get("config");
                    if (MapUtils.isNotEmpty(nodeUsage)) {
                        podData.put("service_inst_node_cpu", null == nodeUsage.get("cpu") ? "" : nodeUsage.get("cpu"));
                        podData.put("service_inst_node_memory", null == nodeUsage.get("memory") ? "" : nodeUsage.get("memory"));
                    } else {
                        podData.put("service_inst_node_cpu", "");
                        podData.put("service_inst_node_memory", "");
                    }

                    if (MapUtils.isNotEmpty(nodeConfig)) {
                        podData.put("service_inst_node_cpu_limit", null == nodeConfig.get("cpu") ? "" : nodeConfig.get("cpu"));
                        podData.put("service_inst_node_memory_limit", null == nodeConfig.get("memory") ? "" : nodeConfig.get("memory"));
                    } else {
                        podData.put("service_inst_node_cpu_limit", "");
                        podData.put("service_inst_node_memory_limit", "");
                    }

                    break;
                }
            }

            if (nodeServiceInstanceCount.containsKey(nodeId)) {
                HashMap<String, Integer> serviceInstanceCount = nodeServiceInstanceCount.get(nodeId);
                if (serviceInstanceCount.containsKey(serviceId)) {
                    serviceInstanceCount.put(serviceId, serviceInstanceCount.get(serviceId) + 1);
                }
                else {
                    serviceInstanceCount.put(serviceId, 1);
                }

                nodeServiceInstanceCount.put(nodeId, serviceInstanceCount);
            }
            else {
                HashMap<String, Integer> serviceInstanceCount = new HashMap<>();
                serviceInstanceCount.put(serviceId, 1);
                nodeServiceInstanceCount.put(nodeId, serviceInstanceCount);
            }

            podData.put(START_TIME, requestTime + "");
            podData.put(END_TIME, responseTime + "");
            serviceInstanceData.add(podData);
        }

        for (LinkedHashMap<String, String> service : serviceInstanceData) {
            service.put("service_node_instance_count", nodeServiceInstanceCount
                    .get(service.get("service_inst_node_id"))
                    .get(service.get("service_id")).toString());
        }

    }

    /**
     * Construct the service data with "LinkedList<LinkedHashMap<String, String>>"
     *
     * @param servicesData service config data
     * @param requestTime  the request time
     * @param responseTime the response time
     * @return the result list
     */
    private LinkedList<LinkedHashMap<String, String>> constructServiceData(List<HashMap<String, Object>> servicesData, long requestTime, long responseTime) {
        LinkedList<LinkedHashMap<String, String>> exportData = new LinkedList<>();

        for (HashMap<String, Object> serviceData : servicesData) {
            LinkedHashMap<String, String> serviceDataMap = new LinkedHashMap<>();

            // filter the database data
            if (serviceData.get(SERVICE_NAME).toString().contains("mongo") || serviceData.get(SERVICE_NAME).toString().contains("mysql")) {
                continue;
            }

            for (String key : serviceData.keySet()) {
                if (SERVICE_NAME.equals(key) || CONF_NUMBER.equals(key) || READY_NUMBER.equals(key)
                    || HEALTH_CHECK_READY_DELAY.equals(key) || HEALTH_CHECK_DOWN_DELAY.equals(key) ) {
                    serviceDataMap.put(key, serviceData.get(key).toString());
                }
//            else if (REQUESTS.equals(key)) {
//                extractLimitsAndRequests(serviceData.getJSONObject(key), REQUESTS, serviceDataMap);
//            }
                else if (LIMITS.equals(key)) {
                    extractLimitsAndRequests((LinkedHashMap<String, String>) serviceData.get(key), LIMITS, serviceDataMap);
                }
            }

            serviceDataMap.put(START_TIME, requestTime + "");
            serviceDataMap.put(END_TIME, responseTime + "");
            exportData.add(serviceDataMap);
        }

        return exportData;
    }

    private void createCSVTableAndWriteHDFSFile(LinkedList<LinkedHashMap<String, String>> exportData, String fileName, boolean contentFlag) throws Exception {
        // create the header of the csv table
        LinkedHashMap<String, String> headMap = new LinkedHashMap<String, String>();

        // construct the CSV Table header
        constructCSVTableHeader(headMap, exportData, contentFlag);

        // create the CSV File
        CSVUtils.createCSVFile(exportData, headMap, "/home", fileName, contentFlag);

        // write CSV file to HDFS
        writeToHDFS(fileName);
    }

    /**
     * @param headMap     the header map of CSV table
     * @param exportData  the data of service returned by call k8s API
     * @param contentFlag true: one service data from one remote call write in one row
     *                    false: all service data from one remote call write in one row
     */
    private void constructCSVTableHeader(LinkedHashMap<String, String> headMap, LinkedList<LinkedHashMap<String, String>> exportData, boolean contentFlag) {

        // record the pod count. key: service name of pod, value: count of pod
        Map<String, Integer> podCount = new HashMap<>();

        if (contentFlag) {
            for (Map.Entry<String, String> entry : exportData.get(0).entrySet()) {
                headMap.put(entry.getKey(), entry.getKey());
            }
        } else {
            for (LinkedHashMap<String, String> anExportData : exportData) {
                String serviceName = anExportData.get("serviceName");
                if (null != serviceName) {
                    serviceName = serviceName.replaceAll("-", "_");
                    for (Map.Entry<String, String> entry : anExportData.entrySet()) {
                        headMap.put(serviceName + "_" + entry.getKey(), entry.getKey());
                    }
                } else {
                    // get the service name from the first element of the map
                    serviceName = anExportData.keySet().toArray(new String[0])[0].split("_inst")[0];

                    // record the pod count
                    if (!podCount.containsKey(serviceName)) {
                        podCount.put(serviceName, 1);
                    }

                    // flag to recognize if the service has several pods
                    boolean sameFlag = false;

                    for (Map.Entry<String, String> entry : anExportData.entrySet()) {
                        // the service has several pods
                        if (headMap.containsKey(entry.getKey())) {
                            String[] tempKey = entry.getKey().split("_inst");
                            // add the index
                            headMap.put(tempKey[0] + "_inst_" + podCount.get(tempKey[0]) + tempKey[1], entry.getKey());
                            sameFlag = true;
                        } else {
                            headMap.put(entry.getKey(), entry.getKey());
                        }
                    }

                    // add the count of pod
                    if (sameFlag) {
                        podCount.put(serviceName, podCount.get(serviceName) + 1);
                    }
                }
            }
        }
    }

    private void extractLimitsAndRequests(LinkedHashMap<String, String> dataMap, String resourceType, LinkedHashMap<String, String> serviceDataMap) {
        if (MapUtils.isEmpty(dataMap)) {
            if (REQUESTS.equals(resourceType)) {
                serviceDataMap.put(REQUEST_CPU, "");
                serviceDataMap.put(REQUEST_MEMORY, "");
            } else if (LIMITS.equals(resourceType)) {
                serviceDataMap.put(LIMIT_CPU, "");
                serviceDataMap.put(LIMIT_MEMORY, "");
            }
        } else {
            if (REQUESTS.equals(resourceType)) {
                serviceDataMap.put(REQUEST_CPU, dataMap.get(CPU));
                serviceDataMap.put(REQUEST_MEMORY, dataMap.get(MEMORY));
            } else if (LIMITS.equals(resourceType)) {
                serviceDataMap.put(LIMIT_CPU, dataMap.get(CPU));
                serviceDataMap.put(LIMIT_MEMORY, dataMap.get(MEMORY));
            }
        }
    }

    private static void writeToHDFS(String fileName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
        FileSystem fs = FileSystem.get(conf);

      //  System.out.println("================  begin create file =============");

        Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/" + fileName + ".csv");
        if (fs.exists(newFile)) {
            fs.delete(newFile, false);
        }

        fs.copyFromLocalFile(new Path("/home/" + fileName + ".csv"), newFile);
        System.out.println(System.currentTimeMillis() + "   ================  end create file =============");
    }
}
