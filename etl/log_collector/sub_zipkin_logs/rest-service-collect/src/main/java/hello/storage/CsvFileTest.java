package hello.storage;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFileTest {


    private CSVPrint csvPrint;

    public CsvFileTest(String fileName,boolean append) throws IOException {
        File file = new File(fileName);
        if(!file.exists()){
            csvPrint = new CSVPrinter(new FileWriter(fileName,append));
            init();
        }else{
            csvPrint = new CSVPrinter(new FileWriter(fileName,append));
            if(!append){
                init();
            }
        }

    }

    public void init() throws IOException{
        write(new String[]{"traceId","spanId","parentId","name","timestamp","duration","annotation","binaryAnnotation"});
    }

    public void write(String[] values) throws IOException {
        csvPrint.writeln(values);
    }


}
