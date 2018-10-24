package hello.domain;

import java.util.Map;

public class TracePassServcie {
    private String trace_id;

    private String ts_ui_dashboard_included = "0";
    private String ts_login_service_included = "0";
    private String ts_register_service_included = "0";
    private String ts_sso_service_included = "0";
    private String ts_verification_code_service_included = "0";
    private String ts_contacts_service_included = "0";
    private String ts_order_service_included = "0";
    private String ts_order_other_service_included = "0";
    private String ts_config_service_included = "0";
    private String ts_station_service_included = "0";
    private String ts_train_service_included = "0";
    private String ts_travel_service_included = "0";
    private String ts_travel2_service_included = "0";
    private String ts_preserve_service_included = "0";
    private String ts_preserve_other_service_included = "0";
    private String ts_basic_service_included = "0";
    private String ts_ticketinfo_service_included = "0";
    private String ts_price_service_included = "0";
    private String ts_notification_service_included = "0";
    private String ts_security_service_included = "0";
    private String ts_inside_payment_service_included = "0";
    private String ts_execute_service_included = "0";
    private String ts_payment_service_included = "0";
    private String ts_rebook_service_included = "0";
    private String ts_cancel_service_included = "0";
    private String ts_route_service_included = "0";
    private String ts_assurance_service_included = "0";
    private String ts_seat_service_included = "0";
    private String ts_travel_plan_service_included = "0";
    private String ts_route_plan_service_included = "0";
    private String ts_food_map_service_included = "0";
    private String ts_food_service_included = "0";
    private String ts_consign_price_service_included = "0";
    private String ts_consign_service_included = "0";
    private String ts_admin_order_service_included = "0";
    private String ts_admin_basic_info_service_included = "0";
    private String ts_admin_route_service_included = "0";
    private String ts_admin_travel_service_included = "0";
    private String ts_admin_user_service_included = "0";
    private String ts_news_service_included = "0";
    private String ts_ticket_office_service_included = "0";
    private String ts_voucher_service_included = "0";

    //    private String cr_servicename;
    //    private String sr_servicename;
    public TracePassServcie(String trace_id, String ts_ui_dashboard_included, String ts_login_service_included) {
        this.trace_id = trace_id;
        this.ts_ui_dashboard_included = ts_ui_dashboard_included;
        this.ts_login_service_included = ts_login_service_included;
    }

