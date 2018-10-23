package hello.aiopsrdd;

import hello.domain.TraceAnnotation;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;


public class AiOpsRDD {
    public static void executor() {

        System.out.println("==============spark sql ====");
        SparkSession spark = SparkSession
                .builder()
                .config("spark.debug.maxToStringFields", 1000)
                .config("spark.sql.crossJoin.enabled", true)
                .appName("java spark sql")
                .master("yarn")
                .getOrCreate();

        filter(spark);

        spark.close();
        System.out.println("==============spark sql closed====");
    }

    private static void filter(SparkSession spark) {

        // step 1 : read original span logs as RDD
        JavaRDD<TraceAnnotation> originalSpanRdd = spark.read()
                .textFile("hdfs://10.141.211.173:8020/user/admin/new_span_trace.csv")
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
                    traceAnnotation.setBnno_node_id(parts[17]);
                    traceAnnotation.setBnno_xrequest_id(parts[18]);
                    traceAnnotation.setBnno_httpurl(parts[19]);
                    traceAnnotation.setBnno_http_method(parts[20]);
                    traceAnnotation.setBnno_downstream_cluster(parts[21]);

                    traceAnnotation.setTest_trace_id(parts[22]);
                    traceAnnotation.setTest_case_id(parts[23]);

                    traceAnnotation.setBnno_http_protocol(parts[24]);
                    traceAnnotation.setBnno_request_size(parts[25]);
                    traceAnnotation.setBnno_upstream_cluster(parts[26]);

                    traceAnnotation.setBnno_status_code(parts[27]);
                    traceAnnotation.setBnno_response_size(parts[28]);
                    traceAnnotation.setBnno_response_flags(parts[29]);

                    return traceAnnotation;
                });

        // 设置parent id 为空的span_duration , 因收集的parent id 为空的 duration 为空
        JavaRDD<TraceAnnotation> changeSpanTimeRdd = originalSpanRdd.map(new Function<TraceAnnotation, TraceAnnotation>() {
            @Override
            public TraceAnnotation call(TraceAnnotation traceAnnotation) throws Exception {
                Long span_duration = 0L;
                if ("".equals(traceAnnotation.getParent_id()) || traceAnnotation.getParent_id() == null) {
                    span_duration = Long.valueOf(traceAnnotation.getAnno_a2_timestamp()) - Long.valueOf(traceAnnotation.getAnno_a1_timestamp());
                    traceAnnotation.setSpan_duration(span_duration + "");
                }
                return traceAnnotation;
            }
        });

        System.out.println("---------------  originalSpanRdd created ---------------");

        // step 2 : create a temp original span table
        Dataset<Row> originalSpanDateset = spark.createDataFrame(changeSpanTimeRdd, TraceAnnotation.class);
        originalSpanDateset.createOrReplaceTempView("original_span_table");

        // step 3 : create real span   dataset
        Dataset<Row> realSpanDataset = spark.sql(TempSQL.genRealSpan);

        //step 4 :  reduce same span id
        String[] duplicasKey = new String[]{"span_id"};
        realSpanDataset = realSpanDataset.dropDuplicates(duplicasKey);

       //step 5 :  create real span cs  cr  ss  sr
        realSpanDataset.printSchema();
        realSpanDataset.createOrReplaceTempView("real_span_trace");
       // realSpanDataset.write().saveAsTable("real_span_trace");
        System.out.println("---------------  real_span_trace table created ---------------");

       // step 6 : create invocation
        Dataset<Row> invocationDataset = spark.sql(TempSQL.genInvocation);
        invocationDataset.show();
        invocationDataset.printSchema();
        invocationDataset.write().saveAsTable("real_invocation");
        System.out.println("---------------  real_invocation table created ---------------");


        // read service config data
        Dataset<Row> serviceConfigData = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/serviceConfigData.csv");
        serviceConfigData.printSchema();
        System.out.println("--------------print servcie config schema --------------");
        serviceConfigData.createOrReplaceTempView("service_config_data");

        // read service instance data
        Dataset<Row> serviceInstanceData = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/serviceConfigData.csv");
        serviceInstanceData.printSchema();
        System.out.println("--------------print servcie instance schema --------------");
        serviceConfigData.createOrReplaceTempView("service_instance_data");



//        Dataset<Row> invocation = spark.sql("select (ts_account_mongo_time + 100000) - (ts_consign_mongo_time) from cpu_memory");
//        invocation.show();



//        // regist temp table   trace_anno  span
//        Dataset<Row> traceDF2 = spark.createDataFrame(multipleNumberRDD, TraceAnnotation.class);
//        traceDF2.createOrReplaceTempView("trace_anno");
//        System.out.println("--------------  schema ----------------");
//        traceDF2.printSchema();
//
//        // config  span cs cr ss sr  use origin  trace_anno
//        Dataset<Row> tempIn = spark.sql(TempSQL.configSpanSql);
//        System.out.println("===========================temp show ===========");
//         tempIn.createOrReplaceTempView("temp_trace_anno");
////        tempIn.write().saveAsTable("temp_trace_anno");
//
//        // gen invocation  use  temp_trace_anno
//        Dataset<Row> invocation = spark.sql(TempSQL.genInvocation);
//        invocation.createOrReplaceTempView("temp_invocation");
//        invocation.write().saveAsTable("temp_invocation");
//        Dataset<Row> cpuMemory = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/CpuMemoryTelemetry.csv");
//        cpuMemory.printSchema();
//        System.out.println("--------------cpu_memory --------------");
//        cpuMemory.createOrReplaceTempView("temp_cpu_memory");
//
//        cpuMemory.write().saveAsTable("cpu_memory");
//        System.out.println("-------------- gen trace--------------");
//        Dataset<Row> trace = spark.sql(TempSQL.genTrace);
//        trace.show();
//        trace.write().saveAsTable("trace3");
    }
}