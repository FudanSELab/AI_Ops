package com.train.test.utils;


import java.util.*;

public class BookingFlowPassServiceMapUtil {


    public static Map<Integer, Map<Integer, String>> flowOnePassService() {
        Map<Integer, Map<Integer, String>> allFlowOnePassService = new HashMap<>();

        // login
        Map<Integer, String> loginServiceMap = new HashMap<>();
        loginServiceMap.put(0, "ts-login-service");
        loginServiceMap.put(1, "ts-verification-code-service");
        loginServiceMap.put(2, "ts-sso-service");
        allFlowOnePassService.put(0, loginServiceMap);

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
        allFlowOnePassService.put(1, queryTicketMap);


        // query contacts
        Map<Integer, String> queryContactsMap = new HashMap<>();
        queryContactsMap.put(0, "ts-contacts-service");
        queryContactsMap.put(1, "ts-sso-service");
        allFlowOnePassService.put(2, queryContactsMap);

        // query food
        Map<Integer, String> queryFoodMap = new HashMap<>();
        queryFoodMap.put(0, "ts-food-service");
        queryFoodMap.put(1, "ts-food-map-service");
        queryFoodMap.put(2, "ts-travel-service");
        queryFoodMap.put(3, "ts-station-service");
        allFlowOnePassService.put(3, queryFoodMap);

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
        allFlowOnePassService.put(4, preserveTicketMap);

        // ticket pay
        Map<Integer, String> ticketPaymentMap = new HashMap<>();
        ticketPaymentMap.put(0, "ts-inside-payment-service");
        ticketPaymentMap.put(1, "ts-order-service");
        ticketPaymentMap.put(2, "ts-order-other-service");
        ticketPaymentMap.put(3, "ts-payment-service");
        allFlowOnePassService.put(5, ticketPaymentMap);

        Map<Integer, String> ticketCollectMap = new HashMap<>();
        ticketCollectMap.put(0, "ts-execute-service");
        ticketCollectMap.put(1, "ts-order-service");
        ticketCollectMap.put(2, "ts-order-other-service");
        allFlowOnePassService.put(6, ticketCollectMap);

        // enter station
        Map<Integer, String> enterStationMap = new HashMap<>();
        enterStationMap.put(0, "ts-execute-service");
        enterStationMap.put(1, "ts-order-service");
        enterStationMap.put(2, "ts-order-other-service");
        allFlowOnePassService.put(7, enterStationMap);


        return allFlowOnePassService;
    }


    public static Map<Integer, List<String>> getErrorServiceInFlowOne() {
        Map<Integer, List<String>> serviceIndexMap = new HashMap<>();
        serviceIndexMap.put(0, Collections.singletonList("ts-login-service"));
        serviceIndexMap.put(1, Arrays.asList("ts-travel2-service", "ts-travel-service"));
        serviceIndexMap.put(2, Collections.singletonList("ts-contacts-service"));
        serviceIndexMap.put(3, Collections.singletonList("ts-food-service"));
        serviceIndexMap.put(4, Arrays.asList("ts-preserve-service", "ts-preserve-other-service"));
        serviceIndexMap.put(5, Collections.singletonList("ts-inside-payment-service"));
        serviceIndexMap.put(6, Collections.singletonList("ts-execute-service"));
        serviceIndexMap.put(7, Collections.singletonList("ts-execute-service"));
        return serviceIndexMap;
    }


}
