package hello;


import com.google.gson.Gson;
import hello.domain.Annotation;
import hello.domain.NewAnnoation;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class TestCSV {

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
        String filePath = "C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\cancel-1.json";
        readToBuffer(sb,filePath);

        Gson gson = new Gson();
        Trace[] traces = gson.fromJson(sb.toString(), Trace[].class);

        String annoCsvFile = "C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces_anno22.csv";
        String binnoCsvFile = "C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces_binno22.csv";
        CsvFilePrinter annoPrint = new CsvFilePrinter(annoCsvFile,true, true);
        CsvFilePrinter binnoPrint = new CsvFilePrinter(binnoCsvFile,true , false);

        for(int i = 0; i < traces.length; i++){

            NewAnnoation[] newAnnoations = new NewAnnoation[4];
            newAnnoations[0] = new NewAnnoation();
            newAnnoations[1] = new NewAnnoation();
            newAnnoations[2] = new NewAnnoation();
            newAnnoations[3] = new NewAnnoation();

            for (int j = 0; traces[i].getAnnotations() != null && j < traces[i].getAnnotations().length; j++) {
                Annotation tempAnno = traces[i].getAnnotations()[j];
                if(tempAnno.getValue().equals("cs")){
                    newAnnoations[0] = new NewAnnoation(tempAnno.getTimestamp(),tempAnno.getValue(),tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(),tempAnno.getEndpoint().getPort());
                }
                if(tempAnno.getValue().equals("cr")){
                    newAnnoations[1] = new NewAnnoation(tempAnno.getTimestamp(),tempAnno.getValue(),tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(),tempAnno.getEndpoint().getPort());
                }
                if(tempAnno.getValue().equals("sr")){
                    newAnnoations[2] = new NewAnnoation(tempAnno.getTimestamp(),tempAnno.getValue(),tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(),tempAnno.getEndpoint().getPort());
                }
                if(tempAnno.getValue().equals("ss")){
                    newAnnoations[3] = new NewAnnoation(tempAnno.getTimestamp(),tempAnno.getValue(),tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(),tempAnno.getEndpoint().getPort());
                }
            }

            for(int j =0 ; traces[i].getAnnotations()!= null && j< traces[i].getAnnotations().length; j++){
                //写入 anno csv
                annoPrint.write(new String[]{
                        traces[i].getTraceId(),
                        traces[i].getId(),
                        traces[i].getName(),
                        traces[i].getParentId(),
                        "" + traces[i].getTimestamp(),
                        "" + traces[i].getDuration(),

                        ""+ newAnnoations[0].getTimestamp(),
                        newAnnoations[0].getValue(),
                        newAnnoations[0].getServiceName(),
                        newAnnoations[0].getIpv4(),
                        ""+ newAnnoations[0].getPort(),

                        ""+ newAnnoations[1].getTimestamp(),
                        newAnnoations[1].getValue(),
                        newAnnoations[1].getServiceName(),
                        newAnnoations[1].getIpv4(),
                        ""+ newAnnoations[1].getPort(),


                        ""+ newAnnoations[2].getTimestamp(),
                        newAnnoations[2].getValue(),
                        newAnnoations[2].getServiceName(),
                        newAnnoations[2].getIpv4(),
                        ""+ newAnnoations[2].getPort(),


                        ""+ newAnnoations[3].getTimestamp(),
                        newAnnoations[3].getValue(),
                        newAnnoations[3].getServiceName(),
                        newAnnoations[3].getIpv4(),
                        ""+ newAnnoations[3].getPort(),

                });
            }

            for(int j =0 ; traces[i].getBinaryAnnotations()!= null && j< traces[i].getBinaryAnnotations().length; j++){
                //写入 binno csv
                binnoPrint.write(new String[]{
                        traces[i].getId(),
                        traces[i].getBinaryAnnotations()[j].getKey(),
                        traces[i].getBinaryAnnotations()[j].getValue(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getServiceName(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getIpv4(),
                        ""+traces[i].getBinaryAnnotations()[j].getEndpoint().getPort(),
                });
            }
        }


        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());

        System.out.println("==========================");
    }
    public static void main(String [] args) throws IOException {
        test();
    }
}
