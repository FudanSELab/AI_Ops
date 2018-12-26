package hello.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedVariableUtils {

    /*
     * {
     *   "serviceName" : {
     *       "apiPath1" : {
     *            "var1" : "initialType1",
     *            "var2" : "initialType2"},
     *        ...
     *    },
     *  ...
     * }
     */
    private static Map<String, Map<String, Map<String, String>>> sharedVariableMap;

    static {
        Map<String, String> variableToInitialType = new HashMap<>();
        Map<String, Map<String, String>> apiPathToVariable = new HashMap<>();
        Map<String, Map<String, Map<String, String>>> serviceToApiPath = new HashMap<>();

        // ts-contacts-service
        variableToInitialType.put("requestContactsNumCache", "2");
        apiPathToVariable.put("/contacts", variableToInitialType);
        serviceToApiPath.put("ts-contacts-service", apiPathToVariable);

        // ts-execute-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("executeNumCache", "2");
        apiPathToVariable.put("/execute", variableToInitialType);
        variableToInitialType = new HashMap<>();
        variableToInitialType.put("collectNumCache", "2");
        apiPathToVariable.put("/execute", variableToInitialType);
        serviceToApiPath.put("ts-execute-service", apiPathToVariable);

        // ts-food-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("getFoodTimeCache", "2");
        apiPathToVariable.put("/food", variableToInitialType);
        serviceToApiPath.put("ts-food-service", apiPathToVariable);

        // ts-login-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("loginNumCache", "2");
        apiPathToVariable.put("/login", variableToInitialType);
        variableToInitialType = new HashMap<>();
        variableToInitialType.put("logoutNumCache", "2");
        apiPathToVariable.put("/logout", variableToInitialType);
        serviceToApiPath.put("ts-login-service", apiPathToVariable);

        // ts-preserve-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("preserveCache", "2");
        apiPathToVariable.put("/preserve", variableToInitialType);
        serviceToApiPath.put("ts-preserve-service", apiPathToVariable);

        // ts-travel-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("travelQueryNumCache", "2");
        apiPathToVariable.put("/travel", variableToInitialType);
        serviceToApiPath.put("ts-travel-service", apiPathToVariable);

        // ts-travel2-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("travel2QueryNumCache", "2");
        apiPathToVariable.put("/travel2", variableToInitialType);
        serviceToApiPath.put("ts-travel2-service", apiPathToVariable);

        // ts-cancel-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("cancelCalculateCache", "2");
        apiPathToVariable.put("/cancelCalculateRefund", variableToInitialType);

        variableToInitialType = new HashMap<>();
        variableToInitialType.put("cancelOrderCache", "2");
        apiPathToVariable.put("/cancelOrder", variableToInitialType);
        serviceToApiPath.put("ts-cancel-service", apiPathToVariable);

        // ts-order-other-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("orderOtherQueryCache", "2");
        apiPathToVariable.put("/orderOther", variableToInitialType);
        serviceToApiPath.put("ts-order-other-service", apiPathToVariable);

        // ts-order-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("orderQueryCache", "2");
        apiPathToVariable.put("/order", variableToInitialType);
        serviceToApiPath.put("ts-order-service", apiPathToVariable);

        // ts-consign-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("insertConsignCache", "2");
        apiPathToVariable.put("/consign", variableToInitialType);
        serviceToApiPath.put("ts-consign-service", apiPathToVariable);


        sharedVariableMap = serviceToApiPath;
    }

    public static Map<String, Map<String, Map<String, String>>> getSharedVariableMap() {
        return sharedVariableMap;
    }

    public static Map<String, Map<String, List<String>>> getPassVarName() {
        Map<String, Map<String, List<String>>> passVarNameMap = new HashMap<>();

        Map<String, List<String>> servicePassVarMap = new HashMap<>();
        List<String> serviceVarNameList = new ArrayList<>();

        serviceVarNameList.add("insertConsignCache");
        servicePassVarMap.put("/consign", serviceVarNameList);
        passVarNameMap.put("ts-consign-service", servicePassVarMap);

        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("orderQueryCache");
        servicePassVarMap.put("/order", serviceVarNameList);
        passVarNameMap.put("ts-order-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("orderOtherQueryCache");
        servicePassVarMap.put("/orderOther", serviceVarNameList);
        passVarNameMap.put("ts-order-other-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("cancelOrderCache");
        servicePassVarMap.put("/cancelOrder", serviceVarNameList);
        passVarNameMap.put("ts-cancel-service", servicePassVarMap);


        servicePassVarMap = new HashMap<>();

        serviceVarNameList = new ArrayList<>();
        serviceVarNameList.add("cancelOrderCache");
        servicePassVarMap.put("/cancelOrder", serviceVarNameList);

        serviceVarNameList = new ArrayList<>();
        serviceVarNameList.add("cancelCalculateCache");
        servicePassVarMap.put("/cancelCalculateRefund", serviceVarNameList);

        passVarNameMap.put("ts-cancel-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("travel2QueryNumCache");
        servicePassVarMap.put("/travel2", serviceVarNameList);
        passVarNameMap.put("ts-travel2-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("travelQueryNumCache");
        servicePassVarMap.put("/travel/query", serviceVarNameList);
        passVarNameMap.put("ts-travel-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("preserveCache");
        servicePassVarMap.put("/preserve", serviceVarNameList);
        passVarNameMap.put("ts-preserve-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("logoutNumCache");
        servicePassVarMap.put("/logout", serviceVarNameList);

        serviceVarNameList = new ArrayList<>();
        serviceVarNameList.add("loginNumCache");
        servicePassVarMap.put("/login", serviceVarNameList);

        passVarNameMap.put("ts-login-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("getFoodTimeCache");
        servicePassVarMap.put("/food", serviceVarNameList);
        passVarNameMap.put("ts-food-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("requestContactsNumCache");
        servicePassVarMap.put("/contacts", serviceVarNameList);
        passVarNameMap.put("ts-contacts-service", servicePassVarMap);


        serviceVarNameList = new ArrayList<>();
        servicePassVarMap = new HashMap<>();
        serviceVarNameList.add("collectNumCache");
        servicePassVarMap.put("/execute", serviceVarNameList);

        serviceVarNameList = new ArrayList<>();
        serviceVarNameList.add("executeNumCache");
        servicePassVarMap.put("/execute", serviceVarNameList);
        passVarNameMap.put("ts-execute-service", servicePassVarMap);

        return passVarNameMap;
    }

    public static Map<String, Integer> getServiceVarlible() {
        Map<String, Integer> serviceValNumMap = new HashMap<>();
        serviceValNumMap.put("ts-consign-service", 1);
        serviceValNumMap.put("ts-order-service", 1);
        serviceValNumMap.put("ts-order-other-service", 1);
        serviceValNumMap.put("ts-cancel-service", 1);
        serviceValNumMap.put("ts-cancel-service", 2);
        serviceValNumMap.put("ts-travel2-service", 1);
        serviceValNumMap.put("ts-travel-service", 1);
        serviceValNumMap.put("ts-preserve-service", 1);
        serviceValNumMap.put("ts-login-service", 2);
        serviceValNumMap.put("ts-food-service", 1);
        serviceValNumMap.put("ts-contacts-service", 1);
        serviceValNumMap.put("ts-execute-service", 2);
        return serviceValNumMap;
    }

//    public static void main(String[] args) {
//        System.out.println(getSharedVariableMap().get("ts-consign-service").get("/consign/insertConsign").get("insertConsignCache"));
//        System.out.println(getPassVarName().get("ts-execute-service").get("/execute/execute").get(0));
//        System.out.println(getPassVarName().get("ts-contacts-service").size());
//        System.out.println();
//
//    }


}
