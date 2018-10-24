package hello;

import hello.aiopsrdd.AiOpsRDD;

public class SparkApplication {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("==========Spark  begin =========");
        AiOpsRDD.executor();
        System.out.println("==========Spark   end  =========");
    }

    public static void test(){
        String test = "1537346507816000";
        System.out.println(test.substring(0,13));
    }
}
