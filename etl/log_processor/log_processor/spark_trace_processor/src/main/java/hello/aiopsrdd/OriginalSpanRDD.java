package hello.aiopsrdd;

import hello.domain.TraceAnnotation;
import hello.util.MatcherUrlRouterUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;

public class OriginalSpanRDD {

    public static JavaRDD<TraceAnnotation> getOriginalSpanRdd(SparkSession spark, String trace_csv_url) {
        JavaRDD<TraceAnnotation> originalSpanRdd = spark.read()
                .textFile(trace_csv_url)
                .javaRDD()
                .map(line -> {
                    String[] parts = line.split(",");
                    TraceAnnotation traceAnnotation = new TraceAnnotation();

                    traceAnnotation.setTrace_id(parts[0]);
                    traceAnnotation.setSpan_name(parts[1]);
                    traceAnnotation.setSpan_id(parts[2]);
                    traceAnnotation.setParent_id(parts[3]);
                    traceAnnotation.setSpan_timestamp(parts[4]);
                    traceAnnotation.setSpan_duration(parts[5]);

                    traceAnnotation.setAnno_a1_timestamp(parts[6]);
                    traceAnnotation.setAnno_a1_value(parts[7]);
                    traceAnnotation.setAnno_a1_ipv4(parts[8]);
                    traceAnnotation.setAnno_a1_port(parts[9]);
                    traceAnnotation.setAnno_a1_servicename(parts[10]);

                    traceAnnotation.setAnno_a2_timestamp(parts[11]);
                    traceAnnotation.setAnno_a2_value(parts[12]);
                    traceAnnotation.setAnno_a2_ipv4(parts[13]);
                    traceAnnotation.setAnno_a2_port(parts[14]);
                    traceAnnotation.setAnno_a2_servicename(parts[15]);

                    traceAnnotation.setBnno_component(parts[16]);


                    traceAnnotation.setBnno_node_id(MatcherUrlRouterUtil.nodeIdMatcherPattern(parts[17]));
                    // traceAnnotation.setBnno_node_id(parts[17]);
                    traceAnnotation.setBnno_xrequest_id(parts[18]);

                    // 过滤
                    traceAnnotation.setBnno_httpurl(MatcherUrlRouterUtil.matcherPattern(parts[19]));

                    traceAnnotation.setBnno_http_method(parts[20]);
                    traceAnnotation.setBnno_downstream_cluster(parts[21]);

                    traceAnnotation.setTest_trace_id(parts[22]);
                    traceAnnotation.setTest_case_id(parts[23]);

                    traceAnnotation.setBnno_http_protocol(parts[24]);
                    traceAnnotation.setBnno_request_size(parts[25]);
                    traceAnnotation.setBnno_upstream_cluster(parts[26]);

                    traceAnnotation.setBnno_status_code(parts[27]);
                    // traceAnnotation.setBnno_status_code(StatusCodeUtil.genInstanceStatus(parts[27]));

                    traceAnnotation.setBnno_response_size(parts[28]);
                    traceAnnotation.setBnno_response_flags(parts[29]);

                    return traceAnnotation;
                });

        // 设置parent id 为空的span_duration , 因收集的parent id 为空的 duration 为空
        JavaRDD<TraceAnnotation> changeSpanTimeRdd = originalSpanRdd.map(new Function<TraceAnnotation, TraceAnnotation>() {
            @Override
            public TraceAnnotation call(TraceAnnotation traceAnnotation) throws Exception {

                if ("".equals(traceAnnotation.getParent_id()) || traceAnnotation.getParent_id() == null) {
                    // trace  duration
                    //    String result = new BigDecimal((double) span_duration / 1000000 + "").toString();
                    traceAnnotation.setSpan_duration(Long.valueOf(traceAnnotation.getAnno_a2_timestamp()) - Long.valueOf(traceAnnotation.getAnno_a1_timestamp()) + "");
                }
                return traceAnnotation;
            }
        });
        return changeSpanTimeRdd;
    }
}
