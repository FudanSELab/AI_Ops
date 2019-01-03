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
                    "a.bnno_httpurl c_req_api, b.bnno_httpurl s_req_api, " +
                    "a.bnno_node_id c_inst_id, b.bnno_node_id s_inst_id, " +
                    "a.bnno_status_code c_status_code, b.bnno_status_code s_status_code,"+
                    "a.test_trace_id, a.test_case_id, " +
                    "a.bnno_http_method req_type " +
            "from  original_span_table a, original_span_table b " +
                    "where (a.span_id == b.span_id And a.parent_id == b.parent_id And a.anno_a1_value == 'cs' And a.parent_id  != '' And  (b.span_timestamp == ''  or b.span_timestamp == '0')) or (a.span_id == b.span_id And a.parent_id == b.parent_id And a.anno_a1_value == 'sr' And a.parent_id  == '')";

    //  由临时表 real_span_trace 产生 invocation
    // gen real_invocation_view
    public static String genInvocation =
            "select  trace_id, span_id, span_timestamp, test_trace_id, test_case_id," +
                    "abs(sr_timestamp - cs_timestamp)  req_latency, parent_id, cs_servicename req_service," +
                    "c_req_api req_api, c_inst_id req_inst_id ,cs_ipv4 req_inst_ip, span_duration duration," +
                    "c_status_code res_status_code, req_type, 0 res_status_desc," +
                    "0 res_exception, abs(ss_timestamp - cr_timestamp) res_latency," +
                    "0 req_param, 0 exec_logs, 0 res_body " +
                    "from real_span_trace_view";

    //  由 service_config_data 和 service_instance_data  产生 cpu_memory_view
    public static String genCpuMemory =
            "select a.*, b.*  from service_config_data a, service_instance_data b " +
                    "where (a.ts_travel_service_start_time == b.ts_order_service_inst_start_time)";

    public static String config_server_view =
            "select start_time , end_time , " +
                    "concat_ws(',', collect_list(serviceName)) serviceName , " +
                    "concat_ws(',', collect_list(l_cpu)) l_cpu , " +
                    "concat_ws(',', collect_list(l_memory)) l_memory , " +
                    "concat_ws(',', collect_list(confNumber)) confNumber , " +
                    "concat_ws(',', collect_list(readyNumber)) readyNumber , " +
                    "concat_ws(',', collect_list(healthCheckDownDelay)) healthCheckDownDelay , " +
                    "concat_ws(',', collect_list(healthCheckReadyDelay)) healthCheckReadyDelay  " +
                    "from config_server_view group by start_time, end_time";
    // 由real_span_trace表 查询出一个服务经过的service
    public static String getTracePassService =
            "select trace_id, " +
                    "concat_ws(',', collect_list(cr_servicename)) cr_service_included,  " +
                    "concat_ws(',', collect_list(cast(abs(cr_timestamp - cs_timestamp) as string))) crs_time, " +
                    "concat_ws(',', collect_list(c_req_api)) c_req_api, "+
                    "concat_ws(',', collect_list(c_inst_id)) c_inst_id, "+
                    "concat_ws(',', collect_list(c_status_code)) c_status_code, "+
                    "concat_ws(',', collect_list(sr_servicename)) sr_service_included, " +
                    "concat_ws(',', collect_list(cast(abs(ss_timestamp - sr_timestamp) as string))) ssr_time, " +
                    "concat_ws(',', collect_list(s_req_api)) s_req_api, "+
                    "concat_ws(',', collect_list(s_inst_id)) s_inst_id, "+
                    "concat_ws(',', collect_list(s_status_code)) s_status_code, "+
                    "cast(count(*) as string) trace_service_span "+
                    "from real_span_trace_view group by trace_id";

    // 合并invocation 和 trace_pass_service
    // gen: before_trace_view
    public static String combinePassServiceToTrace =
            "select a.test_trace_id, a.test_case_id,  concat_ws('_', req_service, req_api) trace_type, a.req_service  trace_service, " +
                    "a.req_api trace_api, a.req_type  trace_req_type, a.span_timestamp  entry_timestamp, " +
                    "cast(abs(a.span_timestamp + a.duration) as string) exit_timestamp, " +
                    "a.duration trace_duration_ql, b.* " +
                    "from  real_invocation_view a, real_trace_pass_view b  " +
                    "where a.trace_id == b.trace_id And a.req_latency == '0' And a.parent_id == '' ";


    // service_config_data
    public static String combineServiceConfigToTrace =
            "select a.* , b.* from   trace_combine_instance a, service_config_data  b " +
            "where ( ( (( substr( a.entry_timestamp , 1 , 13) - 30001) < b.start_time )  And  ( ( substr( a.entry_timestamp , 1 , 13) + 1 ) > b.start_time))  Or " +
                   " ( (( substr( a.entry_timestamp , 1 , 13) - 15000) < b.start_time )  And  ( ( substr( a.entry_timestamp , 1 , 13) + 15000) > b.start_time))  Or " +
                   " ( (( substr( a.entry_timestamp , 1 , 13) - 30000) < b.start_time )  And  ( ( substr( a.entry_timestamp , 1 , 13) + 15000) > b.start_time))  Or " +
                   " ( (( substr( a.entry_timestamp , 1 , 13) - 30000) < b.start_time )  And  ( ( substr( a.entry_timestamp , 1 , 13) + 30000) > b.start_time)))";

    // real_invocation_view ,  cpu_memory_view , real_trace_pass_view  产生 Trace 表
    public static String genRealTrace  =
            "select a.*, b.*  from  trace_passservice_view a, real_cpu_memory_view  b  " +
                    "where (((a.entry_timestamp + 60000) > b.ts_travel_service_start_time )  And  (a.entry_timestamp < b.ts_travel_service_end_time))";

  //   trace_combine_config_view
   public static String combineYtoTrace =
           "select a.*, cast(b.expected_result as string), cast(b.error as string) y_exec_result , " +
                   " b.y_issue_ms, b.y_issue_dim_type, b.y_issue_dim_content  from trace_combine_config_view a, test_traces_mysql_view b  " +
                   " where (a.test_trace_id == b.test_trace_id) And (a.test_case_id == b.test_case_id)";


  // sequence_view  trace_final_view
    // final_seq   final_trace
   public static String trace_finalDateSet =
           "select  a.*, b.*  from  trace_y a , final_seq2 b where a.trace_id == b.trace_id1";







   ///////////////////////////////////////////////////////////////////

