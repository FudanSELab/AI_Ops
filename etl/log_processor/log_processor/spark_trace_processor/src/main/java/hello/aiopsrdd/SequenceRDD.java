package hello.aiopsrdd;

import hello.domain.Copy_2_of_Service;
import hello.util.TimeUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceRDD {



    public static void genSequencePart(SparkSession spark) {
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


        JavaRDD<Row> step2RDD = step3Dataset.javaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                // 最终返回的一行数据
                List<String> rowDataList = new ArrayList<>();
                String trace_id = row.getAs("trace_id");
                rowDataList.add(trace_id);

                String[] caller_service = row.getAs("caller").toString().split("___");
                String[] sr_servicename = row.getAs("sr_servicename").toString().split("___");

                Map<String, String> k_v_caller = new HashMap<>();
                for (int i = 0; i < caller_service.length; i++) {
                    // 拿到一个caller 经过的所有服务
                    String[] temp_sr_service = sr_servicename[i].split(",");
                    for (int j = 0; j < temp_sr_service.length; j++) {
                        temp_sr_service[j] = temp_sr_service[j]
                                .replaceAll("ts-", "")
                                .replaceAll("-", "_");
                    }
                    // 前面排过序了，这里只需要a_b, 不需要b_a
                    // admin_travel_service__voucher_service
                    List<String> map_pair_service = Copy_2_of_Service.callNoDoublePairService(temp_sr_service);

                    // 前面按照开始时间排序了，现在只需要按照结束时间判断
                    for (String pairSer : map_pair_service) {
                        // 应该是每一个都会执行的
                        String[] pairs = pairSer.split("__");
                        // 排除a_b a_a b_b 的重复
                        if (pairs[0] != pairs[1]) {
                            k_v_caller.put(pairSer, caller_service[i]);
                        }
                    }
                }

                // admin_travel_service__voucher_service_caller
                //  所有服务的排列组合a_b_caller b_a_caller
                List<String> callerPairServiceAll = Copy_2_of_Service.onlyCallerPairService(Copy_2_of_Service.callerServicePart2);
                // 加 service  pair
                for (int i = 0; i < callerPairServiceAll.size(); i++) {
                    // k_v  里面可能没有 所有的，就为-1或 null
                    String cloumnValue = k_v_caller.get(callerPairServiceAll.get(i).replaceAll("_caller", ""));
                    if (cloumnValue == null || "".equals(cloumnValue)) {
                        rowDataList.add("");  // caller
                    } else {
                        rowDataList.add(cloumnValue);
                    }
                }
                return RowFactory.create(rowDataList.toArray());
            }
        });
        // caller end
        List<String> onlyCallerCloumnName = Copy_2_of_Service.onlyCallerCloumnName(Copy_2_of_Service.callerServicePart2);
        // 表头
        List<StructField> structFields1 = new ArrayList<>();
        for (int i = 0; i < onlyCallerCloumnName.size(); i++) {
            structFields1.add(DataTypes.createStructField(onlyCallerCloumnName.get(i), DataTypes.StringType, true));
        }
        StructType structType1 = DataTypes.createStructType(structFields1);
        Dataset<Row> callerDataset = spark.createDataFrame(step2RDD, structType1);
        callerDataset.write().saveAsTable(UrlCsvName.SEQ_CALLER);





        //  seq begin

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
                // String[] caller_times = row.getAs("caller_times").toString().split("___");
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
                            if (pairs[0] != pairs[1]) {
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
                //  所有服务的排列组合a_b b_a
                List<String> callerPairServiceAll = Copy_2_of_Service.callPairService(Copy_2_of_Service.callerServicePart2);
                // 加 service  pair
                for (int i = 0; i < callerPairServiceAll.size(); i++) {
                    // k_v  里面可能没有 所有的，就为-1或 null
                    String cloumnValue = k_v_seq.get(callerPairServiceAll.get(i));
                    if (cloumnValue == null || "".equals(cloumnValue)) {
                        rowDataList.add("-1"); // 没调的写个-1
                        // rowDataList.add("");  // caller
                    } else {
                        rowDataList.add(cloumnValue);
                        // rowDataList.add(k_v_caller.get(callerPairServiceAll.get(i)));
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
        callerSerDataSet.write().saveAsTable(UrlCsvName.SEQ_SEQUENCE);
        //  callerSerDataSet.createOrReplaceTempView("final_seq_view");

        System.out.println("==========over===========");
        //  return callerSerDataSet;
    }
}
