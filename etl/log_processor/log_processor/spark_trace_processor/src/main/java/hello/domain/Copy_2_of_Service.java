package hello.domain;

import java.util.ArrayList;
import java.util.List;

public class Copy_2_of_Service {


    public static String[] callerServicePart3 = new String[]{
            "ui_dashboard", "login_service", "register_service", "sso_service",
            "verification_code_service", "contacts_service", "order_service",
            "order_other_service", "config_service", "station_service",
            "train_service", "travel_service", "travel2_service",
            "preserve_service", "preserve_other_service", "basic_service",
            "ticketinfo_service", "price_service", "notification_service",
            "security_service", "inside_payment_service",
            "execute_service", "payment_service",
            "rebook_service", "cancel_service", "route_service",
            "assurance_service", "seat_service", "travel_plan_service",
            "route_plan_service", "food_map_service", "food_service",
            "consign_price_service", "consign_service", "admin_order_service",
            "admin_basic_info_service", "admin_route_service", "admin_travel_service",
            "admin_user_service", "news_service", "ticket_office_service",
            "voucher_service"
    };

    public static String[] callerServicePart2 = new String[]{
            "ui_dashboard", "login_service", "register_service", "sso_service",
            "verification_code_service", "contacts_service", "order_service",
            "order_other_service", "config_service", "station_service",
            "train_service", "travel_service", "travel2_service",
            "preserve_service", "preserve_other_service", "basic_service",
            "ticketinfo_service", "price_service", "notification_service",
            "security_service", "inside_payment_service",
            "execute_service", "payment_service",
            "rebook_service", "cancel_service", "route_service",
            "assurance_service", "seat_service", "travel_plan_service",
            "route_plan_service", "food_map_service", "food_service",
            "consign_price_service", "consign_service", "admin_order_service",
            "admin_basic_info_service", "admin_route_service", "admin_travel_service",
            "admin_user_service", "news_service", "ticket_office_service",
            "voucher_service"
    };

    public static String[] serviceList = new String[]{
            "ui_dashboard", "login", "register", "sso",
            "verification_code", "contacts", "order",
            "order_other", "config", "station",
            "train", "travel", "travel2",
            "preserve", "preserve_other", "basic",
            "ticketinfo", "price", "notification",
            "security", "inside_payment",
            "execute", "payment",
            "rebook", "cancel", "route",
            "assurance", "seat", "travel_plan",
            "route_plan", "food_map", "food",
            "consign_price", "consign", "admin_order",
            "admin_basic_info", "admin_route", "admin_travel",
            "admin_user", "news", "ticket_office",
            "voucher"
    };

    public static void main(String[] args) {
      //  List<String> tt = onlyCallerPairService(new String[]{"A", "B", "C"});
        List<String> tt= Copy_2_of_Service.onlyCallerPairService(Copy_2_of_Service.callerServicePart2);

        for (int i = 0; i < tt.size(); i++) {
            System.out.println(tt.get(i));
        }
    }


    public static List<String> execute() {
        List<String> arrangeStr = callPairServiceWithCaller(serviceList);
        // 添加caller
        // 前面每个字段，每次添加到最前面
        arrangeStr.add(0, "test_case_id");
        arrangeStr.add(0, "test_trace_id");
        arrangeStr.add(0, "trace_id");

        System.out.println(arrangeStr.size() + "---------------------2333333");
        for (int i = 0; i < callerServicePart3.length ; i++) {
            arrangeStr.add(callerServicePart3[i]);
        }
        System.out.println(arrangeStr.size() + "---------------------2333333");
        return arrangeStr;
    }

    public static List<String> callPairServiceWithCaller(String[] temp) {
        return printWithCaller(combine(temp, 2));
    }


    public static List<String> onlyCallerCloumnName(String[] temp) {
        List<String> arrangeStr = onlyCallerPairService(temp);
        // 添加caller
        // 前面每个字段，每次添加到最前面
        arrangeStr.add(0, "trace_id");
        return arrangeStr;
    }

    public static List<String> onlyCallerPairService(String[] temp) {
        return printOnlyCaller(combine(temp, 2));
    }

    // a_b  b_a
    public static List<String> callPairService(String[] temp) {
        return print(combine(temp, 2));
    }

    // a_b  not b_a
    public static List<String> callNoDoublePairService(String[] temp) {
        return printNoDoublePair(combine(temp, 2));
    }


