package hello.util;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloumnNameUtil {

    public static List<String> addCpuMemDiffCloumn() {
        List<String> cpuMemDiff = new ArrayList<>();
        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
            String ser = tracePassServiceCloumn[i].replaceAll("_included", "");
            cpuMemDiff.add(ser + "_inst_mem_diff");
            cpuMemDiff.add(ser + "_inst_cpu_diff");
        }
        return cpuMemDiff;
    }

    public static Map<Integer, List<String>> getCpuMemDiffCloName() {
        Map<Integer, List<String>> allCpuMemDiff = new HashMap<>();
        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
            List<String> oneService = new ArrayList<>();
            String ser = tracePassServiceCloumn[i].replaceAll("_included", "");
            oneService.add(ser + "_l_memory");
            oneService.add(ser + "_inst_memory");
            oneService.add(ser + "_l_cpu");
            oneService.add(ser + "_inst_cpu");
            allCpuMemDiff.put(i, oneService);
        }
        return allCpuMemDiff;
    }


    public static List<String> getTracePassCloumnAllList() {
        List<String> tempList = new ArrayList<>();
        tempList.add("trace_id");
        tempList.add("trace_service_span");

        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
            String serName = tracePassServiceCloumn[i].replaceAll("_included", "");
            tempList.add(serName + "_included");
            tempList.add(serName + "_api");
            tempList.add(serName + "_inst_id");
            tempList.add(serName + "_inst_status_code");
            tempList.add(serName + "_exec_time");

            //  有变量的服务才加,
            //  加的列，为每个服务中变量的个数
            Integer varNum = SharedVariableUtils.getServiceVarlible().get(serName.replaceAll("_", "-"));
            if (varNum != null) {
                for (int j = 0; j < varNum; j++) {
                    tempList.add(serName + "_var_" + j);
                }
            }
        }
        return tempList;
    }


    public static String[] tracePassServiceCloumn = new String[]{
            "ts_login_service_included", "ts_register_service_included",
            "ts_sso_service_included", "ts_verification_code_service_included",
            "ts_contacts_service_included", "ts_order_service_included",
            "ts_order_other_service_included", "ts_config_service_included",
            "ts_station_service_included", "ts_train_service_included",
            "ts_travel_service_included", "ts_travel2_service_included",
            "ts_preserve_service_included", "ts_preserve_other_service_included",
            "ts_basic_service_included", "ts_ticketinfo_service_included",
            "ts_price_service_included", "ts_notification_service_included",
            "ts_security_service_included", "ts_inside_payment_service_included",
            "ts_admin_route_service_included", "ts_admin_travel_service_included",
            "ts_admin_user_service_included", "ts_execute_service_included",
            "ts_payment_service_included", "ts_rebook_service_included",
            "ts_cancel_service_included", "ts_route_service_included",
            "ts_assurance_service_included", "ts_seat_service_included",
            "ts_travel_plan_service_included", "ts_route_plan_service_included",
            "ts_food_map_service_included", "ts_food_service_included",
            "ts_news_service_included", "ts_consign_price_service_included",
            "ts_consign_service_included", "ts_admin_order_service_included",
            "ts_admin_basic_info_service_included", "ts_ticket_office_service_included",
            "ts_voucher_service_included", "ts_ui_dashboard_included",
    };

    public static String[] tracePassServiceCloumnAll = new String[]{
            "trace_id", "trace_service_span_ql",
            "ts_login_service_included", "ts_login_service_api", "ts_register_service_included", "ts_register_service_api",
            "ts_sso_service_included", "ts_sso_service_api", "ts_verification_code_service_included", "ts_verification_code_service_api",
            "ts_contacts_service_included", "ts_contacts_service_api", "ts_order_service_included", "ts_order_service_api",
            "ts_order_other_service_included", "ts_order_other_service_api", "ts_config_service_included", "ts_config_service_api",
            "ts_station_service_included", "ts_station_service_api", "ts_train_service_included", "ts_train_service_api",
            "ts_travel_service_included", "ts_travel_service_api", "ts_travel2_service_included", "ts_travel2_service_api",
            "ts_preserve_service_included", "ts_preserve_service_api", "ts_preserve_other_service_included", "ts_preserve_other_service_api",
            "ts_basic_service_included", "ts_basic_service_api", "ts_ticketinfo_service_included", "ts_ticketinfo_service_api",
            "ts_price_service_included", "ts_price_service_api", "ts_notification_service_included", "ts_notification_service_api",
            "ts_security_service_included", "ts_security_service_api", "ts_inside_payment_service_included", "ts_inside_payment_service_api",
            "ts_admin_route_service_included", "ts_admin_route_service_api", "ts_admin_travel_service_included", "ts_admin_travel_service_api",
            "ts_admin_user_service_included", "ts_admin_user_service_api", "ts_execute_service_included", "ts_execute_service_api",
            "ts_payment_service_included", "ts_payment_service_api", "ts_rebook_service_included", "ts_rebook_service_api",
            "ts_cancel_service_included", "ts_cancel_service_api", "ts_route_service_included", "ts_route_service_api",
            "ts_assurance_service_included", "ts_assurance_service_api", "ts_seat_service_included", "ts_seat_service_api",
            "ts_travel_plan_service_included", "ts_travel_plan_service_api", "ts_route_plan_service_included", "ts_route_plan_service_api",
            "ts_food_map_service_included", "ts_food_map_service_api", "ts_food_service_included", "ts_food_service_api",
            "ts_news_service_included", "ts_news_service_api", "ts_consign_price_service_included", "ts_consign_price_service_api",
            "ts_consign_service_included", "ts_consign_service_api", "ts_admin_order_service_included", "ts_admin_order_service_api",
            "ts_admin_basic_info_service_included", "ts_admin_basic_info_service_api", "ts_ticket_office_service_included", "ts_ticket_office_service_api",
            "ts_voucher_service_included", "ts_voucher_service_api", "ts_ui_dashboard_included", "ts_ui_dashboard_api"
    };

