package hello;


import com.google.gson.Gson;
import hello.bean.NewAnno;
import hello.bean.NewCsvFilePrinter;
import hello.bean.NewTrace;
import hello.domain.Annotation;
import hello.domain.NewAnnoation;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Arrays;

@SpringBootApplication
public class TestNewCSV {

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
        String filePath = "C:\\Users\\Thinkpad\\Desktop\\json\\newspan.json";
        readToBuffer(sb, filePath);

        Gson gson = new Gson();
        NewTrace[] traces = gson.fromJson(sb.toString(), NewTrace[].class);

        String spanCsvFile = "C:\\Users\\Thinkpad\\Desktop\\json\\span_csv.csv";
        NewCsvFilePrinter annoPrint = new NewCsvFilePrinter(spanCsvFile, true, true);


        for (int i = 0; i < traces.length; i++) {
            if("Report".equals(traces[i].getName())){
                continue;
            }
            if("rest-service-collect".equals(traces[i].getAnnotations()[0].getEndpoint().getServiceName())){
                continue;
            }

            NewAnno[] newAnnoations = new NewAnno[2];
            newAnnoations[0] = new NewAnno();
            newAnnoations[1] = new NewAnno();

            // Annotations  2
            for (int j = 0; traces[i].getAnnotations() != null && j < traces[i].getAnnotations().length; j++) {
                newAnnoations[j] = traces[i].getAnnotations()[j];
            }

            //写入  csv
            annoPrint.write(new String[]{
                    traces[i].getTraceId(),
                    traces[i].getName(),
                    traces[i].getId(),
                    traces[i].getParentId(),
                    "" + traces[i].getTimestamp(),
                    "" + traces[i].getDuration(),

                    "" + newAnnoations[0].getTimestamp(),
                    newAnnoations[0].getValue(),
                    newAnnoations[0].getEndpoint().getIpv4(),
                    newAnnoations[0].getEndpoint().getPort()+"",
                    newAnnoations[0].getEndpoint().getServiceName(),

                    "" + newAnnoations[1].getTimestamp(),
                    newAnnoations[1].getValue(),
                    newAnnoations[1].getEndpoint().getIpv4(),
                    newAnnoations[1].getEndpoint().getPort()+"",
                    newAnnoations[1].getEndpoint().getServiceName(),

                    traces[i].getBinaryAnnotations()[1].getValue(),
                    traces[i].getBinaryAnnotations()[2].getValue(),
                    traces[i].getBinaryAnnotations()[3].getValue(),
                    traces[i].getBinaryAnnotations()[4].getValue(),
                    traces[i].getBinaryAnnotations()[6].getValue(),
                    traces[i].getBinaryAnnotations()[8].getValue(),
                    traces[i].getBinaryAnnotations()[9].getValue(),
                    traces[i].getBinaryAnnotations()[10].getValue(),
                    traces[i].getBinaryAnnotations()[11].getValue(),
                    traces[i].getBinaryAnnotations()[12].getValue()
            });

        }
        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());

        System.out.println("==========================");
    }

//    public static void main(String[] args) throws IOException {
//        test();
//    }
}
