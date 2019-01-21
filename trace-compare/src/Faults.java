import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Faults {

    private HashMap<String, ArrayList<ArrayList<String>>> faults = new HashMap<>();

    public void inputFaults(String filePath) throws Exception {

        BufferedReader br =
                new BufferedReader(new FileReader(new File(filePath)));

        String faultsNumLine = br.readLine();
        int faultsNum = Integer.parseInt(faultsNumLine);
        System.out.println("读入故障的数量为：" + faultsNum);

        for(int i = 0; i < faultsNum; i++) {

            String faultsNameAndTraceNumberLine = br.readLine();
            String[] nameAndTraceArr = faultsNameAndTraceNumberLine.split(",");
            String faultName = nameAndTraceArr[0];
            int faultTraceNum = Integer.parseInt(nameAndTraceArr[1]);
            System.out.println("故障名称:" + faultName + " 数量:" + faultTraceNum);

            ArrayList<ArrayList<String>> traces = new ArrayList<>();
            for(int j = 0; j < faultTraceNum; j++) {
                String svcSeqLine = br.readLine();
                String[] svcs = svcSeqLine.split(",");
                ArrayList<String> svcSeqArr = new ArrayList<>();
                for(int k = 0; k < svcs.length; k++){
                    svcSeqArr.add(svcs[k]);
                }
                traces.add(svcSeqArr);
                System.out.print("长度:" + svcSeqArr.size());
                System.out.println(svcSeqArr.toString());
            }
            faults.put(faultName, traces);
        }
    }

    public static void main(String[] args) throws Exception{
        Faults f = new Faults();
        f.inputFaults("text.txt");
    }

}
