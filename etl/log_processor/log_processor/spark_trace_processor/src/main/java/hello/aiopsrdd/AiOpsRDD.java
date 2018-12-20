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

//    private static String TRACE_CSV_URL  = "hdfs://10.141.211.173:8020/user/admin/new_span_trace_sequence.csv";
//    private static String SERVICE_INSTANC_CSV_URL = "hdfs://10.141.211.173:8020/user/admin/serviceInstanceData_sequence.csv";
//    private static String SERVIC_CONFIG_CSV_URL = "hdfs://10.141.211.173:8020/user/admin/serviceConfigData_sequence.csv";
//    private static String DATABASE_NAME = "ai_ops";

    private static String TRACE_CSV_URL = "hdfs://10.141.211.173:8020/user/admin/new_span_trace_instance.csv";
    private static String SERVICE_INSTANC_CSV_URL = "hdfs://10.141.211.173:8020/user/admin/serviceInstanceData_instance.csv";
    private static String SERVIC_CONFIG_CSV_URL = "hdfs://10.141.211.173:8020/user/admin/serviceConfigData_instance.csv";
    private static String DATABASE_NAME = "ai_ops_liu_instance2";

    public static void executor() {

        System.out.println("==============spark sql ====");
        SparkSession spark = SparkSession
                .builder()
                .config("spark.debug.maxToStringFields", 3000)
                .config("spark.driver.maxResultSize", "4g")
                .config("spark.sql.crossJoin.enabled", true)
                .config("spark.default.parallelism", 8)
                .appName("java spark sql")
                .master("yarn")
                .getOrCreate();

        /**
         * trace_combine_instance42_3
         */
        filter(spark);

        /**
         *  第二部
         *   trace_combine_config_3
         */
//        serviceConfig(spark);
//        combineServiceConfigToTrace(spark);


        /**
         * 第三步
         */
//        genTestTraceMysql(spark);
//        combineYtoTrace(spark);

        /**
         * 第四步
         */

        //   genSequencePart(spark);

        spark.close();
        System.out.println("==============spark sql closed====");
    }


    // real_trace_pass_view   real_cpu_memory_view
    private static void filter(SparkSession spark) {

        // step 1 : read original span logs as RDD
        JavaRDD<TraceAnnotation> originalSpanRdd = spark.read()
                .textFile(TRACE_CSV_URL)
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

        // step 2 : create a temp original span table
        Dataset<Row> originalSpanDateset = spark.createDataFrame(changeSpanTimeRdd, TraceAnnotation.class);
        originalSpanDateset.createOrReplaceTempView("original_span_table");
        System.out.println("---------------  original_span_table created ---------------");


        // step 3 : create real span   dataset  and  reduce same span id
        //  cs  cr  ss  sr
        Dataset<Row> realSpanDataset = spark.sql(TempSQL.genRealSpan);
        realSpanDataset = realSpanDataset.dropDuplicates(new String[]{"span_id"});
        realSpanDataset.createOrReplaceTempView("real_span_trace_view");
        //  realSpanDataset.write().saveAsTable("real_span_trace");
        System.out.println("---------------  real_span_trace table created ---------------");

        // from  real_span_trace_view  ==>  real_invocation_view
//        genRealInvocation(spark);
//
//        // from real_span_trace_view  to  real_trace_pass_view
//        // from real_invocation_view  and real_trace_pass_view ==>  trace_passservice_view
//        tracePassService(spark);
//        combinePassServiceToTrace(spark);
//
//        combineInstanceDataToTrace(spark);

//        serviceConfig(spark);
//        combineServiceConfigToTrace(spark);

        // from   mysql  ---- >  test_traces_mysql_view
//       genTestTraceMysql(spark);
//       combineYtoTrace(spark);

        //from  test_traces_mysql_view  , trace_pass_cpu_view  ---- >  trace_y_view


        // from real_span_trace_view  ----->  final_seq_view
        genSequencePart(spark);


        // form final_seq_view  trace_y_view  --->  to trace_final
        // finalTraceCombineSequence(spark);
    }

    /**
     * 产生真的span
     *
     * @param spark
     */
    private static void genRealInvocation(SparkSession spark) {
        Dataset<Row> invocationDataset = spark.sql(TempSQL.genInvocation);
        //invocationDataset.show();
        //invocationDataset.printSchema();
        invocationDataset.createOrReplaceTempView("real_invocation_view");
        // invocationDataset.write().saveAsTable("real_invocation_sequence");
        System.out.println("---------------  real_invocation table created ---------------");
    }

    /**
     * 读出csv 文件， 并选择42列，合并到trce
     *
     * @param spark
     */
    private static void combineInstanceDataToTrace(SparkSession spark) {
        // read service instance data
        Dataset<Row> serviceInstanceData = spark.read().option("header", "true")
                .csv(SERVICE_INSTANC_CSV_URL);
        //serviceInstanceData.printSchema();
        System.out.println("--------------print servcie instance schema --------------");
        serviceInstanceData.createOrReplaceTempView("service_instance_data");

        // from  trace_passservice_view
        Map<String, String> configSQLMap = CloumnNameUtil.configEachServeSQL();
        String[] basic_service = CloumnNameUtil.tracePassServiceCloumn;
        Dataset<Row> traceCombineInstance = null;
        for (int i = 0; i < basic_service.length; i++) {
            traceCombineInstance = spark.sql(configSQLMap.get(basic_service[i].replaceAll("_included", "")));
            if (i % 4 == 0 || i % 6 == 0) {
                traceCombineInstance = traceCombineInstance.dropDuplicates(new String[]{"trace_id"});
            }
            traceCombineInstance.createOrReplaceTempView("trace_passservice_view");
        }
        traceCombineInstance = traceCombineInstance.dropDuplicates(new String[]{"trace_id"});
        // traceCombineInstance.createOrReplaceTempView("trace_combine_instance_view");
        traceCombineInstance.write().saveAsTable("trace_combine_instance42");
        System.out.println("=================   trace_combine_instance created   ==============");
    }


    /**
     * 从csv读出文件，并过滤掉mongo， 把service_config 合并到trace,
     * 这步就把instance 和 config 都 合并到 trace了
     *
     * @param spark
     */
    private static void serviceConfig(SparkSession spark) {
        Dataset<Row> serviceConfigData = spark.read().option("header", "true")
                .csv(SERVIC_CONFIG_CSV_URL);
        serviceConfigData.createOrReplaceTempView("config_server_view");
        Dataset<Row> serviceConfigDataset = spark.sql(TempSQL.config_server_view);

        JavaRDD<Row> lineConfigServerRDD = serviceConfigDataset.toJavaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                List<String> configDataList = new ArrayList<>();

                configDataList.add(row.getAs("start_time"));
                configDataList.add(row.getAs("end_time"));

                String[] serviceName = row.getAs("serviceName").toString().split(",");
                String[] l_cpu = row.getAs("l_cpu").toString().split(",");
                String[] l_memory = row.getAs("l_memory").toString().split(",");
                String[] confNumber = row.getAs("confNumber").toString().split(",");
                String[] readyNumber = row.getAs("readyNumber").toString().split(",");
                Map<String, String> serviceNameMap = new HashMap<>();
                Map<String, String> l_cpu_map = new HashMap<>();
                Map<String, String> l_memoryMap = new HashMap<>();
                Map<String, String> confNumberMap = new HashMap<>();
                Map<String, String> readyNumberMap = new HashMap<>();
                for (int i = 0; i < serviceName.length; i++) {
                    serviceNameMap.put(serviceName[i], serviceName[i]);
                    l_cpu_map.put(serviceName[i], l_cpu[i]);
                    l_memoryMap.put(serviceName[i], l_memory[i]);
                    confNumberMap.put(serviceName[i], confNumber[i]);
                    readyNumberMap.put(serviceName[i], readyNumber[i]);
                }
                String[] allServiceName = CloumnNameUtil.tracePassServiceCloumn;
                for (int i = 0; i < allServiceName.length; i++) {
                    String currentName = serviceNameMap.get(allServiceName[i].replaceAll("_included", "").replaceAll("_", "-"));
                    if (currentName != null || currentName != "") {
                        configDataList.add(serviceNameMap.get(currentName));
                        configDataList.add(l_cpu_map.get(currentName));
                        configDataList.add(l_memoryMap.get(currentName));
                        configDataList.add(confNumberMap.get(currentName));
                        configDataList.add(readyNumberMap.get(currentName));
                    } else {
                        configDataList.add("");
                        configDataList.add("");
                        configDataList.add("");
                        configDataList.add("");
                        configDataList.add("");
                    }
                }
                return RowFactory.create(configDataList.toArray());
            }
        });

        List<String> configCloumn = CloumnNameUtil.configServiceCloumn();

        // 表头
        List<StructField> structFields2 = new ArrayList<>();
        for (int i = 0; i < configCloumn.size(); i++) {
            structFields2.add(DataTypes.createStructField(configCloumn.get(i), DataTypes.StringType, true));
        }
        StructType structType = DataTypes.createStructType(structFields2);
        // 填充数据
        Dataset<Row> configServerDataSet = spark.createDataFrame(lineConfigServerRDD, structType);
        configServerDataSet.createOrReplaceTempView("service_config_data");
        //configServerDataSet.write().saveAsTable("server_config_table11");
    }

    private static void combineServiceConfigToTrace(SparkSession spark) {
//        Dataset<Row> serviceConfigData = spark.read().option("header", "true")
//                .csv(SERVIC_CONFIG_CSV_URL);
//        System.out.println("--------------print servcie config schema --------------");
//
//        // 去除mongo
//        String[] configColName = serviceConfigData.columns();
//        JavaRDD<Row> configNoMongoRDD = serviceConfigData.javaRDD().map(new Function<Row, Row>() {
//            @Override
//            public Row call(Row row) throws Exception {
//                List<String> configData = new ArrayList<>();
//                for (int i = 0; i < configColName.length; i++) {
//                    if (configColName[i].contains("mongo")) {
//                        // 对应值不加进去
//                    } else {
//                        configData.add(row.getAs(i));
//                    }
//                }
//                return RowFactory.create(configData.toArray());
//            }
//        });
//        // 过滤掉mongo的类名
//        List<String> newColName = new ArrayList<>();
//        for (int i = 0; i < configColName.length; i++) {
//            if (!configColName[i].contains("mongo"))
//                newColName.add(configColName[i]);
//        }
//
//        List<StructField> structFields = new ArrayList<>();
//        for (int i = 0; i < newColName.size(); i++) {
//            structFields.add(DataTypes.createStructField(newColName.get(i), DataTypes.StringType, true));
//        }
//        Dataset<Row> serviceConfigDataSet = spark.createDataFrame(configNoMongoRDD, DataTypes.createStructType(structFields));
//        serviceConfigDataSet.createOrReplaceTempView("service_config_data");


        Dataset<Row> trace_combine_configDataset = spark.sql(TempSQL.combineServiceConfigToTrace);
        // 根据时间匹配的，一定要去重
        trace_combine_configDataset = trace_combine_configDataset.dropDuplicates(new String[]{"trace_id"});
        //trace_combine_configDataset.write().saveAsTable("ttttttttttttttttttt");

        /**
         * 计算memory , cpu diff
         * i_mem  - l_mem,  i_cpu -  l_cpu
         */
        String[] traceColName = trace_combine_configDataset.columns();
        Map<Integer, List<String>> cpuMemdiffServerName = CloumnNameUtil.getCpuMemDiffCloName();
        JavaRDD<Row> traceRDD = trace_combine_configDataset.javaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                List<String> traceDataList = new ArrayList<>();
                for (int i = 0; i < traceColName.length; i++) {
                    traceDataList.add(row.getAs(i));
                }

                for (int i = 0; i < cpuMemdiffServerName.size(); i++) {

                    double i_mem = 0;
                    if (row.getAs(cpuMemdiffServerName.get(i).get(1)) == null) {
                        i_mem = 0;
                        // 说明这个不是经过的服务
                        traceDataList.add("");
                        traceDataList.add("");
                        continue;
                    } else {
                        i_mem = Double.parseDouble(row.getAs(cpuMemdiffServerName.get(i).get(1)));
                    }

                    double i_cpu = 0;
                    if (row.getAs(cpuMemdiffServerName.get(i).get(3)) == null) {
                        i_cpu = 0;
                        // 说明这个不是经过的服务
                        traceDataList.add("");
                        traceDataList.add("");
                        continue;
                    } else {
                        i_cpu = Double.parseDouble(row.getAs(cpuMemdiffServerName.get(i).get(3)));
                    }

                    double l_mem = 0;
                    if (row.getAs(cpuMemdiffServerName.get(i).get(0)) == null) {
                        l_mem = 0;
                    } else {
                        l_mem = Double.parseDouble(row.getAs(cpuMemdiffServerName.get(i).get(0)));
                    }


                    double l_cpu = 0;
                    if (row.getAs(cpuMemdiffServerName.get(i).get(2)) == null) {
                        l_cpu = 0;
                    } else {
                        l_cpu = Double.parseDouble(row.getAs(cpuMemdiffServerName.get(i).get(2)));
                    }


                    if (i_mem != 0 && i_cpu != 0 || l_cpu != 0 || l_mem != 0) {
                        traceDataList.add((i_mem - l_mem) + "");
                        traceDataList.add((i_cpu - l_cpu) + "");
                    }
                }
                return RowFactory.create(traceDataList.toArray());
            }
        });

        /**
         * 构造表头,在trace 后面加 42 inst_diff_cpu, 42 inst_diff_mem
         */
        List<String> colName = new ArrayList<>();
        for (int i = 0; i < traceColName.length; i++) {
            colName.add(traceColName[i]);
        }

        List<String> diffCpuMemName = CloumnNameUtil.addCpuMemDiffCloumn();
        for (int i = 0; i < diffCpuMemName.size(); i++) {
            colName.add(diffCpuMemName.get(i));
        }
        // 表头
        List<StructField> structFields2 = new ArrayList<>();
        for (int i = 0; i < colName.size(); i++) {
            structFields2.add(DataTypes.createStructField(colName.get(i), DataTypes.StringType, true));
        }
        StructType structType = DataTypes.createStructType(structFields2);
        // 填充数据
        Dataset<Row> trace_combine_config_diff_DataSet = spark.createDataFrame(traceRDD, structType);
        trace_combine_config_diff_DataSet = trace_combine_config_diff_DataSet.dropDuplicates(new String[]{"trace_id"});
        //   trace_combine_config_diff_DataSet.createOrReplaceTempView("trace_combine_config_view");
        trace_combine_config_diff_DataSet.write().saveAsTable("trace_combine_config_3");
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
        Dataset<Row> testTraceDataSet = DBUtil.connectDBUtil(spark, DATABASE_NAME, "test_trace");
        //testTraceDataSet.printSchema();
        System.out.println("testtrace   shema  printled");
        testTraceDataSet.createOrReplaceTempView("test_traces_mysql_view");
        // testTraceDataSet.write().saveAsTable("traces_mysql_view");
    }

    private static void combineYtoTrace(SparkSession spark) {
        Dataset<Row> finallTrace = spark.sql(TempSQL.combineYtoTrace);
        // finallTrace.printSchema();
        //finallTrace.repartition(12);

        finallTrace.write().saveAsTable("trace_instance_3");
        //finallTrace.createOrReplaceTempView("trace_y_view");
        // System.out.println(finallTrace.count() + "-------------count--------------");
        System.out.println("--------------- trace_sequence_1 table created ---------------");
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
                rowDataList.add(row.getAs("trace_id"));

                // pass service num
                // 1/2/3/4
                // 1/2/3-6/>6
                rowDataList.add(row.getAs("trace_service_span"));

                String[] cr_pass_service = row.getAs("cr_service_included").toString().split(",");
                String[] sr_pass_service = row.getAs("sr_service_included").toString().split(",");

                String[] cr_pass_api = row.getAs("c_req_api").toString().split(",");
                String[] sr_pass_api = row.getAs("s_req_api").toString().split(",");

                String[] cr_inst_id = row.getAs("c_inst_id").toString().split(",");
                String[] sr_inst_id = row.getAs("s_inst_id").toString().split(",");

                String[] cr_status_code = row.getAs("c_status_code").toString().split(",");
                String[] sr_status_code = row.getAs("s_status_code").toString().split(",");

                String[] crs_time = row.getAs("crs_time").toString().split(",");
                String[] ssr_time = row.getAs("ssr_time").toString().split(",");

                Map<String, String> passServiceMap = new HashMap<>();
                Map<String, String> passServiceApiMap = new HashMap<>();
                Map<String, String> passServiceInstId = new HashMap<>();
                Map<String, String> passServiceStatusCode = new HashMap<>();
                Map<String, String> passServiceTimeMap = new HashMap<>();

                // 放前面, 经过的服务，大部分在sr里面
                for (int i = 0; i < sr_pass_service.length; i++) {
                    if (!passServiceMap.containsKey(sr_pass_service[i])) {
                        passServiceMap.put(sr_pass_service[i], sr_pass_service[i]);
                        passServiceApiMap.put(sr_pass_service[i], sr_pass_api[i]);
                        passServiceInstId.put(sr_pass_service[i], sr_inst_id[i]);
                        passServiceStatusCode.put(sr_pass_service[i], sr_status_code[i]);
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


                // 42 个服务
                for (int i = 0; i < tracePassServiceCloumn.length; i++) {
                    // passOrNot 为服务名
                    // 经过的服务， 调用的api, 执行的时间，
                    String changeServiceName = tracePassServiceCloumn[i]
                            .replaceAll("_included", "").replaceAll("_", "-");
                    String passOrNot = passServiceMap.get(changeServiceName);
                    // 没有经过的服务
                    if (passOrNot == null || "".equals(passOrNot)) {
                        rowDataList.add("-1"); // s?_include
                        rowDataList.add(""); // s?_service_api
                        rowDataList.add(""); // s?_inst_id
                        rowDataList.add(""); // s?_inst_status_code
                        rowDataList.add(""); // s?_exec_time

                        // 没有经过的服务，但是有变量， 加对应数量的空值
                        Integer varNum = SharedVariableUtils.getServiceVarlible().get(changeServiceName);
                        if (varNum != null) {
                            for (int l = 0; l < varNum; l++) {
                                rowDataList.add(""); //s?_var
                            }
                        }
                    } else {
                        rowDataList.add("1"); // s?_include
                        rowDataList.add(passServiceApiMap.get(changeServiceName)); // s?_service_api
                        rowDataList.add(passServiceInstId.get(changeServiceName)); // s?_inst_id
                        rowDataList.add(passServiceStatusCode.get(changeServiceName));// s?_inst_status_code
                        rowDataList.add(Double.parseDouble(passServiceTimeMap.get(changeServiceName)) + ""); // s?_exec_time


                        Integer varNum = SharedVariableUtils.getServiceVarlible().get(changeServiceName);
                        if (varNum != null) {

                            // 经过1个服务1个api, 1个api里面可能有多个变量
                            Map<String, String> passApiVariable = SharedVariableUtils.getSharedVariableMap()
                                    .get(changeServiceName).get(passServiceApiMap.get(changeServiceName));
                            if (passApiVariable != null) {
                                // 因为一个路径下面，目前只有一个比变量，所以list 大小肯定为1
                                List<String> passVarName = SharedVariableUtils.getPassVarName()
                                        .get(changeServiceName).get(passServiceApiMap.get(changeServiceName));
//                                if (passVarName != null) {
//                                    for (int j = 0; j < 1; i++) {
//                                        // 先添加经过的api 对应的变量
//                                        rowDataList.add(passApiVariable.get(passVarName.get(0))); //s?_var
//                                    }
//                                    if ((varNum - 1) > 0) {
//                                        for (int l = 0; l < (varNum - 1); l++) {
//                                            // 在添加这个服务剩余的没经过的变量
//                                            rowDataList.add("");
//                                        }
//                                    }
//                                }
                                for (int l = 0; l < varNum; l++) {
                                    rowDataList.add("2");
                                }
                            } else {
                                // 如果上面api 没有对应的变量名， 说明这个api没有经过, 但经过了这个服务, 添加对应变量名的空
                                for (int l = 0; l < varNum; l++) {
                                    rowDataList.add("");
                                }
                            }
                        }
                    }
                }

                System.out.println(rowDataList.size() + "====================");
                return RowFactory.create(rowDataList.toArray());
            }
        });

        // 表头
        List<StructField> structFields = new ArrayList<>();
        for (int i = 0; i < CloumnNameUtil.getTracePassCloumnAllList().size(); i++) {
            structFields.add(DataTypes.createStructField(CloumnNameUtil.getTracePassCloumnAllList().get(i), DataTypes.StringType, true));
        }
        Dataset<Row> tracePassDataset = spark.createDataFrame(step3Rdd, DataTypes.createStructType(structFields));

        tracePassDataset = tracePassDataset.dropDuplicates(new String[]{"trace_id"});
        ///tracePassDataset.write().saveAsTable("real_trace_pass_view");
        tracePassDataset.createOrReplaceTempView("real_trace_pass_view");
    }


    // real_invocation_view   real_trace_pass_view ,  before_trace_view
    public static void combinePassServiceToTrace(SparkSession spark) {
        Dataset<Row> beforeTraceDataset = spark.sql(TempSQL.combinePassServiceToTrace);
        //beforeTraceDataset.show();
        //beforeTraceDataset.printSchema();
        beforeTraceDataset.createOrReplaceTempView("trace_passservice_view");
        //beforeTraceDataset.write().saveAsTable("before_trace_sequence");
    }


    private static void genSequencePart(SparkSession spark) {
        System.out.println("=======begin==============");

        Dataset<Row> step1Dataset = spark.sql(TempSQL.genStep1);

//        String[] duplicasKey = new String[]{"trace_id", "sr_servicename", "caller"};
//        step1Dataset = step1Dataset.dropDuplicates(duplicasKey);
        step1Dataset = step1Dataset.orderBy("s_time");
        step1Dataset.createOrReplaceTempView("view_clean_step1");
        //   step1Dataset.write().saveAsTable("genstep1");
        System.out.println("====genStep1==-----");


        Dataset<Row> step2Dataset = spark.sql(TempSQL.genStep2);
        step2Dataset.createOrReplaceTempView("view_clean_step2");
        //  step2Dataset.write().saveAsTable("genstep2");
        System.out.println("====genStep2==-----");


        Dataset<Row> step3Dataset = spark.sql(TempSQL.genStep3);
        //  step3Dataset.write().saveAsTable("genStep3_1");
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
                String[] caller_times = row.getAs("caller_times").toString().split("___");
                //  String [] s_time = row.getAs("s_time").toString().split("||");
                String[] e_time = row.getAs("e_time").toString().split("___");
                String[] sr_servicename = row.getAs("sr_servicename").toString().split("___");

                // 保存所有的经过服务的 a_b == 0 or 1
                Map<String, String> k_v_seq = new HashMap<>();
                Map<String, String> k_v_caller = new HashMap<>();

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
                        // 前面排过序了，这里只需要a_b, 不需要b_a
                        List<String> map_pair_service = Copy_2_of_Service.callNoDoublePairService(temp_sr_service);

                        // 前面按照开始时间排序了，现在只需要按照结束时间判断
                        for (String pairSer : map_pair_service) {
                            // 应该是每一个都会执行的
                            String[] pairs = pairSer.split("__");
                            String time1 = service_etime_map.get(pairs[0]);
                            String time2 = service_etime_map.get(pairs[1]);

                            // 排除a_b a_a b_b 的重复
                            if(pairs[0] != pairs[1]) {
                                // 如果k_v_map 里面已经有这个组合了，就是重复调用
                                String pairSeqValue = k_v_seq.get(pairSer);
                                // Integer.parseInt(caller_times[i]) % 4 == 0
                                if (pairSeqValue != null && pairSeqValue != "") {
                                    k_v_seq.put(pairSer, "2");
                                } else {
                                    k_v_seq.put(pairSer, TimeUtil.compareTime(time1, time2) + "");
                                }
                                k_v_caller.put(pairSer, caller_service[i]);
                            }
                        }
                    }
                }

                // 所有服务的排列组合a_b b_a
                List<String> callerPairServiceAll = Copy_2_of_Service.callPairService(Copy_2_of_Service.callerServicePart2);
                // 加 service  pair
                for (int i = 0; i < callerPairServiceAll.size(); i++) {
                    // k_v  里面可能没有 所有的，就为-1或 null
                    String cloumnValue = k_v_seq.get(callerPairServiceAll.get(i));
                    if (cloumnValue == null || "".equals(cloumnValue)) {
                        rowDataList.add("-1"); // 没调的写个-1
                        rowDataList.add("");  // caller
                    } else {
                        rowDataList.add(cloumnValue);
                        rowDataList.add(k_v_caller.get(callerPairServiceAll.get(i)));
                    }
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

        callerSerDataSet.write().saveAsTable("seq_instance_3_1");
        //  callerSerDataSet.createOrReplaceTempView("final_seq_view");
        //System.out.println(seqCallerColumsAll.size() + "==============---------------size2"); // 1765
        System.out.println("==========over===========");
        //  return callerSerDataSet;
    }
}