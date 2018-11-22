package hello.aiopsrdd;

import hello.domain.Copy_2_of_Service;
import hello.domain.TimeUtil;
import hello.domain.TraceAnnotation;
import hello.domain.TracePassServcie;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    // real_trace_pass_view   real_cpu_memory_view
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
       // realSpanDataset.printSchema();
        realSpanDataset.createOrReplaceTempView("real_span_trace");
        // realSpanDataset.write().saveAsTable("real_span_trace");
        System.out.println("---------------  real_span_trace table created ---------------");

        // step   : get trace pass service , user real_span_trace , gen tempView  real_trace_pass
        // real_trace_pass_view
        calTracePassService(spark);

        // step 6 : create real_invocation_view
        // real_invocation_view
        genRealInvocation(spark);

        // trace and trace pass service
        // before_trace_view
        genBeforeTrace(spark);


        // real_cpu_memory_view
        cpuMemory(spark);

        genRealTrace(spark);

    }


    public static void genRealInvocation(SparkSession spark) {
        Dataset<Row> invocationDataset = spark.sql(TempSQL.genInvocation);
        //invocationDataset.show();
        //invocationDataset.printSchema();
        invocationDataset.createOrReplaceTempView("real_invocation_view");
        //invocationDataset.write().saveAsTable("real_invocation");
        System.out.println("---------------  real_invocation table created ---------------");
    }

    public static Dataset<TracePassServcie> calTracePassService(SparkSession spark) {
        //  read trace passby service, gen new trace_pass_service   by  real_span_trace
        Dataset<Row> tracePassService = spark.sql(TempSQL.getTracePassService);
        Encoder<TracePassServcie> TracePassEncoder = Encoders.bean(TracePassServcie.class);
        Dataset<TracePassServcie> tracePassDataset = tracePassService.map(new MapFunction<Row, TracePassServcie>() {
            @Override
            public TracePassServcie call(Row row) throws Exception {
                String trace_id = row.getAs("trace_id");
                String[] cr_pass_service = row.getAs("ts_ui_dashboard_included").toString().split(",");
                String[] sr_pass_service = row.getAs("ts_login_service_included").toString().split(",");
                Map<String, String> passServiceMap = new HashMap<>();
                for (int i = 0; i < cr_pass_service.length; i++) {
                    if (!passServiceMap.containsKey(cr_pass_service[i]))
                        passServiceMap.put(cr_pass_service[i], cr_pass_service[i]);
                }
                for (int i = 0; i < sr_pass_service.length; i++) {
                    if (!passServiceMap.containsKey(sr_pass_service[i]))
                        passServiceMap.put(sr_pass_service[i], sr_pass_service[i]);
                }
                return new TracePassServcie(trace_id, passServiceMap);
            }
        }, TracePassEncoder);
       // tracePassDataset.printSchema();
     //   tracePassDataset.show();
        tracePassDataset.createOrReplaceTempView("real_trace_pass_view");
        return tracePassDataset;
    }

    // service_config_data, service_instance_data ===> real_cpu_memory_view
    private static void cpuMemory(SparkSession spark) {
        // read service config data
        Dataset<Row> serviceConfigData = spark.read().option("header", "true").csv("hdfs://10.141.211.173:8020/user/admin/serviceConfigData.csv");
        //serviceConfigData.printSchema();
        System.out.println("--------------print servcie config schema --------------");
        serviceConfigData.createOrReplaceTempView("service_config_data");

        // read service instance data
        Dataset<Row> serviceInstanceData = spark.read().option("header", "true").csv("hdfs://10.141.211.173:8020/user/admin/serviceInstanceData.csv");
        //serviceInstanceData.printSchema();
        System.out.println("--------------print servcie instance schema --------------");
        serviceInstanceData.createOrReplaceTempView("service_instance_data");

        Dataset<Row> combineCpuMemory = spark.sql(TempSQL.genCpuMemory);
        //  combineCpuMemory.printSchema();
        // combineCpuMemory.show();
        // combineCpuMemory.write().saveAsTable("real_cpu_memory");
         combineCpuMemory.createOrReplaceTempView("real_cpu_memory_view");
    }

    // real_invocation_view   real_trace_pass_view ,  before_trace_view
    public static void genBeforeTrace(SparkSession spark) {
        Dataset<Row> beforeTraceDataset = spark.sql(TempSQL.genBeforeTrace);
        //beforeTraceDataset.show();
        //beforeTraceDataset.printSchema();
        beforeTraceDataset.createOrReplaceTempView("before_trace_view");
    }

    public static void genRealTrace(SparkSession spark){
        Dataset<Row> realTraceDataset = spark.sql(TempSQL.genRealTrace);
        //realTraceDataset.printSchema();
       // realTraceDataset.show();
        String[] duplicasKey = new String[]{"trace_id"};
        realTraceDataset = realTraceDataset.dropDuplicates(duplicasKey);
        realTraceDataset.write().saveAsTable("real_trace2");
    }

    private static void genSequencePart(SparkSession spark) {
        System.out.println("=======begin==============");

        Dataset<Row> step1Dataset = spark.sql(TempSQL.genStep1);
        String[] duplicasKey = new String[]{"trace_id", "sr_servicename", "caller"};
        step1Dataset = step1Dataset.dropDuplicates(duplicasKey);
        step1Dataset = step1Dataset.orderBy("s_time");
        step1Dataset.createOrReplaceTempView("view_clean_step1");
        //   step1Dataset.write().saveAsTable("genstep1");
        System.out.println("====genStep1==-----");


        Dataset<Row> step2Dataset = spark.sql(TempSQL.genStep2);
        step2Dataset.createOrReplaceTempView("view_clean_step2");
        //  step2Dataset.write().saveAsTable("genstep2");
        System.out.println("====genStep2==-----");


        Dataset<Row> step3Dataset = spark.sql(TempSQL.genStep3);
        //   step3Dataset.write().saveAsTable("genStep3");
        System.out.println("====genStep3==-----");

        JavaRDD<Row> step3Rdd = step3Dataset.javaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                // 最终返回的一行数据
                List<String> rowDataList = new ArrayList<>();

                // 一次循环一个trace
                String trace_id = row.getAs("trace_id");
                rowDataList.add(trace_id);

                // 其他全是逗号分隔的 caller_service 0 对应 s_time 0 的数组
                String[] test_trace_id = row.getAs("test_trace_id").toString().split("___");
                String[] test_case_id = row.getAs("test_case_id").toString().split("___");
                rowDataList.add(test_trace_id[0].split(",")[0]);
                rowDataList.add(test_case_id[0].split(",")[0]);


                String[] caller_service = row.getAs("caller").toString().split("___");
                //  String [] s_time = row.getAs("s_time").toString().split("||");
                String[] e_time = row.getAs("e_time").toString().split("___");
                String[] sr_servicename = row.getAs("sr_servicename").toString().split("___");

                // 保存所有的a_b == 0 or 1
                Map<String, String> k_v = new HashMap<>();
                // 总的开始遍历的次数了 =============================================
                // 1 个trace 里面有多个caller
                for (int i = 0; i < caller_service.length; i++) {
                    // 拿到一个caller 经过的所有服务
                    String[] temp_sr_service = sr_servicename[i].split(",");
                    String[] temp_e_time = e_time[i].split(",");
                    Map<String, String> service_etime_map = new HashMap<>();
                    System.out.println(temp_sr_service.length + "009999------9--" + temp_e_time.length);
                    // 得到一个list 的两两服务的组合， a_b, a_c, a_d
                    if (temp_sr_service.length == temp_e_time.length) {
                        for (int j = 0; j < temp_sr_service.length; j++) {
                            temp_sr_service[j] = temp_sr_service[j]
                                    .replaceAll("ts-", "")
                                    .replaceAll("-", "_");
                            // 对于一个caller 来将， key 值唯一
                            service_etime_map.put(temp_sr_service[j], temp_e_time[j]);
                        }
                        List<String> map_pair_service = Copy_2_of_Service.callNoDoublePairService(temp_sr_service);

                        // 前面按照开始时间排序了，现在只需要按照结束时间判断
                        for (String pairSer : map_pair_service) {
                            // 应该是每一个都会执行的
                            String[] pairs = pairSer.split("__");
                            String time1 = service_etime_map.get(pairs[0]);
                            String time2 = service_etime_map.get(pairs[1]);
                            k_v.put(pairSer, TimeUtil.compareTime(time1, time2) + "");
                        }
                    }
                }


                List<String> callerPairServiceAll = Copy_2_of_Service.callPairService(Copy_2_of_Service.callerServicePart2);
                // 加 service  pair
                for (int i = 0; i < callerPairServiceAll.size(); i++) {
                    // k_v  里面可能没有 所有的，就为0 或 null
                    String cloumnValue = k_v.get(callerPairServiceAll.get(i));
                    if (cloumnValue == null || "".equals(cloumnValue))
                        rowDataList.add("-1"); // 没调的写个0
                    else
                        rowDataList.add(cloumnValue);
                }

                // 加caller service
                // caller
                String[] callerServiceAll = Copy_2_of_Service.callerServicePart2;
                // 每次都重新初始化
                Map<String, String> columNameAndValueMap = new HashMap<>();
                // 初始化map, 把所有的列都放进去
                for (int i = 0; i < callerServiceAll.length; i++) {
                    columNameAndValueMap.put(callerServiceAll[i], "");
                }

                for (int i = 0; i < caller_service.length; i++) {
                    String tempService = caller_service[i].replaceAll("ts-", "")
                            .replaceAll("-", "_");
                    // admin_route
                    if (columNameAndValueMap.containsKey(tempService)) {
                        columNameAndValueMap.put(tempService, "1"); // 调的写个1
                    }
                }

                for (int i = 0; i < callerServiceAll.length; i++) {
                    String cloumnValue = columNameAndValueMap.get(callerServiceAll[i]);
                    if (cloumnValue == null || "".equals(cloumnValue))
                        rowDataList.add("0"); // 没调的写个0
                    else
                        rowDataList.add(cloumnValue);
                }
                System.out.println(rowDataList.size() + "==============---------------size1");
                // 遍历 k_v 到list 里面即可
                return RowFactory.create(rowDataList.toArray());
            }
        });


        // 根据列名, 构建含有traceid 的seq 和 caller
        List<String> seqCallerColumsAll = Copy_2_of_Service.execute();
        // 表头
        List<StructField> structFields = new ArrayList<>();
        for (int i = 0; i < seqCallerColumsAll.size(); i++) {
            structFields.add(DataTypes.createStructField(seqCallerColumsAll.get(i), DataTypes.StringType, true));
        }
        StructType structType = DataTypes.createStructType(structFields);
        Dataset<Row> callerSerDataSet = spark.createDataFrame(step3Rdd, structType);
        //  callerSerDataSet.printSchema();
        // callerSerDataSet.select("trace_id").show();
        //  System.out.println(callerSerDataSet.count() + "===================-----000");

        callerSerDataSet.write().saveAsTable("seq_finall");
        // callerSerDataSet.createOrReplaceTempView("view_caller_service");
        System.out.println(seqCallerColumsAll.size() + "==============---------------size2"); // 1765
        System.out.println("==========over===========");
        //  return callerSerDataSet;
    }

}