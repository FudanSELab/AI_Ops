package hello;

import com.google.gson.Gson;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import hello.storage.ParquetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * reader
     */
    @KafkaListener(topics = {"app_log"})
    public void consumer(String message) throws IOException{
        System.out.println("[===] KafkaConsumer - consumer");
        System.out.println("==========KafkaConsumer - consumer============");

        Gson gson = new Gson();
        Trace[] traces = gson.fromJson(message, Trace[].class);

        String csvFile = "/parquet/traces.csv";
        CsvFilePrinter print = new CsvFilePrinter(csvFile,true);




        for(int i = 0; i < traces.length; i++){

            //写入csv
            print.write(new String[]{
                    traces[i].getTraceId(),
                    traces[i].getId(),
                    traces[i].getParentId(),
                    traces[i].getName(),
                    "" + traces[i].getTimestamp(),
                    "" + traces[i].getDuration(),
                    gson.toJson(traces[i].getAnnotations()),
                    gson.toJson(traces[i].getBinaryAnnotations())
            });

            //写入Parquet
            //ParquetUtil.parquetWriter(traces[i]);
        }



        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());

        System.out.println("==========================");
    }
}
