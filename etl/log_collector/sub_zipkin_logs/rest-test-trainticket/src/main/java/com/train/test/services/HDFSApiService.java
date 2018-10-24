package com.train.test.services;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HDFSApiService {

    private static Boolean flag = true;
    private static String span_CsvFile = "/parquet/new_span_trace.csv";
    private static Long countNum = 0L;
    public static  void getResourceData() {

        while (flag) {
            try {
                System.out.println("-----------copy-"+ countNum + "-times----------");
                countNum = countNum +1;
                copySpanFileToHdfs();
                Thread.sleep(120000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static String stopCopyFileToHDFS() {
        flag = false;
        return "Stop collecting resource data succeed!";
    }

    public static void copySpanFileToHdfs() {

        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
            FileSystem fs = FileSystem.get(conf);
            System.out.println("================  begin create Anno file =============");
            Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/new_span_trace.csv");

            if (fs.exists(newFile)) {
                fs.delete(newFile, false);
            }
            fs.copyFromLocalFile(new Path(span_CsvFile), newFile);
            System.out.println("================  create Anno file end =============");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finally!");
        }
    }
}
