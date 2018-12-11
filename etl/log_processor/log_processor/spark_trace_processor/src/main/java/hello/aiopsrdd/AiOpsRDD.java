package hello.aiopsrdd;

import hello.domain.*;
import hello.util.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.*;


public class AiOpsRDD {
    public static void executor() {

        System.out.println("==============spark sql ====");
        SparkSession spark = SparkSession
                .builder()
                .config("spark.debug.maxToStringFields", 5000)
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.sql.crossJoin.enabled", true)
                .appName("java spark sql")
                .master("yarn")
                .getOrCreate();

        filter(spark);
        // combineYtoTrace(spark);
        // cpuMemory(spark);
        // finalTraceCombineSequence(spark);
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

                    // 过滤
                    traceAnnotation.setBnno_httpurl(MatcherUrlRouterUtil.matcherPattern(parts[19]));

                    traceAnnotation.setBnno_http_method(parts[20]);
                    traceAnnotation.setBnno_downstream_cluster(parts[21]);

                    traceAnnotation.setTest_trace_id(parts[22]);
                    traceAnnotation.setTest_case_id(parts[23]);

                    traceAnnotation.setBnno_http_protocol(parts[24]);
                    traceAnnotation.setBnno_request_size(parts[25]);
                    traceAnnotation.setBnno_upstream_cluster(parts[26]);


                    traceAnnotation.setBnno_status_code(StatusCodeUtil.genInstanceStatus(parts[27]));

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
                    // trace  duration
                    span_duration = Long.valueOf(traceAnnotation.getAnno_a2_timestamp()) - Long.valueOf(traceAnnotation.getAnno_a1_timestamp());
                    //    String result = new BigDecimal((double) span_duration / 1000000 + "").toString();
                    traceAnnotation.setSpan_duration(span_duration + "");
//                    double time_ql = Double.parseDouble(result);
//                    if (time_ql < 1)
//                        traceAnnotation.setSpan_duration(1 + "");
//                    else if (time_ql >= 1 && time_ql < 3)
//                        traceAnnotation.setSpan_duration(2 + "");
//                    else if (time_ql >= 3 && time_ql < 6)
//                        traceAnnotation.setSpan_duration(3 + "");
//                    else if (time_ql >= 6 && time_ql < 10)
//                        traceAnnotation.setSpan_duration(4 + "");
//                    else if (time_ql >= 10 && time_ql < 20)
//                        traceAnnotation.setSpan_duration(5 + "");
//                    else if (time_ql >= 20)
//                        traceAnnotation.setSpan_duration(6 + "");
                }
                return traceAnnotation;
            }
        });

        // step 2 : create a temp original span table
        Dataset<Row> originalSpanDateset = spark.createDataFrame(changeSpanTimeRdd, TraceAnnotation.class);
        originalSpanDateset.createOrReplaceTempView("original_span_table");
        System.out.println("---------------  original_span_table created ---------------");


        // step 3 : create real span   dataset  and  reduce same span id
        //  cs  cr  ss  sr
        Dataset<Row> realSpanDataset = spark.sql(TempSQL.genRealSpan);
        String[] duplicasKey = new String[]{"span_id"};
        realSpanDataset = realSpanDataset.dropDuplicates(duplicasKey);
        realSpanDataset.createOrReplaceTempView("real_span_trace_view");
        System.out.println("---------------  real_span_trace table created ---------------");

        // from  real_span_trace_view  ==>  real_invocation_view
        genRealInvocation(spark);


        // from real_span_trace_view  to  real_trace_pass_view
        // from real_invocation_view  and real_trace_pass_view ==>  trace_passservice_view
        tracePassService(spark);
        combinePassServiceToTrace(spark);


        // from csv  to real_cpu_memory_view
        // from trace_passservice_view , real_cpu_memory_view ==> trace_pass_cpu_view
        cpuMemory(spark);
        combineCpuMemoryToTrace(spark); // trace_pass_cpu_view


        // from   mysql  ---- >  test_traces_mysql_view
        genTestTraceMysql(spark);


        //from  test_traces_mysql_view  , trace_pass_cpu_view  ---- >  trace_y_view
        combineYtoTrace(spark);

        // from real_span_trace_view  ----->  final_seq_view
        //genSequencePart(spark);


        // form final_seq_view  trace_y_view  --->  to trace_final
        // finalTraceCombineSequence(spark);
    }

    private static void finalTraceCombineSequence(SparkSession spark) {
        Dataset<Row> trace_finalDateSet = spark.sql(TempSQL.trace_finalDateSet);
        // `trace_final`: `trace_id`, `test_case_id`, `test_trace_id`
//        String[] duplicasKey = new String[]{"trace_id", "test_case_id", "test_trace_id"};
//        trace_finalDateSet = trace_finalDateSet.dropDuplicates(duplicasKey);
        //  System.out.println(trace_finalDateSet.count() + "-----------===============");
        //    trace_finalDateSet.repartition(12);
        trace_finalDateSet.write().saveAsTable("trace_final");
        System.out.println("======all over======");
    }

    private static void genTestTraceMysql(SparkSession spark) {
        Dataset<Row> testTraceDataSet = DBUtil.connectDBUtil(spark, "ai_ops_liu", "test_trace");
        //testTraceDataSet.printSchema();
        System.out.println("testtrace   shema  printled");
        testTraceDataSet.createOrReplaceTempView("test_traces_mysql_view");
        // testTraceDataSet.write().saveAsTable("traces_mysql_view");
    }

    private static void combineYtoTrace(SparkSession spark) {
        Dataset<Row> finallTrace = spark.sql(TempSQL.combineYtoTrace);
        // finallTrace.printSchema();
        //finallTrace.repartition(12);
        finallTrace.write().saveAsTable("new_trace_y2");
        //finallTrace.createOrReplaceTempView("trace_y_view");
        // System.out.println(finallTrace.count() + "-------------count--------------");
        System.out.println("--------------- final_trace2 table created ---------------");
    }


    private static void genRealInvocation(SparkSession spark) {
        Dataset<Row> invocationDataset = spark.sql(TempSQL.genInvocation);
        //invocationDataset.show();
        //invocationDataset.printSchema();
        invocationDataset.createOrReplaceTempView("real_invocation_view");
        //invocationDataset.write().saveAsTable("real_invocation3");
        System.out.println("---------------  real_invocation table created ---------------");
    }

    private static void tracePassService(SparkSession spark) {
        //  read trace passby service, gen new trace_pass_service   by  real_span_trace
        Dataset<Row> tracePassServiceDataset = spark.sql(TempSQL.getTracePassService);
        // 根据列名, 构建含有traceid 的seq 和 caller
        String[] tracePassServiceCloumn = CloumnNameUtil.tracePassServiceCloumn;

        JavaRDD<Row> step3Rdd = tracePassServiceDataset.javaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                // 最终返回的一行数据
                List<String> rowDataList = new ArrayList<>();

                String trace_id = row.getAs("trace_id");
                rowDataList.add(trace_id);

                // pass service num
                // 1/2/3/4
                // 1/2/3-6/>6
                String trace_service_span = row.getAs("trace_service_span");
                rowDataList.add(trace_service_span);
//                int span_num = Integer.parseInt(trace_service_span);
//                if (span_num == 1)
//                    rowDataList.add("1");
//                else if (span_num == 2) {
//                    rowDataList.add("2");
//                } else if (span_num >= 3 && span_num <= 6) {
//                    rowDataList.add("3");
//                } else {
//                    rowDataList.add("4");
//                }

                String[] cr_pass_service = row.getAs("cr_service_included").toString().split(",");
                String[] sr_pass_service = row.getAs("sr_service_included").toString().split(",");

                String[] cr_pass_api = row.getAs("c_req_api").toString().split(",");
                String[] sr_pass_api = row.getAs("s_req_api").toString().split(",");

                String[] cr_inst_id = row.getAs("c_inst_id").toString().split(",");
                String[] sr_inst_id = row.getAs("s_inst_id").toString().split(",");

                String[] cr_status_code = row.getAs("c_status_code").toString().split(",");
                String[] s_status_code = row.getAs("s_status_code").toString().split(",");

                String[] crs_time = row.getAs("crs_time").toString().split(",");
                String[] ssr_time = row.getAs("ssr_time").toString().split(",");

                Map<String, String> passServiceMap = new HashMap<>();
                Map<String, String> passServiceApiMap = new HashMap<>();
                Map<String, String> passServiceInstId = new HashMap<>();
                Map<String, String> passServiceStatusCode = new HashMap<>();

                Map<String, String> passServiceTimeMap = new HashMap<>();


                // 放前面
                for (int i = 0; i < sr_pass_service.length; i++) {
                    if (!passServiceMap.containsKey(sr_pass_service[i])) {
                        passServiceMap.put(sr_pass_service[i], sr_pass_service[i]);
                        passServiceApiMap.put(sr_pass_service[i], sr_pass_api[i]);
                        passServiceInstId.put(sr_pass_service[i], sr_inst_id[i]);
                        passServiceStatusCode.put(sr_pass_service[i], s_status_code[i]);

                        passServiceTimeMap.put(sr_pass_service[i], ssr_time[i]);
                    }
                }
                // 前面一行几乎把所有的api都有了
                for (int i = 0; i < cr_pass_service.length; i++) {
                    if (!passServiceMap.containsKey(cr_pass_service[i])) {
                        passServiceMap.put(cr_pass_service[i], cr_pass_service[i]);
                        passServiceApiMap.put(cr_pass_service[i], cr_pass_api[i]);
                        passServiceInstId.put(cr_pass_service[i], cr_inst_id[i]);
                        passServiceStatusCode.put(cr_pass_service[i], cr_status_code[i]);
                        passServiceTimeMap.put(cr_pass_service[i], crs_time[i]);
                    }
                }


                for (int i = 0; i < tracePassServiceCloumn.length; i++) {
                    // passOrNot 为服务名
                    // 经过的服务， 调用的api, 执行的时间，
                    String changeService = tracePassServiceCloumn[i]
                            .replaceAll("_included", "").replaceAll("_", "-");
                    String passOrNot = passServiceMap.get(changeService);
                    if (passOrNot == null || "".equals(passOrNot)) {
                        rowDataList.add("-1"); // s?_include
                        rowDataList.add(""); // s?_service_api
                        rowDataList.add(""); // s?_inst_id
                        rowDataList.add(""); // s?_inst_status_code
                        rowDataList.add(""); // s?_exec_time
                        rowDataList.add(""); //s?_var
                    } else {
                        rowDataList.add("1");
                        rowDataList.add(passServiceApiMap.get(changeService));
                        rowDataList.add("");
                        double serviceExecTime = Double.parseDouble(passServiceTimeMap.get(changeService));
//                        String resultTime = new BigDecimal(serviceExecTime / 1000000 + "").toString();
//                        double rt = Double.parseDouble(resultTime);
                        rowDataList.add(serviceExecTime + "");
                        // 0.007992
//                        if (rt <= 0.3) {
//                            rowDataList.add("1");
//                        } else if (rt < 1) {
//                            rowDataList.add("2");
//                        } else if (rt < 3) {
//                            rowDataList.add("4");
//                        } else if (rt > 3) {
//                            rowDataList.add("5");
//                        }
                    }
                }
                return RowFactory.create(rowDataList.toArray());
            }
        });

        List<String> tracePassServiceCloumnAll = CloumnNameUtil.getTracePassCloumnAllList();
        // 表头
        List<StructField> structFields = new ArrayList<>();
        for (int i = 0; i < tracePassServiceCloumnAll.size(); i++) {
            structFields.add(DataTypes.createStructField(tracePassServiceCloumnAll.get(i), DataTypes.StringType, true));
        }
        StructType structType = DataTypes.createStructType(structFields);
        Dataset<Row> tracePassDataset = spark.createDataFrame(step3Rdd, structType);

        ///tracePassDataset.write().saveAsTable("real_trace_pass_view");
        tracePassDataset.createOrReplaceTempView("real_trace_pass_view");
    }

    // service_config_data, service_instance_data ===> real_cpu_memory_view
    private static void cpuMemory(SparkSession spark) {
        // read service config data
        Dataset<Row> serviceConfigData = spark.read().option("header", "true")
                .csv("hdfs://10.141.211.173:8020/user/admin/serviceConfigData.csv");
        //serviceConfigData.printSchema();
        System.out.println("--------------print servcie config schema --------------");
        serviceConfigData.createOrReplaceTempView("service_config_data");

        // read service instance data
        Dataset<Row> serviceInstanceData = spark.read().option("header", "true")
                .csv("hdfs://10.141.211.173:8020/user/admin/serviceInstanceData.csv");
        //serviceInstanceData.printSchema();
        System.out.println("--------------print servcie instance schema --------------");
        serviceInstanceData.createOrReplaceTempView("service_instance_data");

        Dataset<Row> combineCpuMemory = spark.sql(TempSQL.genCpuMemory);

        String[] col = combineCpuMemory.columns();
        System.out.println(col.length + "-----w-------e-----------");
        Map<Integer, List<String>> cpuMemdiffService = CloumnNameUtil.getCpuMemDiff();

        JavaRDD<Row> configInstanceDataRDD = combineCpuMemory.javaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                List<String> colData = new ArrayList<>();

                for (int i = 0; i < col.length; i++) {
                    if (col[i].contains("mongo")) {
                    } else {
                        colData.add(row.getAs(i));
                    }
                }
                System.out.println("-----------------------" + cpuMemdiffService.size() + "----------------------");
                for (int i = 0; i < cpuMemdiffService.size(); i++) {
//                    System.out.println(cpuMemdiffService.get(i).get(0) + "---"+ cpuMemdiffService.get(i).get(1));
//                    System.out.println(cpuMemdiffService.get(i).get(2) + "---"+ cpuMemdiffService.get(i).get(3));
//
//                    System.out.println(row.getAs(cpuMemdiffService.get(i).get(0)) + "");
//                    System.out.println(row.getAs(cpuMemdiffService.get(i).get(1)) + "");
//                    System.out.println(row.getAs(cpuMemdiffService.get(i).get(2)) + "");
//                    System.out.println(row.getAs(cpuMemdiffService.get(i).get(3)) + "");

                    double l_mem = 0; // _l_memory
                    if (row.getAs(cpuMemdiffService.get(i).get(0)) == null) {
                        l_mem = 0;
                    } else {
                        l_mem = Double.parseDouble(row.getAs(cpuMemdiffService.get(i).get(0)));
                    }

                    double i_mem = 0;  // _inst_memory
                    if (row.getAs(cpuMemdiffService.get(i).get(1)) == null) {
                        i_mem = 0;
                    } else {
                        i_mem = Double.parseDouble(row.getAs(cpuMemdiffService.get(i).get(1)));
                    }

                    double l_cpu = 0; // _l_cpu
                    if (row.getAs(cpuMemdiffService.get(i).get(2)) == null) {
                        l_cpu = 0;
                    } else {
                        l_cpu = Double.parseDouble(row.getAs(cpuMemdiffService.get(i).get(2)));
                    }

                    double i_cpu = 0; // _inst_cpu
                    if (row.getAs(cpuMemdiffService.get(i).get(3)) == null) {
                        i_cpu = 0;
                    } else {
                        i_cpu = Double.parseDouble(row.getAs(cpuMemdiffService.get(i).get(3)));
                    }

                    colData.add((i_mem - l_mem) + "");
                    colData.add((i_cpu - l_cpu) + "");
                }
                return RowFactory.create(colData.toArray());
            }
        });

        // 过滤掉mongo的类名
        List<String> colName = new ArrayList<>();
        for (int i = 0; i < col.length; i++) {
            if (!col[i].contains("mongo"))
                colName.add(col[i]);
        }
        System.out.println("==================" + colName.size() + " ------------" + configInstanceDataRDD.collect().size());
        List<String> diffCpuMemName = CloumnNameUtil.addCpuMemDiffCloumn();
        for (int i = 0; i < diffCpuMemName.size(); i++) {
            colName.add(diffCpuMemName.get(i));
        }
        // 表头
        List<StructField> structFields = new ArrayList<>();
        for (int i = 0; i < colName.size(); i++) {
            structFields.add(DataTypes.createStructField(colName.get(i), DataTypes.StringType, true));
        }
        StructType structType = DataTypes.createStructType(structFields);
        // 填充数据
        Dataset<Row> configInstanceDataSet = spark.createDataFrame(configInstanceDataRDD, structType);


        // configInstanceDataSet.write().saveAsTable("cpu_memory2");
        System.out.println("===============all over ===================");
        configInstanceDataSet.createOrReplaceTempView("real_cpu_memory_view");
    }


    // real_invocation_view   real_trace_pass_view ,  before_trace_view
    public static void combinePassServiceToTrace(SparkSession spark) {
        Dataset<Row> beforeTraceDataset = spark.sql(TempSQL.combinePassServiceToTrace);
        //beforeTraceDataset.show();
        //beforeTraceDataset.printSchema();
        beforeTraceDataset.createOrReplaceTempView("trace_passservice_view");
        //beforeTraceDataset.write().saveAsTable("before_trace");
    }

    private static void combineCpuMemoryToTrace(SparkSession spark) {
        Dataset<Row> realTraceDataset = spark.sql(TempSQL.genRealTrace);
        // realTraceDataset.printSchema();
        // realTraceDataset.show();
        String[] duplicasKey = new String[]{"trace_id"};
        realTraceDataset = realTraceDataset.dropDuplicates(duplicasKey);
        realTraceDataset.createOrReplaceTempView("trace_pass_cpu_view");
        // System.out.println(realTraceDataset.count() + "========================");
        //    realTraceDataset.write().saveAsTable("trace_pass_cpu_view");
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
                    // System.out.println(temp_sr_service.length + "009999------9--" + temp_e_time.length);
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
                // System.out.println(rowDataList.size() + "==============---------------size1");
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

        callerSerDataSet.write().saveAsTable("final_seq");
        //  callerSerDataSet.createOrReplaceTempView("final_seq_view");
        //System.out.println(seqCallerColumsAll.size() + "==============---------------size2"); // 1765
        System.out.println("==========over===========");
        //  return callerSerDataSet;
    }
}