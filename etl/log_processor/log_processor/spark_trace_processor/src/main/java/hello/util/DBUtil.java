package hello.util;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DBUtil {
    //=========================  连接mysql =======================
    public static Dataset<Row> connectDBUtil(SparkSession spark, String dbName, String tableName) {
        String url = "jdbc:mysql://10.141.212.21:3306/" + dbName + "?useUnicode=true&characterEncoding=utf-8";
        Dataset<Row> jdbcDF = spark.read().format("jdbc")
                .option("url", url)
                .option("dbtable", tableName)
                .option("user", "root")
                .option("password", "root")
                .load();
        jdbcDF.printSchema();
        return jdbcDF;
    }

}