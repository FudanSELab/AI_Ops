package hello;

import com.google.gson.Gson;
import hello.domain.NewAnno;
import hello.domain.NewCsvFilePrinter;
import hello.domain.NewTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        // 遍历收集到的span, 过滤isito的
        for (int i = 0; i < traces.length; i++) {
            if ("Report".equals(traces[i].getName())) {
                continue;
            }
            if ("rest-service-collect".equals(traces[i].getAnnotations()[0].getEndpoint().getServiceName())) {
                continue;
            }
            NewAnno[] newAnnoations = new NewAnno[2];
            newAnnoations[0] = new NewAnno();
            newAnnoations[1] = new NewAnno();

            // Annotations  2
            for (int j = 0; traces[i].getAnnotations() != null && j < traces[i].getAnnotations().length; j++) {
                newAnnoations[j] = traces[i].getAnnotations()[j];
            }

            //  sidecar~10.244.4.70~ts-config-service-646c4b8dc5-z6cv2.default~default.svc.cluster.local
            String user_agent = traces[i].getBinaryAnnotations()[6].getValue();
            user_agent = user_agent.replaceAll("\\[", "").replaceAll("]", "");
            //
            if (!user_agent.contains(",")) {
                continue;
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
                    newAnnoations[0].getEndpoint().getPort() + "",
                    newAnnoations[0].getEndpoint().getServiceName(),

                    "" + newAnnoations[1].getTimestamp(),
                    newAnnoations[1].getValue(),
                    newAnnoations[1].getEndpoint().getIpv4(),
                    newAnnoations[1].getEndpoint().getPort() + "",
                    newAnnoations[1].getEndpoint().getServiceName(),

                    traces[i].getBinaryAnnotations()[0].getValue(),
                    traces[i].getBinaryAnnotations()[1].getValue(),
                    traces[i].getBinaryAnnotations()[2].getValue(),
                    traces[i].getBinaryAnnotations()[3].getValue(),
                    traces[i].getBinaryAnnotations()[4].getValue(),
                    traces[i].getBinaryAnnotations()[5].getValue(),
                    //traces[i].getBinaryAnnotations()[6].getValue(), // user_agent
                    user_agent.split(",")[1],
                    user_agent.split(",")[0],
//                    "test_trace_id" + i,
//                    "test_case_id" + i,

                    traces[i].getBinaryAnnotations()[7].getValue(),
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

    public void genInvocationCSV() {
        System.out.println("======== generate invocation =======");
    }
}
