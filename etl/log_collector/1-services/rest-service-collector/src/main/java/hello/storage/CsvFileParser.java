package hello.storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.Ostermiller.util.ExcelCSVParser;
import com.Ostermiller.util.LabeledCSVParser;

public class CsvFileParser{

    private LabeledCSVParser csvParser;//csv解析器，对于第一行的表头信息，自动加载为索引关键字

    private int currLineNum = -1;//文件所读到行数

    private String[] currLine = null;//用来存放当前行的数据

    public CsvFileParser(InputStream in) throws IOException {
        csvParser = new LabeledCSVParser(new ExcelCSVParser(in));
        currLineNum = csvParser.getLastLineNumber();
    }

    public CsvFileParser(String fileName) throws IOException {
        InputStream in = new FileInputStream(fileName);
        csvParser = new LabeledCSVParser(new ExcelCSVParser(in));
        currLineNum = csvParser.getLastLineNumber();
    }

    public boolean hasMore() throws IOException {
        currLine = csvParser.getLine();
        currLineNum = csvParser.getLastLineNumber();
        if (null == currLine)
            return false;
        return true;
    }

    /*
     * 返回当前行数据，关键字所指向的数据
     * param:String filedName 该行的表头
     * return:String 返回当前行数据，关键字所指向的数据
     */
    public String getByFieldName(String fieldName) {
        return csvParser.getValueByLabel(fieldName);
    }

    public void close() throws IOException {
        csvParser.close();
    }

    public String[] readLine() throws IOException {
        currLine = csvParser.getLine();
        currLineNum = csvParser.getLastLineNumber();
        return currLine;
    }

    public int getCurrLineNum(){
        return currLineNum;
    }

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream(new File("/parquet/traces/csv"));
        CsvFileParser parser = new CsvFileParser(in);
        //读取数据
        while(parser.hasMore()){
            System.out.print(parser.getByFieldName("time") + " ");//time 系表头数据
            System.out.print(parser.getByFieldName("total") + " ");
        }
        parser.close();
    }

}