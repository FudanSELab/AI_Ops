package hello;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;

public class HDFSApiDemo {

    public static void testHDFS() {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
            FileSystem fs = FileSystem.get(conf);

 //            System.out.println("================  begin create file =============");
//            FSDataOutputStream out = fs.create(newFile);
//            out.writeUTF("Hello World！");
//            out.close();

            Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/traces_anno22.csv");
            if (fs.exists(newFile)) {
                fs.delete(newFile, false);
            }

            fs.copyFromLocalFile(new Path("C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces_anno22.csv"), newFile);
            System.out.println("================  create file   end =============");

            System.out.println("================  read   file =============");
            OutputStream outos = new ByteArrayOutputStream();
            String str = null;

            FSDataInputStream in = fs.open(newFile);
            IOUtils.copyBytes(in, outos, 4096, false);
            str = ((ByteArrayOutputStream) outos).toString();
            System.out.println(str);
            fs.close();

            System.out.println("================  read   file   over =============");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finally!");
        }
    }

//    public static void main(String[] args) {
//        testHDFS();
//    }
}
