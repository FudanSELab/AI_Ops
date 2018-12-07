package hello.util;

public class TimeUtil {

    public static int compareTime(String time1, String time2){
        System.out.println(System.currentTimeMillis());
        Long a1 = Long.valueOf(time1);
        Long a2 = Long.valueOf(time2);
        if(a1> a2){
            System.out.println("0");
            return 0;
        }else{
            System.out.print("1");
            return 1;
        }
    }
}