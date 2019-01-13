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
        trace3.add("f");
        trace3.add("g");
        trace3.add("a");
        trace3.add("c");

        ArrayList<String> trace4= new ArrayList<>();
        trace4.add("c");
        trace4.add("d");
        trace4.add("e");
        trace4.add("a");
        trace4.add("b");

        ArrayList<String> trace5 = new ArrayList<>();
        trace5.add("e");
        trace5.add("d");
        trace5.add("c");
        trace5.add("b");
        trace5.add("a");



        ArrayList< ArrayList<String>> fault1 = new ArrayList<>();
        fault1.add(trace1);
        fault1.add(trace2);

        ArrayList<ArrayList<String>> fault2 = new ArrayList<>();
        fault2.add(trace3);
        fault2.add(trace4);
        fault2.add(trace5);


        System.out.println(DistanceToFault.distanceToFault(trace1, fault1));
        System.out.println(DistanceToFault.distanceToFault(trace1, fault2));


    }

}
