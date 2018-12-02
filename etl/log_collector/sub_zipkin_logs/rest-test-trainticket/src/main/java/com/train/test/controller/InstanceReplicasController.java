package com.train.test.controller;


import com.train.test.entity.instance.FlowTestResult;
import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import com.train.test.utils.ArrangeInStanceNum;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class InstanceReplicasController {


    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    private static Map<Integer, String> serviceIndexMap = new HashMap<>();


    private final static Executor executor = Executors.newCachedThreadPool();

    @RequestMapping(value = "/testOne")
    public String testOne() {
        String url = "http://10.141.212.140:18898/api/setReplicas";
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        ssrrDto.setClusterName("cluster1");
        List<ServiceReplicasSetting> srsList = new ArrayList<>();
        srsList.add(new ServiceReplicasSetting("ts-login-service", 2));
        ssrrDto.setServiceReplicasSettings(srsList);


        SetServiceReplicasResponse srs =
                restTemplate.postForObject(url, ssrrDto, SetServiceReplicasResponse.class);
        return srs.getMessage();
    }

    @RequestMapping(value = "/testThread")
    public String testThread() throws InterruptedException {
        logger.info("===================ssssssssssssssssssssssss");
        exeRemoteRequet();
        exeRemoteRequet();
        exeRemoteRequet();
        logger.info("=============errerererereresssssssssssss");
        return "thread";
    }

    @RequestMapping(value = "/changeInstanceAndRun")
    public String changeInstanceAndRun() {
        initOrderFlowService();
        // 调 interface
        String url = "http://10.141.212.140:18898/api/setReplicas";
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        // 添加要改变的  服务 和  数量
        HashMap<Integer, List<Integer>> allList = ArrangeInStanceNum.getAllrangeList(7);
        System.out.println(allList.size() + "------=排列的数量=--=-=-");
        for (int i = 0; i < allList.size(); i++) {
            // 某一行的 1112111
            List<ServiceReplicasSetting> srsList = new ArrayList<>();
            List<Integer> tempss = allList.get(i);
            for (int j = 0; j < tempss.size(); j++) {
                // 设置 一行服务的实例数量
                srsList.add(new ServiceReplicasSetting(serviceIndexMap.get(j), tempss.get(j)));
            }
            ssrrDto.setClusterName("cluster1");
            ssrrDto.setServiceReplicasSettings(srsList);
            System.out.println("------------ " + tempss.toString() + "  一行 15 次 开始跑 -------------");

            SetServiceReplicasResponse srs =
                    restTemplate.postForObject(url, ssrrDto, SetServiceReplicasResponse.class);
            if (!srs.isStatus()){
                logger.info("----------------- check is  ready--------------------");
            }
            if (srs.isStatus()) {
                // 跑 20次 case
                logger.info("===================" + i +" times ====================");
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
                logger.info("===================" + i +" times over  =================");
            }
            System.out.println("------------ " + tempss.toString() + "  一行 15 次 已经跑完 -------------");
        }
        System.out.println("all  over");
        return "all  over";
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


    private void initOrderFlowService() {
        //private static Map<Integer, String> serviceMap = new HashMap<>(){1:""};
        serviceIndexMap.put(0, "ts-login-service");
        serviceIndexMap.put(1, "ts-travel2-service");
        serviceIndexMap.put(2, "ts-travel-service");
        serviceIndexMap.put(3, "ts-contacts-service");
        serviceIndexMap.put(4, "ts-food-service");
        serviceIndexMap.put(5, "ts-preserve-service");
        serviceIndexMap.put(6, "ts-execute-service");
    }

}
