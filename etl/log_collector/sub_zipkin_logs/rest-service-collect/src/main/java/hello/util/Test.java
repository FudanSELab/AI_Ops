package hello.util;

public class Test {
    public static void main(String[] args){
        RemoteExecuteCommand rec = new RemoteExecuteCommand("10.141.212.140", "root","root");
        //执行脚本
        System.out.println(rec.execute("nc -lk 9999  >> bbb.txt"));


    }
}