    public TracePassServcie(String trace_id, Map<String, String> tracePassService) {
        this.trace_id = trace_id;
        for (String key : tracePassService.keySet()) {
            if ("ts_ui_dashboard_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_ui_dashboard_included = "1";
            }
            if ("ts_login_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_login_service_included = "1";
            }
            if ("ts_register_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_register_service_included = "1";
            }
            if ("ts_sso_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_sso_service_included = "1";
            }
            if ("ts_verification_code_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_verification_code_service_included = "1";
            }
            if ("ts_contacts_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_contacts_service_included = "1";
            }
            if ("ts_order_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_order_service_included = "1";
            }
            if ("ts_order_other_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_order_other_service_included = "1";
            }
            if ("ts_config_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_config_service_included = "1";
            }
            if ("ts_station_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_station_service_included = "1";
            }
            if ("ts_train_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_train_service_included = "1";
            }
            if ("ts_travel_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_travel_service_included = "1";
            }
            if ("ts_travel2_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_travel2_service_included = "1";
            }
            if ("ts_preserve_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_preserve_service_included = "1";
            }
            if ("ts_preserve_other_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_preserve_other_service_included = "1";
            }
            if ("ts_basic_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_basic_service_included = "1";
            }

            if ("ts_ticketinfo_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_ticketinfo_service_included = "1";
            }
            if ("ts_price_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_price_service_included = "1";
            }
            if ("ts_notification_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_notification_service_included = "1";
            }
            if ("ts_security_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_security_service_included = "1";
            }
            if ("ts_inside_payment_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_inside_payment_service_included = "1";
            }
            if ("ts_execute_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_execute_service_included = "1";
            }
            if ("ts_payment_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_payment_service_included = "1";
            }
            if ("ts_rebook_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_rebook_service_included = "1";
            }
            if ("ts_cancel_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_cancel_service_included = "1";
            }
            if ("ts_route_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_route_service_included = "1";
            }
            if ("ts_assurance_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_assurance_service_included = "1";
            }
            if ("ts_seat_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_seat_service_included = "1";
            }
            if ("ts_travel_plan_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_travel_plan_service_included = "1";
            }
            if ("ts_route_plan_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_route_plan_service_included = "1";
            }

            if ("ts_food_map_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_food_map_service_included = "1";
            }
            if ("ts_food_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_food_service_included = "1";
            }

            if ("ts_consign_price_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_consign_price_service_included = "1";
            }

            if ("ts_consign_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_consign_service_included = "1";
            }

            if ("ts_admin_order_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_admin_order_service_included = "1";
            }

            if ("ts_admin_basic_info_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_admin_basic_info_service_included = "1";
            }

            if ("ts_admin_route_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_admin_route_service_included = "1";
            }

            if ("ts_admin_travel_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_admin_travel_service_included = "1";
            }

            if ("ts_admin_user_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_admin_user_service_included = "1";
            }
            if ("ts_news_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_news_service_included = "1";
            }
            if ("ts_ticket_office_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_ticket_office_service_included = "1";
            }
            if ("ts_voucher_service_included".contains(tracePassService.get(key).replaceAll("-", "_"))) {
                this.ts_voucher_service_included = "1";
            }
        }
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getTs_ui_dashboard_included() {
        return ts_ui_dashboard_included;
    }

    public void setTs_ui_dashboard_included(String ts_ui_dashboard_included) {
        this.ts_ui_dashboard_included = ts_ui_dashboard_included;
    }

    public String getTs_login_service_included() {
        return ts_login_service_included;
    }

    public void setTs_login_service_included(String ts_login_service_included) {
        this.ts_login_service_included = ts_login_service_included;
    }

    public String getTs_register_service_included() {
        return ts_register_service_included;
    }

    public void setTs_register_service_included(String ts_register_service_included) {
        this.ts_register_service_included = ts_register_service_included;
    }

    public String getTs_sso_service_included() {
        return ts_sso_service_included;
    }

    public void setTs_sso_service_included(String ts_sso_service_included) {
        this.ts_sso_service_included = ts_sso_service_included;
    }

    public String getTs_verification_code_service_included() {
        return ts_verification_code_service_included;
    }

    public void setTs_verification_code_service_included(String ts_verification_code_service_included) {
        this.ts_verification_code_service_included = ts_verification_code_service_included;
    }

    public String getTs_contacts_service_included() {
        return ts_contacts_service_included;
    }

    public void setTs_contacts_service_included(String ts_contacts_service_included) {
        this.ts_contacts_service_included = ts_contacts_service_included;
    }

    public String getTs_order_service_included() {
        return ts_order_service_included;
    }

    public void setTs_order_service_included(String ts_order_service_included) {
        this.ts_order_service_included = ts_order_service_included;
    }

    public String getTs_order_other_service_included() {
        return ts_order_other_service_included;
    }

    public void setTs_order_other_service_included(String ts_order_other_service_included) {
        this.ts_order_other_service_included = ts_order_other_service_included;
    }

    public String getTs_config_service_included() {
        return ts_config_service_included;
    }

    public void setTs_config_service_included(String ts_config_service_included) {
        this.ts_config_service_included = ts_config_service_included;
    }

    public String getTs_station_service_included() {
        return ts_station_service_included;
    }

    public void setTs_station_service_included(String ts_station_service_included) {
        this.ts_station_service_included = ts_station_service_included;
    }

    public String getTs_train_service_included() {
        return ts_train_service_included;
    }

    public void setTs_train_service_included(String ts_train_service_included) {
        this.ts_train_service_included = ts_train_service_included;
    }

    public String getTs_travel_service_included() {
        return ts_travel_service_included;
    }

    public void setTs_travel_service_included(String ts_travel_service_included) {
        this.ts_travel_service_included = ts_travel_service_included;
    }

    public String getTs_travel2_service_included() {
        return ts_travel2_service_included;
    }

    public void setTs_travel2_service_included(String ts_travel2_service_included) {
        this.ts_travel2_service_included = ts_travel2_service_included;
    }

    public String getTs_preserve_service_included() {
        return ts_preserve_service_included;
    }

    public void setTs_preserve_service_included(String ts_preserve_service_included) {
        this.ts_preserve_service_included = ts_preserve_service_included;
    }

    public String getTs_preserve_other_service_included() {
        return ts_preserve_other_service_included;
    }

    public void setTs_preserve_other_service_included(String ts_preserve_other_service_included) {
        this.ts_preserve_other_service_included = ts_preserve_other_service_included;
    }

    public String getTs_basic_service_included() {
        return ts_basic_service_included;
    }

    public void setTs_basic_service_included(String ts_basic_service_included) {
        this.ts_basic_service_included = ts_basic_service_included;
    }

    public String getTs_ticketinfo_service_included() {
        return ts_ticketinfo_service_included;
    }

    public void setTs_ticketinfo_service_included(String ts_ticketinfo_service_included) {
        this.ts_ticketinfo_service_included = ts_ticketinfo_service_included;
    }

    public String getTs_price_service_included() {
        return ts_price_service_included;
    }

    public void setTs_price_service_included(String ts_price_service_included) {
        this.ts_price_service_included = ts_price_service_included;
    }

    public String getTs_notification_service_included() {
        return ts_notification_service_included;
    }

    public void setTs_notification_service_included(String ts_notification_service_included) {
        this.ts_notification_service_included = ts_notification_service_included;
    }

    public String getTs_security_service_included() {
        return ts_security_service_included;
    }

    public void setTs_security_service_included(String ts_security_service_included) {
        this.ts_security_service_included = ts_security_service_included;
    }

    public String getTs_inside_payment_service_included() {
        return ts_inside_payment_service_included;
    }

    public void setTs_inside_payment_service_included(String ts_inside_payment_service_included) {
        this.ts_inside_payment_service_included = ts_inside_payment_service_included;
    }

    public String getTs_execute_service_included() {
        return ts_execute_service_included;
    }

    public void setTs_execute_service_included(String ts_execute_service_included) {
        this.ts_execute_service_included = ts_execute_service_included;
    }

    public String getTs_payment_service_included() {
        return ts_payment_service_included;
    }

    public void setTs_payment_service_included(String ts_payment_service_included) {
        this.ts_payment_service_included = ts_payment_service_included;
    }

    public String getTs_rebook_service_included() {
        return ts_rebook_service_included;
    }

    public void setTs_rebook_service_included(String ts_rebook_service_included) {
        this.ts_rebook_service_included = ts_rebook_service_included;
    }

    public String getTs_cancel_service_included() {
        return ts_cancel_service_included;
    }

    public void setTs_cancel_service_included(String ts_cancel_service_included) {
        this.ts_cancel_service_included = ts_cancel_service_included;
    }

    public String getTs_route_service_included() {
        return ts_route_service_included;
    }

    public void setTs_route_service_included(String ts_route_service_included) {
        this.ts_route_service_included = ts_route_service_included;
    }

    public String getTs_assurance_service_included() {
        return ts_assurance_service_included;
    }

    public void setTs_assurance_service_included(String ts_assurance_service_included) {
        this.ts_assurance_service_included = ts_assurance_service_included;
    }

    public String getTs_seat_service_included() {
        return ts_seat_service_included;
    }

    public void setTs_seat_service_included(String ts_seat_service_included) {
        this.ts_seat_service_included = ts_seat_service_included;
    }

    public String getTs_travel_plan_service_included() {
        return ts_travel_plan_service_included;
    }

    public void setTs_travel_plan_service_included(String ts_travel_plan_service_included) {
        this.ts_travel_plan_service_included = ts_travel_plan_service_included;
    }

    public String getTs_route_plan_service_included() {
        return ts_route_plan_service_included;
    }

    public void setTs_route_plan_service_included(String ts_route_plan_service_included) {
        this.ts_route_plan_service_included = ts_route_plan_service_included;
    }

    public String getTs_food_map_service_included() {
        return ts_food_map_service_included;
    }

    public void setTs_food_map_service_included(String ts_food_map_service_included) {
        this.ts_food_map_service_included = ts_food_map_service_included;
    }

    public String getTs_food_service_included() {
        return ts_food_service_included;
    }

    public void setTs_food_service_included(String ts_food_service_included) {
        this.ts_food_service_included = ts_food_service_included;
    }

    public String getTs_consign_price_service_included() {
        return ts_consign_price_service_included;
    }

    public void setTs_consign_price_service_included(String ts_consign_price_service_included) {
        this.ts_consign_price_service_included = ts_consign_price_service_included;
    }

    public String getTs_consign_service_included() {
        return ts_consign_service_included;
    }

    public void setTs_consign_service_included(String ts_consign_service_included) {
        this.ts_consign_service_included = ts_consign_service_included;
    }

    public String getTs_admin_order_service_included() {
        return ts_admin_order_service_included;
    }

    public void setTs_admin_order_service_included(String ts_admin_order_service_included) {
        this.ts_admin_order_service_included = ts_admin_order_service_included;
    }

    public String getTs_admin_basic_info_service_included() {
        return ts_admin_basic_info_service_included;
    }

    public void setTs_admin_basic_info_service_included(String ts_admin_basic_info_service_included) {
        this.ts_admin_basic_info_service_included = ts_admin_basic_info_service_included;
    }

    public String getTs_admin_route_service_included() {
        return ts_admin_route_service_included;
    }

    public void setTs_admin_route_service_included(String ts_admin_route_service_included) {
        this.ts_admin_route_service_included = ts_admin_route_service_included;
    }

    public String getTs_admin_travel_service_included() {
        return ts_admin_travel_service_included;
    }

    public void setTs_admin_travel_service_included(String ts_admin_travel_service_included) {
        this.ts_admin_travel_service_included = ts_admin_travel_service_included;
    }

    public String getTs_admin_user_service_included() {
        return ts_admin_user_service_included;
    }

    public void setTs_admin_user_service_included(String ts_admin_user_service_included) {
        this.ts_admin_user_service_included = ts_admin_user_service_included;
    }

    public String getTs_news_service_included() {
        return ts_news_service_included;
    }

    public void setTs_news_service_included(String ts_news_service_included) {
        this.ts_news_service_included = ts_news_service_included;
    }

    public String getTs_ticket_office_service_included() {
        return ts_ticket_office_service_included;
    }

    public void setTs_ticket_office_service_included(String ts_ticket_office_service_included) {
        this.ts_ticket_office_service_included = ts_ticket_office_service_included;
    }

    public String getTs_voucher_service_included() {
        return ts_voucher_service_included;
    }

    public void setTs_voucher_service_included(String ts_voucher_service_included) {
        this.ts_voucher_service_included = ts_voucher_service_included;
    }
}
