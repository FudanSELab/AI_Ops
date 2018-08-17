package hello;

import com.google.gson.Gson;
import hello.domain.Anno1;
import hello.domain.Trace;
import hello.storage.CsvFileParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class testReadCsv {

    public static void main(String[] args) throws IOException {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces_anno1.csv"));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            Gson gson = new Gson();
            List<Anno1> annoyList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                Anno1 temp = new Anno1(item[0], item[1], item[2], item[3], Long.parseLong(item[4]), Long.parseLong(item[5]), Long.parseLong(item[6]), item[7], item[8], item[9], Integer.parseInt(item[10]));
                annoyList.add(temp);
            }

            List<Anno1> tempList = new ArrayList<>();

            for (int i = 0; i < annoyList.size(); i++) {
                String traceId = annoyList.get(i).getTraceId();
                if(annoyList.get(i+1) != null && traceId.equals(annoyList.get(i+1).getTraceId())){

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
