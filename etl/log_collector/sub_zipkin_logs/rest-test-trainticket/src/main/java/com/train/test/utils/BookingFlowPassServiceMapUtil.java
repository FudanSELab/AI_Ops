package com.train.test.utils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingFlowPassServiceMapUtil {

// 测试排列组合
//    public static void main(String[] args){
//        Map<Integer, Map<Integer, String>> flowOnePassServiceMap = BookingFlowPassServiceMapUtil.flowOnePassService();
//
//        for (int i = 0; i < flowOnePassServiceMap.size(); i++) {
//            Map<Integer, String> oneTraceServiceMap = flowOnePassServiceMap.get(i);
//            int serviceSize = oneTraceServiceMap.size();
//              System.out.println(i);
//            HashMap<Integer, List<Integer>> oneTraceAllArrangeList = ArrangeInStanceNum.getAllrangeList(serviceSize);
//            for(int j =0;j<oneTraceAllArrangeList.size() ;j++){
//                System.out.println(oneTraceAllArrangeList.get(j).toString());
//            }
//
//        }
//    }
    public static Map<Integer, Map<Integer, String>> flowOnePassService() {
        Map<Integer, Map<Integer, String>> allFlowOnePassServcie = new HashMap<>();

        // login
        Map<Integer, String> loginServiceMap = new HashMap<>();
        loginServiceMap.put(0, "ts-login-service");
        loginServiceMap.put(1, "ts-verification-code-service");
        loginServiceMap.put(2, "ts-sso-service");
        allFlowOnePassServcie.put(0, loginServiceMap);

        // query ticket
        Map<Integer, String> queryTicketMap = new HashMap<>();
        queryTicketMap.put(0, "ts-travel2-service");
        queryTicketMap.put(1, "ts-travel-service");
        queryTicketMap.put(2, "ts-ticketinfo-service");
        queryTicketMap.put(3, "ts-order-other-service");
        queryTicketMap.put(4, "ts-route-service");
        queryTicketMap.put(5, "ts-seat-service");
        queryTicketMap.put(6, "ts-order-service");
        queryTicketMap.put(7, "ts-train-service");
        allFlowOnePassServcie.put(1, queryTicketMap);


        // query contacts
        Map<Integer, String> queryContactsMap = new HashMap<>();
        queryContactsMap.put(0, "ts-contacts-service");
        queryContactsMap.put(1, "ts-sso-service");
        allFlowOnePassServcie.put(2, queryContactsMap);

        // query food
        Map<Integer, String> queryFoodMap = new HashMap<>();
        queryFoodMap.put(0, "ts-food-service");
        queryFoodMap.put(1, "ts-food-map-service");
        queryFoodMap.put(2, "ts-travel-service");
        queryFoodMap.put(3, "ts-station-service");
        allFlowOnePassServcie.put(3, queryFoodMap);

        // preserve ticket
        Map<Integer, String> preserveTicketMap = new HashMap<>();
        preserveTicketMap.put(0, "ts-preserve-service");
        preserveTicketMap.put(1, "ts-sso-service");
        preserveTicketMap.put(2, "ts-security-service");
        preserveTicketMap.put(3, "ts-contacts-service");
        preserveTicketMap.put(4, "ts-travel-service");
        preserveTicketMap.put(5, "ts-ticketinfo-service");
        preserveTicketMap.put(6, "ts-route-service");
        preserveTicketMap.put(7, "ts-order-service");
        preserveTicketMap.put(8, "ts-seat-service");
        preserveTicketMap.put(9, "ts-train-service");
        allFlowOnePassServcie.put(4, preserveTicketMap);

        // ticket pay
        Map<Integer, String> ticketPaymentMap = new HashMap<>();
        ticketPaymentMap.put(0, "ts-inside-payment-service");
        ticketPaymentMap.put(1, "ts-order-service");
        ticketPaymentMap.put(2, "ts-order-other-service");
        ticketPaymentMap.put(3, "ts-payment-service");
        allFlowOnePassServcie.put(5, ticketPaymentMap);

        Map<Integer, String> ticketCollectMap = new HashMap<>();
        ticketCollectMap.put(0, "ts-execute-service");
        ticketCollectMap.put(1, "ts-order-service");
        ticketCollectMap.put(2, "ts-order-other-service");
        allFlowOnePassServcie.put(6, ticketCollectMap);

        // enter station
        Map<Integer, String> enterStationMap = new HashMap<>();
        enterStationMap.put(0, "ts-execute-service");
        enterStationMap.put(1, "ts-order-service");
        enterStationMap.put(2, "ts-order-other-service");
        allFlowOnePassServcie.put(7, enterStationMap);


        return allFlowOnePassServcie;
    }


    private Map<Integer, String> initOrderFlowService() {
        Map<Integer, String> serviceIndexMap = new HashMap<>();
        serviceIndexMap.put(0, "ts-login-service");
        serviceIndexMap.put(1, "ts-travel2-service");
        serviceIndexMap.put(2, "ts-travel-service");
        serviceIndexMap.put(3, "ts-contacts-service");
        serviceIndexMap.put(4, "ts-food-service");
        serviceIndexMap.put(5, "ts-preserve-service");
        serviceIndexMap.put(6, "ts-execute-service");
        return serviceIndexMap;
    }


}
