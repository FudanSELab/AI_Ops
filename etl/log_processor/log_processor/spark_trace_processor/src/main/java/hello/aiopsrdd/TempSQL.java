package hello.aiopsrdd;

public class TempSQL {
//    invocation
//    cpu_memory

    // 产生真正的span 中间表， 由此表生成invocation 表
    public static String genRealSpan =
            "select a.trace_id, a.span_name, a.span_id, a.parent_id, a.span_timestamp, a.span_duration, " +
                    "a.anno_a1_timestamp cs_timestamp, a.anno_a1_ipv4 cs_ipv4, a.anno_a1_servicename cs_servicename, " +
                    "a.anno_a2_timestamp cr_timestamp, a.anno_a2_ipv4 cr_ipv4, a.anno_a2_servicename cr_servicename, " +
                    "b.anno_a1_timestamp sr_timestamp, b.anno_a1_ipv4 sr_ipv4, b.anno_a1_servicename sr_servicename, " +
                    "b.anno_a2_timestamp ss_timestamp, b.anno_a2_ipv4 ss_ipv4, b.anno_a2_servicename ss_servicename, " +
                    "a.bnno_node_id c_node_id, b.bnno_node_id s_node_id, a.bnno_httpurl req_api, a.test_trace_id, " +
                    "a.test_case_id, a.bnno_status_code status_code " +
            "from  original_span_table a, original_span_table b where (a.span_id == b.span_id And a.parent_id == b.parent_id And a.anno_a1_value == 'cs' And a.parent_id  != '' And  (b.span_timestamp == ''  or b.span_timestamp == '0')) or (a.span_id == b.span_id And a.parent_id == b.parent_id And a.anno_a1_value == 'sr' And a.parent_id  == '')";

    //  由临时表 real_span_trace 产生 invocation
    public static String genInvocation =
            "select  trace_id, span_id, span_timestamp, test_trace_id, test_case_id," +
                    "abs(sr_timestamp - cs_timestamp)  req_latency, parent_id, cs_servicename req_service," +
                    "req_api, c_node_id req_inst_id ,cs_ipv4 req_inst_ip, span_duration duration," +
                    "status_code res_status_code, 0 res_status_desc," +
                    "0 res_exception, \""+"abs(ss_timestamp - cr_timestamp)" +"\" res_latency," +
                    "0 req_param, 0 exec_logs, 0 res_body " +
                    "from real_span_trace";

    //  由 service_config_data 和 service_instance_data  产生 cpu_memory_view
    public static String genCpuMemory =
            "select a.*, b.*  from service_config_data a, service_instance_data b " +
                    "where (a.ts_travel_service_start_time == b.ts_cancel_service_inst_collect_start_time)";

    // 由real_span_trace表 查询出一个服务经过的service
    public static String getTracePassService =
            "select trace_id, concat_ws(',', collect_set(cr_servicename)) ts_ui_dashboard_included, concat_ws(',', collect_set(sr_servicename)) ts_login_service_included from real_span_trace group by trace_id";

    // 合并invocation 和 trace_pass_service
    public static String genBeforeTrace =
            "select a.test_trace_id, a.test_case_id, a.req_service entry_service, " +
                    " a.req_api entry_api, a.span_timestamp  entry_timestamp, (a.span_timestamp + a.duration) exit_timestamp, " +
                    " a.duration , b.* from real_invocation_view a, real_trace_pass_view b  " +
                    "where a.trace_id == b.trace_id And a.req_latency == '0' And a.parent_id == ''";


    // real_invocation_view ,  cpu_memory_view , real_trace_pass_view  产生 Trace 表
    public static String genRealTrace  =
            "select a.*, b.* , 0 y_exec_result, 0 y_issue_dim_type, 0 y_issue_dim_content  from  before_trace_view a, real_cpu_memory_view  b  " +
                    "where (a.entry_timestamp + 60000) > b.ts_travel_service_start_time";










    public static String configSpanSql = "select a.trace_id, a.span_id , a.span_name," +
            "a.parent_id,a.span_timestamp,a.span_duration, " +
            "a.anno_cs_timestamp, a.anno_cs,a.anno_cs_servicename, a.anno_cs_ip,a.anno_cs_port, " +
            "a.anno_cr_timestamp, a.anno_cr,a.anno_cr_servicename, a.anno_cr_ip,a.anno_cr_port, " +
            "b.anno_sr_timestamp, b.anno_sr, b.anno_sr_servicename,b.anno_sr_ip,b.anno_sr_port, " +
            "b.anno_ss_timestamp, b.anno_ss, b.anno_ss_servicename,b.anno_ss_ip,b.anno_ss_port, " +
            "b.status_code, b.res_status_desc, b.res_exception, b.is_error " +
            "from trace_anno a, trace_anno b where (a.span_id == b.parent_id and a.trace_id == b.trace_id and a.span_name == b.span_name ) or (a.trace_id == b.trace_id and a.span_name == b.span_name and a.span_id == b.span_id and a.parent_id=='')";
//
    public static String genInvocation2
            = "select span_id  invocation_id, trace_id, 0 session_id, span_timestamp ," +
            "abs(anno_sr_timestamp - anno_cs_timestamp)  req_duration ," +
            "anno_cs_servicename req_service,span_name req_api, 0 req_param," +
            "span_duration exec_duration, 0 exec_logs, status_code res_status_code," +
            "res_status_desc, res_exception, 0 res_body," +
            "abs(anno_cr_timestamp - anno_ss_timestamp) res_duration," +
            "is_error FROM temp_trace_anno";

    public static String genTrace ="select a.trace_id,0 session_id, a.anno_sr_servicename entry_service, a.span_name entry_api, a.anno_sr_timestamp  entry_timestamp, " +
        "0 y_is_error_lazy, 0 y_is_error_predict, a.is_error y_is_error, " +
        "0 y_is_valid,  0 y_issue_ms, 0  y_issue_dimension , " +
        "b.* from  temp_trace_anno a , temp_cpu_memory b " +
        "where   ((a.trace_id == a.span_id) And (a.span_timestamp <  b.ts_account_mongo_time) And  (a.span_timestamp+60000 > b.ts_account_mongo_time))";
}