//    public static String configSpanSql = "select a.trace_id, a.span_id , a.span_name," +
//            "a.parent_id,a.span_timestamp,a.span_duration, " +
//            "a.anno_cs_timestamp, a.anno_cs,a.anno_cs_servicename, a.anno_cs_ip,a.anno_cs_port, " +
//            "a.anno_cr_timestamp, a.anno_cr,a.anno_cr_servicename, a.anno_cr_ip,a.anno_cr_port, " +
//            "b.anno_sr_timestamp, b.anno_sr, b.anno_sr_servicename,b.anno_sr_ip,b.anno_sr_port, " +
//            "b.anno_ss_timestamp, b.anno_ss, b.anno_ss_servicename,b.anno_ss_ip,b.anno_ss_port, " +
//            "b.status_code, b.res_status_desc, b.res_exception, b.is_error " +
//            "from trace_anno a, trace_anno b where (a.span_id == b.parent_id and a.trace_id == b.trace_id and a.span_name == b.span_name ) or (a.trace_id == b.trace_id and a.span_name == b.span_name and a.span_id == b.span_id and a.parent_id=='')";
////
//    public static String genInvocation2
//            = "select span_id  invocation_id, trace_id, 0 session_id, span_timestamp ," +
//            "abs(anno_sr_timestamp - anno_cs_timestamp)  req_duration ," +
//            "anno_cs_servicename req_service,span_name req_api, 0 req_param," +
//            "span_duration exec_duration, 0 exec_logs, status_code res_status_code," +
//            "res_status_desc, res_exception, 0 res_body," +
//            "abs(anno_cr_timestamp - anno_ss_timestamp) res_duration," +
//            "is_error FROM temp_trace_anno";
//
//    public static String genTrace ="select a.trace_id,0 session_id, a.anno_sr_servicename entry_service, a.span_name entry_api, a.anno_sr_timestamp  entry_timestamp, " +
//        "0 y_is_error_lazy, 0 y_is_error_predict, a.is_error y_is_error, " +
//        "0 y_is_valid,  0 y_issue_ms, 0  y_issue_dimension , " +
//        "b.* from  temp_trace_anno a , temp_cpu_memory b " +
//        "where   ((a.trace_id == a.span_id) And (a.span_timestamp <  b.ts_account_mongo_time) And  (a.span_timestamp+60000 > b.ts_account_mongo_time))";
//
//
    // genSequencePart: 3 steps
    public static String genStep1 =
            "select trace_id ,test_trace_id, test_case_id, sr_timestamp s_time, ss_timestamp e_time, sr_servicename, " +
                    "cr_servicename caller " +
                    " from real_span_trace_view  where  sr_servicename != cr_servicename ";

    public static String genStep2 =
            "select trace_id , caller , " +
                    " count(sr_servicename) caller_times , " +
                    " concat_ws(',', collect_set(test_trace_id)) test_trace_id, " +
                    " concat_ws(',', collect_set(test_case_id)) test_case_id, " +
                    " concat_ws(',', collect_set(s_time)) s_time, " +
                    " concat_ws(',', collect_set(e_time)) e_time, " +
                    " concat_ws(',', collect_list(sr_servicename)) sr_servicename  " +
                    " from view_clean_step1 group by trace_id , caller having count(sr_servicename) >= 2";

    public static String genStep3 =
            "select trace_id , " +
                    "concat_ws('___' , collect_set(test_trace_id)) test_trace_id, " +
                    "concat_ws('___' , collect_set(test_case_id)) test_case_id, " +
                    "concat_ws('___' , collect_set(caller)) caller, " +
                    "concat_ws('___' , collect_set(caller_times)) caller_times, "+
                    "concat_ws('___', collect_set(s_time)) s_time, " +
                    "concat_ws('___', collect_set(e_time)) e_time, " +
                    "concat_ws('___', collect_set(sr_servicename)) sr_servicename  " +
                    "from view_clean_step2  group by trace_id ";
}
