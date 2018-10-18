package hello.bean;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NewCsvFilePrinter {

    private CSVPrint csvPrint;

    public NewCsvFilePrinter(String fileName, boolean append, boolean annotationOrNot) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            init(annotationOrNot);
        } else {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            if (!append) {
                init(annotationOrNot);
            }
        }
    }

    public void init(boolean annotationOrNot) throws IOException {
        if (annotationOrNot)
            write(new String[]{"trace_id", "span_name", "span_id", "parent_id", "span_timestamp", "span_duration",
                    "anno_a1_timestamp", "anno_a1_value", "anno_a1_ipv4", "anno_a1_port", "anno_a1_servicename",
                    "anno_a2_timestamp", "anno_a2_value", "anno_a2_ipv4", "anno_a2_port", "anno_a2_servicename",
                    "bnno_node_id", "bnno_xrequist_id", "bnno_httpurl", "bnno_http_method", "bnno_user_agent",
                    "bnno_request_size", "bnno_upstream_cluster",
                    "bnno_status_code", "bnno_response_size", "bnno_response_flags" , "bnno_is_error"
            });
        else
            write(new String[]{"spanId", "bin_key", "bin_value", "bin_serviceName", "bin_ipv4", "bin_port"});

    }

    public void write(String[] values) throws IOException {
        csvPrint.writeln(values);
    }
}