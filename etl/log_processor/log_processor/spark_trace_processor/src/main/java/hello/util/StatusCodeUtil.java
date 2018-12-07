package hello.util;

public class StatusCodeUtil {

    public static String genInstanceStatus(String statusCode) {
        if ("200".equals(statusCode)) {
            return "1";
        } else if ("304".equals(statusCode)) {
            return "2";
        } else if ("500".equals(statusCode)) {
            return "4";
        } else {
            return "5";
        }
    }
}