package com.train.test.services.impl;

import com.train.test.domain.InitCacheDataResponse;
import com.train.test.entity.instance.FlowTestResult;
import com.train.test.entity.instance.ServiceReplicasSetting;
import com.train.test.entity.instance.SetServiceReplicasRequest;
import com.train.test.entity.instance.SetServiceReplicasResponse;
import com.train.test.services.InstanceErrorService;
import com.train.test.utils.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstanceErrorServiceImpl implements InstanceErrorService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private static int EVERY_THREAD_RUN_TIME = 10;
    private static String SET_REPLICAS_URI = "http://10.141.212.140:18898/api/setReplicas";

    private static String TEST_CASE_flowOne_URL = "http://10.141.212.140:10101/test/bookingflow";
    private static String INIT_CLIENT_CACHE_ACCESS_NUM = "http://10.141.212.140:10101/test/initBookingFlowCacheData";

    private static String TEST_CASE_CANCEL_FLOW_URL = "http://10.141.212.140:10101/test/cancelflow";
    private static String INIT_CLIENT_CANCEL_CASHE_ACCESS_NUM = "http://10.141.212.140:10101/test/initCancelFlowCacheData";

    private static String TEST_CASE_CONSIGN_FLOW_URL = "http://10.141.212.140:10101/test/consignFlow";
    private static String TEST_CASE_VOUCHER_FLOW_URL = "http://10.141.212.140:10101/test/voucherFlow";
    private static String INIT_CLIENT_CONSIGN_CASHE_ACCESS_NUM = "http://10.141.212.140:10101/test/initFlowThreeClientCacheData";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String testInstanceErrorFlowOne() {

        Map<Integer, Map<Integer, String>> flowOnePassServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();
        testFlowPublic(flowOnePassServiceMap, TEST_CASE_flowOne_URL, INIT_CLIENT_CACHE_ACCESS_NUM);
        logger.info("all  over");
        return "all over";
    }

    @Override
    public String testInstanceErrorCancelFlow() {
        Map<Integer, Map<Integer, String>> cancelFlowPassServiceMap = CancelFlowPassServiceMapUtil.cancelFlowPassService();
        testFlowPublic(cancelFlowPassServiceMap, TEST_CASE_CANCEL_FLOW_URL, INIT_CLIENT_CANCEL_CASHE_ACCESS_NUM);
        logger.info("all  over");
        return "all over";
    }

    @Override
    public String testConsignFlow() {
        Map<Integer, Map<Integer, String>> consignFlowPassServiceMap = ConsignFlowPassServiceMapUtil.consignFlowPassService();
        testFlowPublic(consignFlowPassServiceMap, TEST_CASE_CONSIGN_FLOW_URL, INIT_CLIENT_CONSIGN_CASHE_ACCESS_NUM);
        logger.info("all  over");
        return "all over";
    }

    @Override
    public String testVoucherFlow() {
        Map<Integer, Map<Integer, String>> consignFlowPassServiceMap = ConsignFlowPassServiceMapUtil.consignFlowPassService();
        testFlowPublic(consignFlowPassServiceMap, TEST_CASE_VOUCHER_FLOW_URL, INIT_CLIENT_CONSIGN_CASHE_ACCESS_NUM);
        logger.info("all  over");
        return "all over";
    }

    @Override
    public String testInstanceErrorOneService(String serviceName, int replicasNum) {
        SetServiceReplicasRequest ssrrDto = new SetServiceReplicasRequest();
        ssrrDto.setClusterName("cluster1");
        List<ServiceReplicasSetting> srsList = new ArrayList<>();
        srsList.add(new ServiceReplicasSetting(serviceName, replicasNum));
        ssrrDto.setServiceReplicasSettings(srsList);
        SetServiceReplicasResponse srs =
                restTemplate.postForObject(SET_REPLICAS_URI, ssrrDto, SetServiceReplicasResponse.class);
        return srs.getMessage();
    }


    private void testFlowPublic(Map<Integer, Map<Integer, String>> flowMap, String testFlowUrl, String initCacheDataUrl) {
        // 整个流程的服务和下标
        for (int i = 0; i < flowMap.size(); i++) {
            Map<Integer, String> oneTraceServiceMap = flowMap.get(i);
            int serviceSize = oneTraceServiceMap.size();

            HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(serviceSize);

            logger.info("trace - " + i + "-------排列的数量-" + oneTraceAllArrangeList.size());

            // 某一个trace 的
            for (int j = 0; j < oneTraceAllArrangeList.size(); j++) {
                if (oneTraceAllArrangeList.get(j).get(0) == 2 && oneTraceAllArrangeList.get(j).get(1) == 2) {
                    EVERY_THREAD_RUN_TIME = 35;
                } else {
                    //EVERY_THREAD_RUN_TIME = 3;
                    continue;
                }
                // init 客户端记录的数量
                InitCacheDataResponse initCacheDataResponse = restTemplate.getForObject(initCacheDataUrl, InitCacheDataResponse.class);
                // 跑每个trace 的 排列前， 先初始化客户端访问变量次数， 然后跑24次
                if (initCacheDataResponse.isStatus()) {
                    logger.info("-------------------change client cache success -----------------------");
                    // 请求改变实例数量
                    boolean isChangReplicasReady = true;
                    try {
                        //  isChangReplicasReady = requestToChangeReplicas(oneTraceAllArrangeList.get(j), oneTraceServiceMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("------exception --- continue-1----");
                        continue;
                    }
                    if (!isChangReplicasReady) {
                        logger.info("----------------- check is not ready--------------------");
                    }
                    if (isChangReplicasReady) {

                        // TimeUtils.waitMinutes(50);

                        // 跑 24 次 case
                        logger.info("===================" + i + " times ====================");
                        try {
                            exeRemoteRequet(testFlowUrl);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        logger.info("===================" + i + " times over  =================");
                    }
                }

                // TimeUtils.waitMinutes(50);
                logger.info("------------ " + oneTraceAllArrangeList.get(j).toString() + "  一行 " + EVERY_THREAD_RUN_TIME + " 次 已经跑完 -------------");
            }

            // 恢复实例数全为1，避免影响后面
            boolean isChangReplicasReady = true;
            try {
                //isChangReplicasReady = requestToChangeReplicas(resetInStanceReplicas(serviceSize), oneTraceServiceMap);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("------exception --- continue-2----");
                continue;
            }
            if (isChangReplicasReady) {
                logger.info("---------改变的服务数量已经恢复为1----------");
            }

            logger.info("-------" + i + "-- 次 trace 经过" + oneTraceServiceMap.size() + "服务, 排列大小---" + oneTraceAllArrangeList.size() + "  已经跑完-------");
        }
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
    private boolean requestToChangeReplicas(List<Integer> tempss, Map<Integer, String> serviceIndexMap) throws Exception {
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

        SetServiceReplicasResponse srs
                = restTemplate.postForObject(SET_REPLICAS_URI, ssrrDto, SetServiceReplicasResponse.class);
        return srs.isStatus();
    }


    public void exeRemoteRequet(String testUrl) throws InterruptedException {
        logger.info("线程开始-------");
        // 每个线程5次 ，共15次
//        Thread t1 = new Thread(new ThreadWorker("thread-1", testUrl));
//        Thread t2 = new Thread(new ThreadWorker("thread-2", testUrl));
//        Thread t3 = new Thread(new ThreadWorker("thread-3", testUrl));
//        t1.start();
//        t2.start();
//        t3.start();
//        t1.join();
//        t2.join();
//        t3.join();

        for (int i = 0; i < EVERY_THREAD_RUN_TIME; i++) {
            try {
                //restTemplate.getForObject("http://localhost:10101/test/bookingflow", FlowTestResult.class);
                restTemplate.getForObject(testUrl, FlowTestResult.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeUtils.waitMILLISECONDS(500);
            logger.info(i + "------ times ----" + "thread over ========");
        }

        logger.info("线程结束-------");
    }


//    class ThreadWorker implements Runnable {
//        private String name;
//
//        private String testUrl;
//
//        public ThreadWorker(String name, String testUrl) {
//            this.name = name;
//            this.testUrl = testUrl;
//        }
//
//        @Override
//        public void run() {
//            for (int i = 0; i < EVERY_THREAD_RUN_TIME; i++) {
//                try {
//                    //restTemplate.getForObject("http://localhost:10101/test/bookingflow", FlowTestResult.class);
//                    restTemplate.getForObject(testUrl, FlowTestResult.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                TimeUtils.waitMILLISECONDS(500);
//                logger.info(i + "------ times ----" + "thread over ========" + name);
//            }
//        }
//    }
}
