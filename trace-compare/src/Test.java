import java.util.ArrayList;
import java.util.HashMap;

public class Test {

    public static void main(String[] args){
        ArrayList<String> trace1 = new ArrayList<>();
        trace1.add("a");
        trace1.add("b");
        trace1.add("c");
        trace1.add("d");
        trace1.add("e");

        ArrayList<String> trace2 = new ArrayList<>();
        trace2.add("b");
        trace2.add("a");
        trace2.add("c");
        trace2.add("d");
        trace2.add("e");

        ArrayList<String> trace3= new ArrayList<>();
        trace3.add("e");
        trace3.add("d");
        trace3.add("c");
        trace3.add("b");
        trace3.add("a");

        ArrayList<String> trace4= new ArrayList<>();
        trace4.add("e");
        trace4.add("d");
        trace4.add("c");
        trace4.add("a");
        trace4.add("b");

        ArrayList<String> trace5 = new ArrayList<>();
        trace5.add("a");
        trace5.add("v");
        trace5.add("d");
        trace5.add("d");
        trace5.add("c");



        ArrayList<String> trace6 = new ArrayList<>();
        trace6.add("9");
        trace6.add("1");
        trace6.add("3");
        trace6.add("2");
        trace6.add("1");

        ArrayList<String> trace7 = new ArrayList<>();
        trace7.add("a");
        trace7.add("1");
        trace7.add("3");
        trace7.add("2");
        trace7.add("a");

        // 第一种故障类型和相关的trace
        ArrayList< ArrayList<String>> fault1 = new ArrayList<>();
        fault1.add(trace1);
        fault1.add(trace2);

        // 第二种故障类型和相关的trace
        ArrayList<ArrayList<String>> fault2 = new ArrayList<>();
        fault2.add(trace1);
        fault2.add(trace4);
        fault2.add(trace5);

        // 第三种故障类型和相关的trace
        ArrayList<ArrayList<String>> fault3 = new ArrayList<>();
        fault3.add(trace6);
        fault3.add(trace7);

        // 故障名称和相关trace的集合
        HashMap<String, ArrayList<ArrayList<String>>> faults = new HashMap<>();
        faults.put("FAULT-ONE", fault1);
        faults.put("FAULT-TWO", fault2);
        faults.put("FAULT-THREE", fault3);

        // 把Trace1作为准备预测的Trace
        // K=2 我们找出两个最接近的Fault Type
        // Faults 各种故障对应的Trace
        ArrayList<String> kFaults = DistanceToFault.TopKNearestFaults(trace1,2,faults);

        // 输出结果
        for(int i = 0; i < kFaults.size(); i++){
            System.out.println("我们选择：" + kFaults.get(i));
        }



    }

}
