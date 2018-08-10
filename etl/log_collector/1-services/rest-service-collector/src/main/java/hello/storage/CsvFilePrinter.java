package hello.storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

public class CsvFilePrinter{

    private CSVPrint csvPrint;

    public CsvFilePrinter(String fileName,boolean append) throws IOException {
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