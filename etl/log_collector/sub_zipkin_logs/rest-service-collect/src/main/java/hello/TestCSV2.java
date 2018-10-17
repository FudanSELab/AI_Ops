package hello;


import com.google.gson.Gson;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import hello.storage.CsvFileTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class TestCSV2 {

    private static final Logger log = LoggerFactory.getLogger(Application.class);


    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    public static void test() throws IOException {
        System.out.println("==========KafkaConsumer - consumer============");

        StringBuffer sb = new StringBuffer();
        String filePath = "G:\\dailywork\\micro_doc\\faults\\F13\\step2_fault_origin_traces\\case4\\fail2\\cancel.json";
        readToBuffer(sb,filePath);

        Gson gson = new Gson();
        Trace[] traces = gson.fromJson(sb.toString(), Trace[].class);

        String annoCsvFile = "C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces_anno6.csv";
        CsvFileTest annoPrint = new CsvFileTest(annoCsvFile,true);
        for(int i = 0; i < traces.length; i++){
     System.out.println(gson.toJson(traces[i].getAnnotations()).replaceAll(",","|"));
            //写入csv
            annoPrint.write(new String[]{
                    traces[i].getTraceId(),
                    traces[i].getId(),
                    traces[i].getParentId(),
                    traces[i].getName(),
                    "" + traces[i].getTimestamp(),
                    "" + traces[i].getDuration(),
                     "\""+gson.toJson(traces[i].getAnnotations()).replaceAll(",","|"),
                     "\""+gson.toJson(traces[i].getBinaryAnnotations()).replaceAll(",","|")
            });
            //写入Parquet
            //ParquetUtil.parquetWriter(traces[i]);
        }
        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());

        System.out.println("==========================");
    }
//    public static void main(String [] args) throws IOException {
//        test();
//    }
}
