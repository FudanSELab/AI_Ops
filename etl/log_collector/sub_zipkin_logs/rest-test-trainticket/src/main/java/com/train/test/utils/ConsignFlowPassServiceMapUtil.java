package com.train.test.utils;

import java.util.HashMap;
import java.util.Map;

public class ConsignFlowPassServiceMapUtil {

    public static Map<Integer, Map<Integer, String>> consignFlowPassService() {
        Map<Integer, Map<Integer, String>> consignFlowPassService = new HashMap<>();

        Map<Integer, String> loginServiceMap = new HashMap<>();
        loginServiceMap.put(0, "ts-login-service");
        loginServiceMap.put(1, "ts-verification-code-service");
        loginServiceMap.put(2, "ts-sso-service");
        consignFlowPassService.put(0, loginServiceMap);

        Map<Integer, String> orderQueryMap = new HashMap<>();
        orderQueryMap.put(0, "ts-order-service");
        orderQueryMap.put(1, "ts-sso-service");
        consignFlowPassService.put(1, orderQueryMap);

        Map<Integer, String> orderOtherQuery = new HashMap<>();
        orderOtherQuery.put(0, "ts-order-other-service");
        orderOtherQuery.put(1, "ts-sso-service");
        consignFlowPassService.put(2, orderOtherQuery);

//        Map<Integer, String> stationQuery = new HashMap<>();
//        stationQuery.put(0, "ts-station-service");
//        consignFlowPassService.put(3, stationQuery);

        Map<Integer, String> consignQuery = new HashMap<>();
        consignQuery.put(0, "ts-consign-service");
        consignQuery.put(1, "ts-consign-price-service");
        consignFlowPassService.put(3, consignQuery);

        return consignFlowPassService;
    }


}
