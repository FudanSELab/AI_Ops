package hello.storage;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFilePrinter {

    private CSVPrint csvPrint;

//    public CsvFilePrinter2(String fileName, boolean append) throws IOException {
//        File file = new File(fileName);
//        if (!file.exists()) {
//            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
//            init();
//        } else {
//            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
//            if (!append) {
//                init();
//            }
//        }
//    }

    public CsvFilePrinter(String fileName, boolean append, boolean annotationOrNot) throws IOException {
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
//        write(new String[]{"traceId", "spanId", "spanName","spanParentId", "spanTimeStamp",
//                "spanDuration", "anno_timeStamp", "anno_value", "anno_serviceName", "anno_ipv4", "anno_port"});
        if (annotationOrNot)
            write(new String[]{"trace_id", "span_id", "span_name","parent_id", "span_timestamp", "span_duration",
                    "anno_cs_timestamp", "anno_cs", "anno_cs_servicename", "anno_cs_ip", "anno_cs_port",
                    "anno_cr_timestamp", "anno_cr", "anno_cr_servicename", "anno_cr_ip", "anno_cr_port",
                    "anno_sr_timestamp", "anno_sr", "anno_sr_servicename", "anno_sr_ip", "anno_sr_port",
                    "anno_ss_timestamp", "anno_ss", "anno_ss_servicename", "anno_ss_ip", "anno_ss_port",
                    "res_status_code", "res_status_desc","res_exception"
                    });
        else
            write(new String[]{"spanId", "bin_key", "bin_value", "bin_serviceName", "bin_ipv4", "bin_port"});

    }

    public void write(String[] values) throws IOException {
        csvPrint.writeln(values);
    }


}