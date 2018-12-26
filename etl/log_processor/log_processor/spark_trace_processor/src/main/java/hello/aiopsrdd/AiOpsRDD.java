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
                .config("spark.debug.maxToStringFields", 4000)
                .config("spark.driver.maxResultSize", "8g")
                .config("spark.sql.crossJoin.enabled", true)
                .config("spark.default.parallelism", 10)
                .appName("java spark sql")
                .master("yarn")
                .getOrCreate();
        filter(spark);

        spark.close();
        System.out.println("==============spark sql closed====");
    }


    private static void filter(SparkSession spark) {

        JavaRDD<TraceAnnotation> changeSpanTimeRdd = OriginalSpanRDD.getOriginalSpanRdd(spark, UrlCsvName.TRACE_CSV_URL);

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

        genRealInvocation(spark);

        // 经过服务的api, instance_id 等   trace_passservice_view
        combinePassServiceToTrace(spark);

        // 42 个 sql 的 left out join
        combineInstanceDataToTrace(spark);   // 产生 table  trace_combine_?

        // 竖着转行  service_config_data
        serviceConfig(spark);
        // 计算cpu,mem diff  trace_combine_?  ----   trace_combine_config_view
        combineServiceConfigToTrace(spark);
        // 从mysql 取y
        combineYtoTrace(spark); // 产生  table  trace_y_?

        SequenceRDD.genSequencePart(spark);

    }

    /**
     * 产生真的span
     *
     * @param spark
     */
    private static void genRealInvocation(SparkSession spark) {
        Dataset<Row> invocationDataset = spark.sql(TempSQL.genInvocation);
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
                .csv(UrlCsvName.SERVICE_INSTANC_CSV_URL);
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
        traceCombineInstance.write().saveAsTable(UrlCsvName.TRACE_COMBINE_INSTANC);
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
                .csv(UrlCsvName.SERVIC_CONFIG_CSV_URL);
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

        Dataset<Row> trace_combine_configDataset = spark.sql(TempSQL.combineServiceConfigToTrace);
        // 根据时间匹配的，一定要去重
        trace_combine_configDataset = trace_combine_configDataset.dropDuplicates(new String[]{"trace_id"});

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
        trace_combine_config_diff_DataSet.createOrReplaceTempView("trace_combine_config_view");
       // trace_combine_config_diff_DataSet.write().saveAsTable("trace_combine_config_3");
    }


    private static void combineYtoTrace(SparkSession spark) {
        Dataset<Row> testTraceDataSet = DBUtil.connectDBUtil(spark, UrlCsvName.DATABASE_NAME, "test_trace");
        System.out.println("testtrace   shema  printled");
        testTraceDataSet.createOrReplaceTempView("test_traces_mysql_view");
        // testTraceDataSet.write().saveAsTable("traces_mysql_view");

        Dataset<Row> finallTrace = spark.sql(TempSQL.combineYtoTrace);
        finallTrace.write().saveAsTable(UrlCsvName.TRACE_COMBINE_Y);
        System.out.println("--------------- trace_sequence_1 table created ---------------");
    }


    // real_invocation_view   real_trace_pass_view ,  before_trace_view
    public static void combinePassServiceToTrace(SparkSession spark) {

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

        Dataset<Row> beforeTraceDataset = spark.sql(TempSQL.combinePassServiceToTrace);
        beforeTraceDataset.createOrReplaceTempView("trace_passservice_view");
        //beforeTraceDataset.write().saveAsTable("before_trace_sequence");
    }



//
//    private static void finalTraceCombineSequence(SparkSession spark) {
//        Dataset<Row> trace_finalDateSet = spark.sql(TempSQL.trace_finalDateSet);
//        // `trace_final`: `trace_id`, `test_case_id`, `test_trace_id`
////        String[] duplicasKey = new String[]{"trace_id", "test_case_id", "test_trace_id"};
////        trace_finalDateSet = trace_finalDateSet.dropDuplicates(duplicasKey);
//        //  System.out.println(trace_finalDateSet.count() + "-----------===============");
//        //    trace_finalDateSet.repartition(12);
//        trace_finalDateSet.write().saveAsTable("trace_final");
//        System.out.println("======all over======");
//    }


}