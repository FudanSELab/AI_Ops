import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Faults {

    public HashMap<String, ArrayList<ArrayList<String>>> faults = new HashMap<>();

    public HashMap<String, ArrayList<ArrayList<String>>> ms = new HashMap<>();

    public ArrayList<ArrayList<String>> testTraces = new ArrayList<>();
    public ArrayList<String> testDimType = new ArrayList<>();
    public ArrayList<String> testMs = new ArrayList<>();

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

    //    public static void main(String[] args) throws Exception{
//        Faults f = new Faults();
//        f.inputFaults("text.txt");
//    }

    public void input_train(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        String headers = br.readLine();
        System.out.println("表头:" + headers);
        String line;
        while ((line = br.readLine()) != null){
            String[] lineElements = line.split(",");
            String trace = lineElements[1];
            String dimType = lineElements[2].toLowerCase();

            String[] traceSeq = trace.split("__");
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(traceSeq));
            if(!faults.keySet().contains(dimType)) {
                ArrayList<ArrayList<String>> faultTypeTrace = new ArrayList<>();
                faultTypeTrace.add(arrayList);
                faults.put(dimType, faultTypeTrace);
                System.out.println("增加DimType:" + dimType);
             }else{
                ArrayList<ArrayList<String>> faultTypeTrace = faults.get(dimType);
                faultTypeTrace.add(arrayList);
                faults.put(dimType, faultTypeTrace);
            }

            String traceMs = lineElements[3].toLowerCase();
            if(!ms.keySet().contains(traceMs)){
                ArrayList<ArrayList<String>> faultTypeTrace = new ArrayList<>();
                faultTypeTrace.add(arrayList);
                ms.put(traceMs, faultTypeTrace);
                System.out.println("增加MS:" + traceMs + "|");
            }else{
                ArrayList<ArrayList<String>> faultTypeTrace = ms.get(traceMs);
                faultTypeTrace.add(arrayList);
                ms.put(traceMs, faultTypeTrace);
            }

        }

    }

    public void input_test(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        String headers = br.readLine();
        System.out.println("表头:" + headers);
        String line;
        while ((line = br.readLine()) != null){
            String[] lineElements = line.split(",");
            String trace = lineElements[1];
            String dimType = lineElements[2].toLowerCase();
            String traceMs = lineElements[3].toLowerCase();

            String[] traceSeq = trace.split("__");
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(traceSeq));

            testTraces.add(arrayList);
            testDimType.add(dimType);
            testMs.add(traceMs);
        }
    }

    public static void main(String[] args) throws Exception{
        Faults f = new Faults();
        f.input_train("ts_tpds_train.csv");
        f.input_test("ts_tpds_test.csv");

        ArrayList<ArrayList<Integer>> predictResultList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> realResultList = new ArrayList<>();
        // 把Trace1作为准备预测的Trace
        // K=2 我们找出两个最接近的Fault Type
        // Faults 各种故障对应的Trace
        int total = f.testTraces.size();
        int count = 0;
        for(int i = 0; i < total;i++) {
            ArrayList<String> tempTrace = f.testTraces.get(i);
            ArrayList<String> kFaults = DistanceToFault.TopKNearestFaults(tempTrace,1,f.ms);
//            String realResult = f.testMs.get(i);
//            String predictResult = kFaults.get(0);

//            ArrayList<Integer> realResultArr = getDimTypeArr(realResult);
//            ArrayList<Integer> predictResultArr = getDimTypeArr(predictResult);
//            predictResultList.add(predictResultArr);
//            realResultList.add(realResultArr);


            System.out.println("实际:" + f.testMs.get(i) + " 预测:" + kFaults.get(0));
            if(kFaults.contains(f.testMs.get(i)))
                count++;
//            if(f.testMs.get(i).equals(kFaults.get(0)))
//                count++;
        }
//        calculateDimTypeNumber(predictResultList, realResultList);
        System.out.print("Accuracy:" + ((double)count/(double)total));


//        // 输出结果
//        for(int i = 0; i < kFaults.size(); i++){
//            System.out.println("我们选择：" + kFaults.get(i));
//        }
    }

    public static void calculateDimTypeNumber(ArrayList<ArrayList<Integer>> prediction, ArrayList<ArrayList<Integer>> real) {
        int size = prediction.size();
        int labelNumber = 3;

        double precision = 0.0;
        double recall = 0.0;
        double f1;
        for(int i = 0;i < labelNumber;i++){
            // 0为负，1为正
            int TP = 1;  // 预测为正，实际为正
            int FP = 1;  // 预测为正，实际为负
            int TN = 1;  // 预测为负，实际为负
            int FN = 1;  // 预测为负，实际为正
            for(int j = 0;j < size;j++){
                int pre_temp_label_num = prediction.get(j).get(i);
                int real_temp_label_num = real.get(j).get(i);
                if(pre_temp_label_num== 1 && real_temp_label_num  == 1)
                    TP += 1;
                else if (pre_temp_label_num == 1 && real_temp_label_num == 0)
                    FP += 1;
                else if (pre_temp_label_num == 0 && real_temp_label_num == 0)
                    TN += 1;
                else
                    FN += 1;
            }
            double tempPrecision =  (double)TP / ((double)TP + (double)FP);
            double tempRecall =  (double)TP / ((double)TP + (double)FN);
            precision += tempPrecision;
            recall += tempRecall;
            System.out.println("标签" + i + " P:" + tempPrecision + " R:" + tempRecall);
        }
        precision /= 3.0;
        recall /= 3.0;
        f1 = (2 * precision * recall) / (precision + recall);
        System.out.print("Precision:" + precision);
        System.out.print("Recall:" + recall);
        System.out.print("F1:" + f1);
    }

    public static ArrayList<Integer> getDimTypeArr(String dimType) {
        if(dimType.equals("seq")){
            ArrayList<Integer> arrList = new ArrayList<>();
            arrList.add(1);
            arrList.add(0);
            arrList.add(0);
            return arrList;
        }else if(dimType.equals("config")){
            ArrayList<Integer> arrList = new ArrayList<>();
            arrList.add(0);
            arrList.add(1);
            arrList.add(0);
            return arrList;
        }else if(dimType.equals("instance")){
            ArrayList<Integer> arrList = new ArrayList<>();
            arrList.add(0);
            arrList.add(0);
            arrList.add(1);
            return arrList;
        }else{
            ArrayList<Integer> arrList = new ArrayList<>();
            arrList.add(1);
            arrList.add(1);
            arrList.add(1);
            return arrList;
        }
    }
}
