package hello.services;

import hello.queue.MsgSender;
import hello.utils.CSVUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

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
    private static final String TIME = "time";

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
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url("http://10.141.212.21:18898/api/getServicesAndConfig/cluster2").build();


        try {
            Response response = okHttpClient.newCall(request).execute();
            long responseTime = System.currentTimeMillis();
            JSONObject responseData;
            if (null != response.body()) {
                responseData = JSONObject.fromObject(response.body().string());
                System.out.println("Request time: " + dateFormat.format(requestTime));
                System.out.println("Response time: " + dateFormat.format(responseTime));

                if (responseData.get(STATUS).toString().equals("true")) {
                    JSONArray servicesData = JSONArray.fromObject(responseData.get(SERVICES));
                    if (null != servicesData && !servicesData.isEmpty()) {
                        LinkedList<LinkedHashMap<String, String>> exportData = new LinkedList<LinkedHashMap<String, String>>();

                        // convert the json array to the list
                        for (Object serviceData : servicesData) {
                            LinkedHashMap<String, String> serviceDataMap = jsonToMap(JSONObject.fromObject(serviceData), requestTime);
                            exportData.add(serviceDataMap);
                        }

                        // create the header of the csv table
                        LinkedHashMap<String, String> headMap = new LinkedHashMap<String, String>();

                        // construct the CSV Table header
                        constructCSVTableHeader(headMap, exportData, contentFlag);

                        // create the CSV File
                        CSVUtils.createCSVFile(exportData, headMap, "/home", "CpuMemoryTelemetry", contentFlag);
                    }
                }

                // write CSV file to HDFS
                writeToHDFS();

                System.out.println("End write time: " + dateFormat.format(System.currentTimeMillis()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param headMap the header map of CSV table
     * @param exportData the data of service returned by call k8s API
     * @param contentFlag true: one service data from one remote call write in one row
     *                   false: all service data from one remote call write in one row
     */
    private void constructCSVTableHeader(LinkedHashMap<String, String> headMap, LinkedList<LinkedHashMap<String, String>> exportData, boolean contentFlag) {

        if (contentFlag) {
            for (Map.Entry<String, String> entry : exportData.get(0).entrySet()) {
                headMap.put(entry.getKey(), entry.getKey());
            }
        }
        else {
            for (LinkedHashMap<String, String> anExportData : exportData) {
                String serviceName = anExportData.get("serviceName").replaceAll("-", "_");
                for (Map.Entry<String, String> entry : anExportData.entrySet()) {
                    headMap.put(serviceName + "_" + entry.getKey(), entry.getKey());
                }
            }
        }
    }

    private LinkedHashMap<String, String> jsonToMap(JSONObject serviceData, long requestTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LinkedHashMap<String, String> serviceDataMap = new LinkedHashMap<String, String>();

        Iterator<?> iterator = serviceData.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (SERVICE_NAME.equals(key) || INSTANCE_NUMBER.equals(key)) {
                serviceDataMap.put(key, serviceData.getString(key));
            } else if (REQUESTS.equals(key)) {
                extractLimitsAndRequests(serviceData.getJSONObject(key), REQUESTS, serviceDataMap);
            } else if (LIMITS.equals(key)) {
                extractLimitsAndRequests(serviceData.getJSONObject(key), LIMITS, serviceDataMap);
            }
        }

        // according to calculate, the request to K8S is about 2 seconds.
        serviceDataMap.put(TIME, requestTime + 2000 + "");
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

    private static void writeToHDFS() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
        FileSystem fs = FileSystem.get(conf);

        System.out.println("================  begin create file =============");

        Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/CpuMemoryTelemetry.csv");
        if (fs.exists(newFile)) {
            fs.delete(newFile, false);
        }

        fs.copyFromLocalFile(new Path("/home/CpuMemoryTelemetry.csv"), newFile);
        System.out.println("================  end create file =============");
    }
}
