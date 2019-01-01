package com.train.test.utils;

import java.util.HashMap;
import java.util.Map;

public class VoucherFlowPassServiceMapUtil {

    public static Map<Integer, Map<Integer, String>> voucherFlowPassService() {
        Map<Integer, Map<Integer, String>> voucherFlowPassService = new HashMap<>();

        Map<Integer, String> loginServiceMap = new HashMap<>();
        loginServiceMap.put(0, "ts-login-service");
        loginServiceMap.put(1, "ts-verification-code-service");
        loginServiceMap.put(2, "ts-sso-service");
        voucherFlowPassService.put(0, loginServiceMap);

        Map<Integer, String> orderQueryMap = new HashMap<>();
        orderQueryMap.put(0, "ts-order-service");
        orderQueryMap.put(1, "ts-sso-service");
        voucherFlowPassService.put(1, orderQueryMap);

        Map<Integer, String> orderOtherQuery = new HashMap<>();
        orderOtherQuery.put(0, "ts-order-other-service");
        orderOtherQuery.put(1, "ts-sso-service");
        voucherFlowPassService.put(2, orderOtherQuery);


//        Map<Integer, String> stationQuery = new HashMap<>();
//        stationQuery.put(0, "ts-station-service");
//        voucherFlowPassService.put(3, stationQuery);

        Map<Integer, String> voucherQuery = new HashMap<>();
        voucherQuery.put(0, "ts-voucher-service");
        voucherQuery.put(1, "ts-order-other-service");
        voucherQuery.put(2, "ts-order-service");
        voucherFlowPassService.put(3, voucherQuery);

        return voucherFlowPassService;
    }
}