//    public static void main(String[] args) {
//        Map<String, String> fdfd = configServe();
//        System.out.println(fdfd.size() + "=========批量生成42条SQL=============");
//        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
//            System.out.println(fdfd.get(tracePassServiceCloumn[i].replaceAll("_included", "")));
//        }
//    }

    public static Map<String, String> configEachServeSQL() {
        Map<String, String> tableNameAndSQl = new HashMap<>();

        String[] basic_service = tracePassServiceCloumn;
        for (int i = 0; i < basic_service.length; i++) {
            String tempService = basic_service[i].replaceAll("_included", "");
            String temp = "select a.*, " +
                    "b.service_inst_memory as " + tempService + "_inst_memory , " +
                    "b.service_inst_cpu as " + tempService + "_inst_cpu , " +
                    "b.service_inst_service_version as " + tempService + "_inst_service_version , " +
                    "b.service_inst_node_id as " + tempService + "_inst_node_id , " +
                    "b.service_inst_node_cpu as " + tempService + "_inst_node_cpu , " +
                    "b.service_inst_node_memory as " + tempService + "_inst_node_memory , " +
                    "b.service_inst_node_cpu_limit as " + tempService + "_inst_node_cpu_limit , " +
                    "b.service_inst_node_memory_limit as " + tempService + "_inst_node_memory_limit " +
                    "from trace_passservice_view a left outer join service_instance_data b " +
                    "on  a." + tempService + "_inst_id" + " = b.service_inst_id And (substr(a.entry_timestamp , 1 , 13) + 60000 ) > b.start_time And (substr(a.entry_timestamp , 1 , 13)) < b.start_time";

            tableNameAndSQl.put(tempService, temp);
        }
        return tableNameAndSQl;
    }
}