    public static List combine(String[] a, int m) {
        int n = a.length;
        if (m > n) {
            System.out.println("错误！数组a中只有" + n + "个元素。" + m + "大于" + 2 + "!!!");
        }

        List result = new ArrayList();

        int[] bs = new int[n];
        for (int i = 0; i < n; i++) {
            bs[i] = 0;
        }
        //初始化
        for (int i = 0; i < m; i++) {
            bs[i] = 1;
        }
        boolean flag = true;
        boolean tempFlag = false;
        int pos = 0;
        int sum = 0;
        //首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
        do {
            sum = 0;
            pos = 0;
            tempFlag = true;
            result.add(print(bs, a, m));

            for (int i = 0; i < n - 1; i++) {
                if (bs[i] == 1 && bs[i + 1] == 0) {
                    bs[i] = 0;
                    bs[i + 1] = 1;
                    pos = i;
                    break;
                }
            }
            //将左边的1全部移动到数组的最左边

            for (int i = 0; i < pos; i++) {
                if (bs[i] == 1) {
                    sum++;
                }
            }
            for (int i = 0; i < pos; i++) {
                if (i < sum) {
                    bs[i] = 1;
                } else {
                    bs[i] = 0;
                }
            }

            //检查是否所有的1都移动到了最右边
            for (int i = n - m; i < n; i++) {
                if (bs[i] == 0) {
                    tempFlag = false;
                    break;
                }
            }
            if (tempFlag == false) {
                flag = true;
            } else {
                flag = false;
            }

        } while (flag);
        result.add(print(bs, a, m));

        return result;
    }

    private static String[] print(int[] bs, String[] a, int m) {
        String[] result = new String[m];
        int pos = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == 1) {
                result[pos] = a[i];
                pos++;
            }
        }
        return result;
    }

    private static List<String> print(List l) {
        List<String> arrangeStr = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            String[] a = (String[]) l.get(i);
            String temp = "";

            for (int j = 0; j < a.length; j++) {
                // System.out.print(a[j] + " ");
                if (j == 0)
                    temp = temp + a[j] + "__";
                else
                    temp = temp + a[j];
            }
            arrangeStr.add(temp);
            String[] temp2 = temp.split("__");
            // 调换顺序加入列
            arrangeStr.add(temp2[1] + "__" + temp2[0]);
            //System.out.println();
        }
        return arrangeStr;
    }


    private static List<String> printOnlyCaller(List l) {
        List<String> arrangeStr = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            String[] a = (String[]) l.get(i);
            String temp = "";

            for (int j = 0; j < a.length; j++) {
                // System.out.print(a[j] + " ");
                if (j == 0)
                    temp = temp + a[j] + "__";
                else
                    temp = temp + a[j];
            }
          //  arrangeStr.add(temp + "_seq");
            arrangeStr.add(temp+"_caller");
            String[] temp2 = temp.split("__");
            // 调换顺序加入列
            // arrangeStr.add(temp2[1] + "__" + temp2[0] + "_seq");
            arrangeStr.add(temp2[1]+"__"+temp2[0]+ "_caller");
            //System.out.println();
        }
        return arrangeStr;
    }


    private static List<String> printWithCaller(List l) {
        List<String> arrangeStr = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            String[] a = (String[]) l.get(i);
            String temp = "";

            for (int j = 0; j < a.length; j++) {
                // System.out.print(a[j] + " ");
                if (j == 0)
                    temp = temp + a[j] + "__";
                else
                    temp = temp + a[j];
            }
            arrangeStr.add(temp + "_seq");
            // arrangeStr.add(temp+"_caller");
            String[] temp2 = temp.split("__");
            // 调换顺序加入列
            arrangeStr.add(temp2[1] + "__" + temp2[0] + "_seq");
            //  arrangeStr.add(temp2[1]+"__"+temp2[0]+ "_caller");
            //System.out.println();
        }
        return arrangeStr;
    }


    private static List<String> printNoDoublePair(List l) {
        List<String> arrangeStr = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            String[] a = (String[]) l.get(i);
            String temp = "";

            for (int j = 0; j < a.length; j++) {
                // System.out.print(a[j] + " ");
                if (j == 0)
                    temp = temp + a[j] + "__";
                else
                    temp = temp + a[j];
            }
            arrangeStr.add(temp);
            //System.out.println();
        }
        return arrangeStr;
    }
}
