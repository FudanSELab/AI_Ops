package hello;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.Arrays;

@EnableAutoConfiguration
@SpringBootApplication
public class ImpalaApplication {

    public static void hiveSql() throws SQLException {
        // String url = "jdbc:hive2://10.141.211.173:10000/default;auth=noSasl";
        String url = "jdbc:hive2://10.141.211.173:10000/default";
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = DriverManager.getConnection(url, "hdfs", "");
        Statement stmt = conn.createStatement();
//        System.out.println("================ create table  =======================");
//        stmt.execute("create table if not exists invocation(invocation_id string , trace_id string, session_id string, " +
//                " req_duration string, req_service string , req_api string, req_params string, exec_duration string," +
//                "exec_logs string, res_status_code string, res_body string, res_duration string, is_error string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n'");
//        System.out.println("================ create table  end=======================");

        System.out.println("================ begin =======================");
        String sql2 = "select * from traces_anno_test";
        ResultSet res2 = stmt.executeQuery(sql2);

        while (res2.next()) {

//               "trace_id",            "span_id", "span_name",            "parent_id",  "span_timestamp", "span_duration", 6
//               "anno_cs_timestamp", "anno_cs", "anno_cs_servicename", "anno_cs_ip", "anno_cs_port",  11
//               "anno_cr_timestamp", "anno_cr", "anno_cr_servicename", "anno_cr_ip", "anno_cr_port",  16
//               "anno_sr_timestamp", "anno_sr", "anno_sr_servicename", "anno_sr_ip", "anno_sr_port", 21
//               "anno_ss_timestamp", "anno_ss", "anno_ss_servicename", "anno_ss_ip", "anno_ss_port"  26

            Invocation tempInvocation = new Invocation();
            tempInvocation.trace_id =  res2.getString(1);
            tempInvocation.exec_duration = res2.getString(6);
            tempInvocation.req_api = res2.getString(3);
            String anno_cr = res2.getString(8);

            if (!"".equals(anno_cr)) {
                tempInvocation.req_duration = Long.parseLong(res2.getString(17)) - Long.parseLong(res2.getString(7))+"";
                tempInvocation.req_service = res2.getString(9);
                tempInvocation.res_duration = Long.parseLong(res2.getString(22)) - Long.parseLong(res2.getString(12))+"";
            }

             String invocation_id = "'"+ tempInvocation.getInvocation_id()+"'";
             String trace_id = "'"+tempInvocation.getTrace_id()+"'";
             String session_id = "'"+tempInvocation.getSession_id()+"'";
             String req_duration = "'"+tempInvocation.getReq_duration()+"'";
             String req_service = "'"+tempInvocation.getReq_service()+"'";
             String req_api = "'"+tempInvocation.getReq_api()+"'";
             String req_param = "'"+tempInvocation.getReq_param()+"'";
             String exec_duration = "'"+tempInvocation.getExec_duration()+"'";
             String exec_logs = "'"+tempInvocation.getExec_logs()+"'";
             String res_status_code = "'"+tempInvocation.getRes_status_code()+"'";
             String res_body = "'"+tempInvocation.getRes_body()+"'";
             String res_duration = "'"+tempInvocation.getRes_duration()+"'";
             String is_error = "'"+tempInvocation.getIs_error()+"'";

            System.out.println(tempInvocation.toString());
            String insertSQL= "insert into invocation(invocation_id, trace_id ,session_id, req_duration, req_service, req_api, req_params,exec_duration, exec_logs, res_status_code, res_body, res_duration, is_error) values ("+
                    invocation_id+","+ trace_id+","+ session_id+","+
                    req_duration+","+req_service+","+req_api+","+
                    req_param+","+exec_duration+","+exec_logs+","+
                    res_status_code+","+res_body+","+res_duration+","+is_error+")";
            stmt.execute(insertSQL);
        }
        System.out.println("==============  end =========================");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ImpalaApplication.class, args);
         hiveSql();
    }
}