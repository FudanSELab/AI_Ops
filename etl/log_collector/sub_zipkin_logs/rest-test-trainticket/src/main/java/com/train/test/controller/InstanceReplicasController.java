package com.train.test.controller;

import com.train.test.entity.instance.FlowTestResult;
import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import com.train.test.utils.ArrangeInStanceNum;
import com.train.test.utils.BookingFlowPassServiceMapUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

@RestController
public class InstanceReplicasController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private static String URL = "http://10.141.212.140:18898/api/setReplicas";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/changeInstanceAndRun")
    public String changeInstanceAndRun() {
        // 整个流程的服务和下标
        Map<Integer, Map<Integer, String>> flowOnePassServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();

        for (int i = 0; i < flowOnePassServiceMap.size(); i++) {
            Map<Integer, String> oneTraceServiceMap = flowOnePassServiceMap.get(i);
            int serviceSize = oneTraceServiceMap.size();

            HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(serviceSize);

            System.out.println(oneTraceAllArrangeList.size() + "------=排列的数量=--=-=-");

            // 某一个trace 的
            for (int j = 0; j < oneTraceAllArrangeList.size(); j++) {
                boolean isChangReplicasReady = requestToChangeReplicas(oneTraceAllArrangeList.get(j), oneTraceServiceMap);
                if (!isChangReplicasReady) {
                    logger.info("----------------- check is not ready--------------------");
                }
                if (isChangReplicasReady) {
                    // 跑 15次 case
                    logger.info("===================" + i + " times ====================");
                    try {
                        exeRemoteRequet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("===================" + i + " times over  =================");
                }
                System.out.println("------------ " + oneTraceAllArrangeList.get(j).toString() + "  一行 15 次 已经跑完 -------------");
            }

            // 恢复实例数全为1，避免影响后面
            boolean isChangReplicasReady = requestToChangeReplicas(resetInStanceReplicas(serviceSize), oneTraceServiceMap);
            if(isChangReplicasReady)
                System.out.println("---------改变的服务数量已经恢复为1----------");

            System.out.println("-------" + i + "-- 次 trace 经过" + oneTraceServiceMap.size() + "服务, 排列大小---" + oneTraceAllArrangeList.size() + "  已经跑完-------");
        }
        System.out.println("all  over");
        return "all  over";
    }

    private List<Integer> resetInStanceReplicas(int serviceSize) {
        List<Integer> tempLsit = new ArrayList<>();
        for (int i = 0; i < serviceSize; i++) {
            tempLsit.add(1);
        }
        return tempLsit;
    }

    /**
     * @param tempss          某一行的实例数量 112，
     * @param serviceIndexMap 某一行经过的服务map
     * @return
     */
    private boolean requestToChangeReplicas(List<Integer> tempss, Map<Integer, String> serviceIndexMap) {
        // 调 interface
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        // 添加要改变的  服务 和  数量
        List<ServiceReplicasSetting> srsList = new ArrayList<>();

        for (int j = 0; j < tempss.size(); j++) {
            // 设置 一行服务   和  实例数量
            srsList.add(new ServiceReplicasSetting(serviceIndexMap.get(j), tempss.get(j)));
        }
        ssrrDto.setClusterName("cluster1");
        ssrrDto.setServiceReplicasSettings(srsList);
        System.out.println("------------ " + tempss.toString() + "  一行 15 次 开始跑 -------------");

        SetServiceReplicasResponse srs =
                restTemplate.postForObject(URL, ssrrDto, SetServiceReplicasResponse.class);
        return srs.isStatus();
    }


    public void exeRemoteRequet() throws InterruptedException {
        logger.info("主线程开始-------");
        // 每个线程5次 ，共15次
        Thread t1 = new Thread(new ThreadWorker("thread-1"));
        Thread t2 = new Thread(new ThreadWorker("thread-2"));
        Thread t3 = new Thread(new ThreadWorker("thread-3"));
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        logger.info("主线程结束-------");
    }

    class ThreadWorker implements Runnable {
        private String name;

        public ThreadWorker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                // AsyncRestTemplate asyncTemplate = new AsyncRestTemplate();
                try {
                    logger.info(i + "------ times ");
                    restTemplate.getForObject("http://10.141.212.140:10101/test/bookingflow", FlowTestResult.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("thread over ========" + name);
            }
        }
    }


//    @RequestMapping(value = "/testOne")
//    public String testOne() {
//        String url = "http://10.141.212.140:18898/api/setReplicas";
//        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
//        ssrrDto.setClusterName("cluster1");
//        List<ServiceReplicasSetting> srsList = new ArrayList<>();
//        srsList.add(new ServiceReplicasSetting("ts-login-service", 2));
//        ssrrDto.setServiceReplicasSettings(srsList);
//        SetServiceReplicasResponse srs =
//                restTemplate.postForObject(url, ssrrDto, SetServiceReplicasResponse.class);
//        return srs.getMessage();
//    }

//
//    @RequestMapping(value = "/testThread")
//    public String testThread() throws InterruptedException {
//        logger.info("===================ssssssssssssssssssssssss");
//        exeRemoteRequet();
//        exeRemoteRequet();
//        exeRemoteRequet();
//        logger.info("=============errerererereresssssssssssss");
//        return "thread";
//    }

}