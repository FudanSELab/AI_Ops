package hello;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;

public class HDFSApiDemo {
    private static Boolean flag = true;
    private static String annoCsvFile = "/parquet/traces_anno.csv";
    private static String binnoCsvFile = "/parquet/traces_binno.csv";

    public static  void getResourceData() {

        while (flag) {
            try {
                copyAnnoFileToHdfs();
                copyBinnoFileToHdfs();
                Thread.sleep(300000);
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

    public static void copyAnnoFileToHdfs() {

        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
            FileSystem fs = FileSystem.get(conf);
            System.out.println("================  begin create Anno file =============");
            Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/traces_anno.csv");

            if (fs.exists(newFile)) {
                fs.delete(newFile, false);
            }
            fs.copyFromLocalFile(new Path(annoCsvFile), newFile);
            System.out.println("================  create Anno file end =============");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finally!");
        }
    }

    public static void copyBinnoFileToHdfs() {

        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
            FileSystem fs = FileSystem.get(conf);
            System.out.println("================  begin create Binno file =============");
            Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/traces_binno.csv");
            if (fs.exists(newFile)) {
                fs.delete(newFile, false);
            }
            fs.copyFromLocalFile(new Path(binnoCsvFile), newFile);
            System.out.println("================  create Binno file end =============");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finally!");
        }
    }
}
