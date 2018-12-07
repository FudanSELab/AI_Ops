package hello.util;

import java.util.HashMap;
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
        apiPathToVariable.put("/contacts/findContacts", variableToInitialType);
        serviceToApiPath.put("ts-contacts-service", apiPathToVariable);

        // ts-execute-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("executeNumCache", "2");
        apiPathToVariable.put("/execute/execute", variableToInitialType);
        variableToInitialType = new HashMap<>();
        variableToInitialType.put("collectNumCache", "2");
        apiPathToVariable.put("/execute/collected", variableToInitialType);
        serviceToApiPath.put("ts-execute-service", apiPathToVariable);

        // ts-food-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("getFoodTimeCache", "2");
        apiPathToVariable.put("/food/getFood", variableToInitialType);
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
        apiPathToVariable.put("/travel/query", variableToInitialType);
        serviceToApiPath.put("ts-travel-service", apiPathToVariable);

        // ts-travel2-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("travel2QueryNumCache", "2");
        apiPathToVariable.put("/travel2/query", variableToInitialType);
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
        apiPathToVariable.put("/orderOther/query", variableToInitialType);
        serviceToApiPath.put("ts-order-other-service", apiPathToVariable);

        // ts-order-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("orderQueryCache", "2");
        apiPathToVariable.put("/order/query", variableToInitialType);
        serviceToApiPath.put("ts-order-service", apiPathToVariable);

        // ts-consign-service
        variableToInitialType = new HashMap<>();
        apiPathToVariable = new HashMap<>();
        variableToInitialType.put("insertConsignCache", "2");
        apiPathToVariable.put("/consign/insertConsign", variableToInitialType);
        serviceToApiPath.put("ts-consign-service", apiPathToVariable);

        sharedVariableMap = serviceToApiPath;
    }

    public static Map<String, Map<String, Map<String, String>>> getSharedVariableMap(){
        return sharedVariableMap;
    }
}
