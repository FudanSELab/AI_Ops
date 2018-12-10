package hello.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloumnNameUtil {

    public static List<String> addCpuMemDiffCloumn() {
        List<String> cpuMemDiff = new ArrayList<>();
        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
            String ser = tracePassServiceCloumn[i].replaceAll("_included", "");
            cpuMemDiff.add(ser+"_inst_mem_diff");
            cpuMemDiff.add(ser+"_inst_cpu_diff");
        }
        return cpuMemDiff;
    }

    public static Map<Integer, List<String>> getCpuMemDiff() {
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
        tempList.add("trace_service_span_ql");
        for (int i = 0; i < tracePassServiceCloumn.length; i++) {
            String ser = tracePassServiceCloumn[i].replaceAll("_included", "");
            tempList.add(ser + "_included");
            tempList.add(ser + "_api");
            tempList.add(ser + "_exec_time_ql");
            // System.out.println("\""+ ser + "_included" +"\""+" , " +"\""+ ser + "_api" +"\""+ " , " +"\""+ ser + "_exec_time"+"\"");
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
}