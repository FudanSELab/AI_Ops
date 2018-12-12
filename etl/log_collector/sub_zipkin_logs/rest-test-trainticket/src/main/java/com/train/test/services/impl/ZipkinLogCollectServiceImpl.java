package com.train.test.services.impl;

import com.train.test.services.ZipkinLogCollectService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ZipkinLogCollectServiceImpl implements ZipkinLogCollectService {
    private static Boolean flag = true;
    private static String span_CsvFile = "/parquet/new_span_trace.csv";
    private static Long countNum = 0L;

    @Override
    public void startCopyLogToHDFS() {
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

    @Override
    public String stopCopyLogToHDFS() {
        flag = false;
        return "Stop collecting resource data succeed!";
    }

    public static void copySpanFileToHdfs() {

        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
            FileSystem fs = FileSystem.get(conf);
            System.out.println("================  begin create Anno file =============");
            Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/new_span_trace_instance.csv");

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
