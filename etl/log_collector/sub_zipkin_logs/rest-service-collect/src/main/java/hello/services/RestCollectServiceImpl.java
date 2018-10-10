package hello.services;

import hello.queue.MsgSender;
import hello.utils.CSVUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RestCollectServiceImpl implements RestCollectService {

    private static final String STATUS = "status";
    private static final String SERVICES = "services";
    private static final String SERVICE_NAME = "serviceName";
    private static final String REQUESTS = "requests";
    private static final String LIMITS = "limits";
    private static final String INSTANCE_NUMBER = "instanceNumber";
    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";
    private static final String REQUEST_CPU = "r_cpu";
    private static final String REQUEST_MEMORY = "r_memory";
    private static final String LIMIT_CPU = "l_cpu";
    private static final String LIMIT_MEMORY = "l_memory";
    private static final String SERVICE_CONFIG_DATA = "serviceConfigData";
    private static final String SERVICE_INSTANCE_DATA = "serviceInstanceData";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";

    private Boolean flag = true;

    @Autowired
    private MsgSender msgSender;

    @Override
    public void getResourceData() {

        while (flag) {

            try {
                msgSender.sendLoginInfoToSso(System.currentTimeMillis());

                Thread.sleep(60000);
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


        boolean contentFlag = false;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // create http request
        OkHttpClient okHttpClient = new OkHttpClient();
        // request to get service config data
        Request requestService = new Request.Builder().url("http://10.141.212.21:18898/api/getServicesAndConfig/cluster2").build();
        // request to get service pod data
        Request requestPod = new Request.Builder().url("http://10.141.212.21:18898/api/podMetrics/cluster2").build();
        // request to get node data
        Request requestNode = new Request.Builder().url("http://10.141.212.21:18898/api/nodeMetrics/cluster2").build();


        try {
            Response responseService = okHttpClient.newCall(requestService).execute();
            Response responsePod = okHttpClient.newCall(requestPod).execute();
            Response responseNode = okHttpClient.newCall(requestNode).execute();
            long responseTime = System.currentTimeMillis();

            if (null != responseService.body() && null != requestPod.body() && null != responseNode.body()) {
                JSONObject responseServiceData = JSONObject.fromObject(responseService.body().string());
                JSONObject responseNodeData = JSONObject.fromObject(responseNode.body().string());
                JSONObject responsePodData = JSONObject.fromObject(responsePod.body().string());

                System.out.println("Request time: " + dateFormat.format(requestTime));
                System.out.println("Response time: " + dateFormat.format(responseTime));

                if (responseServiceData.get(STATUS).toString().equals(Boolean.TRUE.toString())) {
                    JSONArray servicesData = JSONArray.fromObject(responseServiceData.get(SERVICES));
                    createCSVTableAndWriteHDFSFile(constructServiceData(servicesData, requestTime, responseTime), SERVICE_CONFIG_DATA, contentFlag);
                }

                if (responseNodeData.get(STATUS).toString().equals(Boolean.TRUE.toString())
                        && responsePodData.get(STATUS).toString().equals(Boolean.TRUE.toString())) {
                    List<HashMap<String, Object>> podsData = (List<HashMap<String, Object>>) responsePodData.get("podsMetrics");
                    List<HashMap<String, Object>> nodesData = (List<HashMap<String, Object>>) responseNodeData.get("nodesMetrics");
                    LinkedList<LinkedHashMap<String, String>> serviceInstanceData = new LinkedList<>();
                    constructServiceInstanceData(podsData, nodesData, serviceInstanceData, requestTime, responseTime);
                    createCSVTableAndWriteHDFSFile(serviceInstanceData, SERVICE_INSTANCE_DATA, contentFlag);
                }


                System.out.println("End write time: " + dateFormat.format(System.currentTimeMillis()));
            } else {
                throw new Exception("Get Service/Pod/Node data failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void constructServiceInstanceData(List<HashMap<String, Object>> podsData, List<HashMap<String, Object>> nodesData,
                                              LinkedList<LinkedHashMap<String, String>> serviceInstanceData, long requestTime, long responseTime) {
        for (HashMap<String, Object> podItem : podsData) {
            LinkedHashMap<String, String> podData = new LinkedHashMap<>();

            // get service name
            String serviceName = podItem.get("podId").toString().contains("mongo")
                    ? (podItem.get("podId").toString().split("mongo")[0] + "mongo").replaceAll("-", "_")
                    : (podItem.get("podId").toString().split("service")[0] + "service").replaceAll("-", "_");

            // add the pod data
            podData.put(serviceName + "_inst_id", podItem.get("podId").toString());
            podData.put(serviceName + "_inst_node_id", podItem.get("nodeId").toString());

            Map<String, String> podUsage = (Map<String, String>) podItem.get("usage");
            if (MapUtils.isNotEmpty(podUsage)) {
                podData.put(serviceName + "_inst_cpu", null == podUsage.get("cpu") ? "" : podUsage.get("cpu"));
                podData.put(serviceName + "_inst_memory", null == podUsage.get("memory") ? "" : podUsage.get("memory"));
            } else {
                podData.put(serviceName + "_inst_cpu", "");
                podData.put(serviceName + "_inst_memory", "");
            }

            // add the node data
            for (HashMap nodeItem : nodesData) {
                if (podItem.get("nodeId").toString().equals(nodeItem.get("nodeId"))) {
                    Map<String, String> nodeUsage = (Map<String, String>) nodeItem.get("usage");
                    Map<String, String> nodeConfig = (Map<String, String>) nodeItem.get("usage");
                    if (MapUtils.isNotEmpty(nodeUsage)) {
                        podData.put(serviceName + "_inst_node_cpu", null == nodeUsage.get("cpu") ? "" : nodeUsage.get("cpu"));
                        podData.put(serviceName + "_inst_node_memory", null == nodeUsage.get("memory") ? "" : nodeUsage.get("memory"));
                    } else {
                        podData.put(serviceName + "_inst_node_cpu", "");
                        podData.put(serviceName + "_inst_node_memory", "");
                    }

                    if (MapUtils.isNotEmpty(nodeConfig)) {
                        podData.put(serviceName + "_inst_node_cpu_limit", null == nodeConfig.get("cpu") ? "" : nodeConfig.get("cpu"));
                        podData.put(serviceName + "_inst_node_memory_limit", null == nodeConfig.get("memory") ? "" : nodeConfig.get("memory"));
                    } else {
                        podData.put(serviceName + "_inst_node_cpu_limit", "");
                        podData.put(serviceName + "_inst_node_memory_limit", "");
                    }

                    break;
                }
            }

            podData.put(serviceName + "_collect_" + START_TIME, requestTime + "");
            podData.put(serviceName + "_collect_" +END_TIME, responseTime +"" );
            serviceInstanceData.add(podData);
        }
    }

    /**
     * Construct the service data with "LinkedList<LinkedHashMap<String, String>>"
     *
     * @param servicesData service config data
     * @param requestTime the request time
     * @param responseTime the response time
     * @return the result list
     */
    private LinkedList<LinkedHashMap<String, String>> constructServiceData(JSONArray servicesData, long requestTime, long responseTime) {
        LinkedList<LinkedHashMap<String, String>> exportData = new LinkedList<LinkedHashMap<String, String>>();

        // convert the json array to the list
        for (Object serviceData : servicesData) {
            LinkedHashMap<String, String> serviceDataMap = jsonToMap(JSONObject.fromObject(serviceData), requestTime, responseTime);
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
                }
                else {
                    for (Map.Entry<String, String> entry : anExportData.entrySet()) {
                        headMap.put(entry.getKey(), entry.getKey());
                    }
                }
            }
        }
    }

    private LinkedHashMap<String, String> jsonToMap(JSONObject serviceData, long requestTime, long responseTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LinkedHashMap<String, String> serviceDataMap = new LinkedHashMap<String, String>();

        Iterator<?> iterator = serviceData.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (SERVICE_NAME.equals(key) || INSTANCE_NUMBER.equals(key)) {
                serviceDataMap.put(key, serviceData.getString(key));
            }
//            else if (REQUESTS.equals(key)) {
//                extractLimitsAndRequests(serviceData.getJSONObject(key), REQUESTS, serviceDataMap);
//            }
            else if (LIMITS.equals(key)) {
                extractLimitsAndRequests(serviceData.getJSONObject(key), LIMITS, serviceDataMap);
            }
        }

        serviceDataMap.put(START_TIME, requestTime + "");
        serviceDataMap.put(END_TIME, responseTime + "");
        return serviceDataMap;
    }

    private void extractLimitsAndRequests(JSONObject jsonObject, String resourceType, LinkedHashMap<String, String> serviceDataMap) {
        if (jsonObject.isEmpty()) {
            if (REQUESTS.equals(resourceType)) {
                serviceDataMap.put(REQUEST_CPU, "");
                serviceDataMap.put(REQUEST_MEMORY, "");
            } else if (LIMITS.equals(resourceType)) {
                serviceDataMap.put(LIMIT_CPU, "");
                serviceDataMap.put(LIMIT_MEMORY, "");
            }
        } else {
            if (REQUESTS.equals(resourceType)) {
                serviceDataMap.put(REQUEST_CPU, jsonObject.getString(CPU));
                serviceDataMap.put(REQUEST_MEMORY, jsonObject.getString(MEMORY));
            } else if (LIMITS.equals(resourceType)) {
                serviceDataMap.put(LIMIT_CPU, jsonObject.getString(CPU));
                serviceDataMap.put(LIMIT_MEMORY, jsonObject.getString(MEMORY));
            }
        }
    }

    private static void writeToHDFS(String fileName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
        FileSystem fs = FileSystem.get(conf);

        System.out.println("================  begin create file =============");

        Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/" + fileName + ".csv");
        if (fs.exists(newFile)) {
            fs.delete(newFile, false);
        }

        fs.copyFromLocalFile(new Path("/home/" + fileName + ".csv"), newFile);
        System.out.println("================  end create file =============");
    }
}
