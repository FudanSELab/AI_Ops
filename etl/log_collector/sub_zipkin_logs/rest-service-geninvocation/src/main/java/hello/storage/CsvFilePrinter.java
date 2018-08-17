package hello.storage;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFilePrinter {

    private CSVPrint csvPrint;

    public CsvFilePrinter(String fileName, boolean append) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            init();
        } else {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            if (!append) {
                init();
            }
        }
    }

    public void init() throws IOException {
        write(new String[]{"invocation_id", "trace_id", "session_id", "req_duration", "req_service", "req_api",
                "req_param_*", "exec_duration", "exec_logs", "rest_status_code", "res_body_*", "res_duration", "error_or_not"});
    }

    public void write(String[] values) throws IOException {
        csvPrint.writeln(values);
    }


}