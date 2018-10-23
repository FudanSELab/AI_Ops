package hello;

import hello.storage.ParquetUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrameNaFunctions;
import org.apache.spark.sql.SQLContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Method;


public class TestParquet {

//    public static void main(String [] args) throws IOException {
//        System.out.print("df----------------------------------");
//        ParquetUtil.parquetReader("C:\\Users\\liuZOZO\\Desktop\\babs_open_data_year_1\\traces.parquet");
//    }
}
