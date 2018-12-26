package com.train.test.utils;

import java.util.HashMap;
import java.util.Map;

public class CancelFlowPassServiceMapUtil {

    public static Map<Integer, Map<Integer, String>> cancelFlowPassService() {
        Map<Integer, Map<Integer, String>> cancelFlowPassService = new HashMap<>();

        // login
        Map<Integer, String> loginServiceMap = new HashMap<>();
        loginServiceMap.put(0, "ts-login-service");
        loginServiceMap.put(1, "ts-verification-code-service");
        loginServiceMap.put(2, "ts-sso-service");
        cancelFlowPassService.put(0, loginServiceMap);

        Map<Integer, String> orderQueryMap = new HashMap<>();
        orderQueryMap.put(0, "ts-order-service");
        orderQueryMap.put(1, "ts-sso-service");
        cancelFlowPassService.put(1, orderQueryMap);

        Map<Integer, String> orderOtherQuery = new HashMap<>();
        orderOtherQuery.put(0, "ts-order-other-service");
        orderOtherQuery.put(1, "ts-sso-service");
        cancelFlowPassService.put(2, orderOtherQuery);

        Map<Integer, String> calculateRefound = new HashMap<>();
        calculateRefound.put(0, "ts-cancel-service");
        calculateRefound.put(1, "ts-order-service");
        calculateRefound.put(2, "ts-order-other-service");
        cancelFlowPassService.put(3, calculateRefound);

        Map<Integer, String> cancelService = new HashMap<>();
        cancelService.put(0, "ts-cancel-service");
        cancelService.put(1, "ts-sso-service");
        cancelService.put(2, "ts-order-service");
        cancelService.put(3, "ts-order-other-service");
        cancelFlowPassService.put(4, cancelService);


        return cancelFlowPassService;
    }
}
