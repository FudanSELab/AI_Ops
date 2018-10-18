package hello;

import com.google.gson.Gson;
import hello.bean.NewAnno;
import hello.bean.NewCsvFilePrinter;
import hello.bean.NewTrace;
import hello.domain.Annotation;
import hello.domain.NewAnnoation;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import hello.storage.ParquetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * reader
     */
//    @KafkaListener(topics = {"app_log"} , containerFactory = "batchFactory")
    @KafkaListener(topics = {"app_log"})
    public void consumer(String message) throws IOException {
        System.out.println("[===] KafkaConsumer - consumer");
        System.out.println("==========KafkaConsumer - consumer============");

        Gson gson = new Gson();
        NewTrace[] traces = gson.fromJson(message, NewTrace[].class);

        String spanCsvFile = "/parquet/new_span_trace.csv";

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
                    traces[i].getBinaryAnnotations()[12].getValue(),
                    traces[i].getBinaryAnnotations()[13].getValue()
            });

        }
        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());

        System.out.println("==========================");
    }

    public void genInvocationCSV() {
        System.out.println("======== generate invocation =======");
    }
}
