package hello;

import com.google.gson.Gson;
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

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

//	public KafkaListenerContainerFactory<?> batchFactory() {
//        ConcurrentKafkaListenerContainerFactory<Integer,String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConcurrency(8);
//        factory.setBatchListener(true);
//	    return factory;
//    }

    /**
     * reader
     */
//    @KafkaListener(topics = {"app_log"} , containerFactory = "batchFactory")
    @KafkaListener(topics = {"app_log"})
    public void consumer(String message) throws IOException {
        System.out.println("[===] KafkaConsumer - consumer");
        System.out.println("==========KafkaConsumer - consumer============");

        Gson gson = new Gson();
        Trace[] traces = gson.fromJson(message, Trace[].class);

        String annoCsvFile = "/parquet/traces_anno.csv";
        String binnoCsvFile = "/parquet/traces_binno.csv";

        CsvFilePrinter annoPrint = new CsvFilePrinter(annoCsvFile, true, true);
        CsvFilePrinter binnoPrint = new CsvFilePrinter(binnoCsvFile, true, false);

        for (int i = 0; i < traces.length; i++) {
            ParquetUtil.parquetWriter(traces[i]);
        }

        for (int i = 0; i < traces.length; i++) {

//            {"trace_id", "span_id", "span_name","parent_id", "span_timestamp", "span_duration",
//            "anno_cs_timestamp", "anno_cs", "anno_cs_servicename", "anno_cs_ip", "anno_cs_port",
//                    "anno_cr_timestamp", "anno_cr", "anno_cr_servicename", "anno_cr_ip", "anno_cr_port",
//                    "anno_sr_timestamp", "anno_sr", "anno_sr_servicename", "anno_sr_ip", "anno_sr_port",
//                    "anno_ss_timestamp", "anno_ss", "anno_ss_servicename", "anno_ss_ip", "anno_ss_port"

            NewAnnoation[] newAnnoations = new NewAnnoation[4];
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

            for (int j = 0; traces[i].getAnnotations() != null && j < traces[i].getAnnotations().length; j++) {
                //写入 anno csv
                annoPrint.write(new String[]{
                        traces[i].getTraceId(),
                        traces[i].getId(),
                        traces[i].getName(),
                        traces[i].getParentId(),
                        "" + traces[i].getTimestamp(),
                        "" + traces[i].getDuration(),
                        "" + traces[i].getAnnotations()[j].getTimestamp(),
                        traces[i].getAnnotations()[j].getValue(),
                        traces[i].getAnnotations()[j].getEndpoint().getServiceName(),
                        traces[i].getAnnotations()[j].getEndpoint().getIpv4(),
                        "" + traces[i].getAnnotations()[j].getEndpoint().getPort()
                });
            }

            for (int j = 0; traces[i].getBinaryAnnotations() != null && j < traces[i].getBinaryAnnotations().length; j++) {
                //写入 binno csv
                binnoPrint.write(new String[]{
                        traces[i].getId(),
                        traces[i].getBinaryAnnotations()[j].getKey(),
                        traces[i].getBinaryAnnotations()[j].getValue(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getServiceName(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getIpv4(),
                        "" + traces[i].getBinaryAnnotations()[j].getEndpoint().getPort(),
                });
            }
        }

        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());
        System.out.println("==========================");
    }

    public void genInvocationCSV() {
        System.out.println("======== generate invocation =======");

    }
}
