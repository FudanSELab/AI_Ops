package com.train.test.services.impl;

import com.train.test.domain.InitCacheDataResponse;
import com.train.test.entity.instance.FlowTestResult;
import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import com.train.test.services.InstanceErrorService;
import com.train.test.utils.ArrangeInStanceNum;
import com.train.test.utils.BookingFlowPassServiceMapUtil;
import com.train.test.utils.TimeUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class InstanceErrorServiceImpl implements InstanceErrorService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private int EVERY_THREAD_RUN_TIME = 24;
    private static String SET_REPLICAS_URI = "http://10.141.212.140:18898/api/setReplicas";
    private static String TEST_CASE_URL = "http://10.141.212.140:10101/test/bookingflow";
    private static String INIT_CLIENT_CACHE_ACCESS_NUM = "http://10.141.212.140:10101/test/initBookingFlowCacheData";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String testInstanceErrorFlowOne() {

        Map<Integer, Map<Integer, String>> flowOnePassServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();
         // 整个流程的服务和下标
        for (int i = 0; i < flowOnePassServiceMap.size(); i++) {
            Map<Integer, String> oneTraceServiceMap = flowOnePassServiceMap.get(i);
            int serviceSize = oneTraceServiceMap.size();

            HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(serviceSize);

            logger.info(oneTraceAllArrangeList.size() + "------=排列的数量=--=-=-");

            // 某一个trace 的
            for (int j = 0; j < oneTraceAllArrangeList.size(); j++) {
                // init 客户端记录的数量
                InitCacheDataResponse initCacheDataResponse = restTemplate.getForObject(INIT_CLIENT_CACHE_ACCESS_NUM, InitCacheDataResponse.class);
                // 跑每个trace 的 排列前， 先初始化客户端访问变量次数， 然后跑24次
                if (initCacheDataResponse.isStatus()) {
                    logger.info("-------------------change client cache success -----------------------");
                    // 请求改变实例数量
                    boolean isChangReplicasReady = requestToChangeReplicas(oneTraceAllArrangeList.get(j), oneTraceServiceMap);
                    if (!isChangReplicasReady) {
                        logger.info("----------------- check is not ready--------------------");
                    }
                    if (isChangReplicasReady) {

                        TimeUtils.waitMinutes(60);

                        // 跑 24 次 case
                        logger.info("===================" + i + " times ====================");
                        try {
                            exeRemoteRequet();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        logger.info("===================" + i + " times over  =================");
                    }
                }

                TimeUtils.waitMinutes(60);
                logger.info("------------ " + oneTraceAllArrangeList.get(j).toString() + "  一行 " + EVERY_THREAD_RUN_TIME + " 次 已经跑完 -------------");
            }

            // 恢复实例数全为1，避免影响后面
            boolean isChangReplicasReady = requestToChangeReplicas(resetInStanceReplicas(serviceSize), oneTraceServiceMap);
            if (isChangReplicasReady) {
                logger.info("---------改变的服务数量已经恢复为1----------");
            }

            logger.info("-------" + i + "-- 次 trace 经过" + oneTraceServiceMap.size() + "服务, 排列大小---" + oneTraceAllArrangeList.size() + "  已经跑完-------");
        }
        logger.info("all  over");
        return "all over";
    }

    @Override
    public String testInstanceErrorOneService(String serviceName , int replicasNum) {
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        ssrrDto.setClusterName("cluster1");
        List<ServiceReplicasSetting> srsList = new ArrayList<>();
        srsList.add(new ServiceReplicasSetting(serviceName, replicasNum));
        ssrrDto.setServiceReplicasSettings(srsList);
        SetServiceReplicasResponse srs =
                restTemplate.postForObject(SET_REPLICAS_URI, ssrrDto, SetServiceReplicasResponse.class);
        return srs.getMessage();
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
        logger.info("------------ " + tempss.toString() + "  一行 " + EVERY_THREAD_RUN_TIME + " 次 开始跑 -------------");

        SetServiceReplicasResponse srs =
                restTemplate.postForObject(SET_REPLICAS_URI, ssrrDto, SetServiceReplicasResponse.class);
        return srs.isStatus();
    }


    public void exeRemoteRequet() throws InterruptedException {
        logger.info("主线程开始-------");
        // 每个线程5次 ，共15次
        Thread t1 = new Thread(new ThreadWorker("thread-1"));
        ///  Thread t2 = new Thread(new ThreadWorker("thread-2"));
        //  Thread t3 = new Thread(new ThreadWorker("thread-3"));
        t1.start();
        //  t2.start();
        //   t3.start();
        t1.join();
        //  t2.join();
        //   t3.join();
        logger.info("主线程结束-------");
    }


    class ThreadWorker implements Runnable {
        private String name;

        public ThreadWorker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 0; i < EVERY_THREAD_RUN_TIME; i++) {
                try {
                    //restTemplate.getForObject("http://localhost:10101/test/bookingflow", FlowTestResult.class);
                    restTemplate.getForObject(TEST_CASE_URL, FlowTestResult.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(i + "------ times ----" + "thread over ========" + name);
            }
        }
    }
}
