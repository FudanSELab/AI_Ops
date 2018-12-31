package hello.util;

import org.apache.spark.sql.sources.In;

import java.util.HashMap;
import java.util.Map;

public class DependentDbCache {
    // 有写个1
   public static Map<String, Integer> getDependentDB(){
       Map<String, Integer> dependentDBMap = new HashMap<>();
       dependentDBMap.put("ts-assurance-service", 1);
       dependentDBMap.put("ts-config-service", 1);
       dependentDBMap.put("ts-consign-price-service", 1);
       dependentDBMap.put("ts-consign-service", 1);
       dependentDBMap.put("ts-contacts-service", 1);
       dependentDBMap.put("ts-food-map-service", 1);
       dependentDBMap.put("ts-food-service", 1);
       dependentDBMap.put("ts-inside-payment-service", 1);
       dependentDBMap.put("ts-order-other-service", 1);
       dependentDBMap.put("ts-order-service", 1);
       dependentDBMap.put("ts-payment-service", 1);
       dependentDBMap.put("ts-price-service", 1);
       dependentDBMap.put("ts-route-service", 1);
       dependentDBMap.put("ts-security-service", 1);
       dependentDBMap.put("ts-sso-service", 1);
       dependentDBMap.put("ts-station-service", 1);
       dependentDBMap.put("ts-ticket-office-service", 1);
       dependentDBMap.put("ts-train-service", 1);
       dependentDBMap.put("ts-travel2-service", 1);
       dependentDBMap.put("ts-travel-service", 1);
       dependentDBMap.put("ts-verification-code-service", 1);
       dependentDBMap.put("ts-voucher-service", 1);
       return dependentDBMap;
    }
}
