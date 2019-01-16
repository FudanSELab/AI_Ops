import java.util.ArrayList;
import java.util.HashMap;

public class DistanceToFault {

    public static double distanceToFault(ArrayList<String> newTrace, ArrayList<ArrayList<String>> faultTraces) {
        int faultTraceNum = faultTraces.size();
        // 计算new_trace到这个fault下各个trace的距离
        double[] distances = new double[faultTraceNum];
        for(int i = 0; i < faultTraceNum; i++) {
            ArrayList<String> faultTrace = faultTraces.get(i);
            distances[i] = EditDistance.editDitanceOfArray(newTrace, faultTrace);
        }

        // 求距离的标准差
        double sd = CalculationSet.StandardDiviation(distances);

        // 计入高斯影响，消除各个fault内部的变化性影响
        double[] gaussianInfluence = new double[faultTraceNum];
        for(int i = 0; i < faultTraceNum; i++) {
            gaussianInfluence[i] = CalculationSet.GaussianInfluence(distances[i], sd);
        }

        // 高斯影响的均值，就是trace与这个类型的fault的距离
        return CalculationSet.Average(gaussianInfluence);
    }


    public static ArrayList<String> TopKNearestFaults(ArrayList<String> newTrace, int K,
                                                      HashMap<String, ArrayList<ArrayList<String>>> faultAndTraces) {

        //前K个故障
        ArrayList<String> TopKF = new ArrayList<>();
        //前K个故障对应的距离
        ArrayList<Double> TopKD = new ArrayList<>();

        // 每个故障的距离
        ArrayList<Double> distances = new ArrayList<>();
        // 每个故障的名称
        ArrayList<String> faultNames = new ArrayList<>();

        ArrayList<String> oldFaultNameSet = new ArrayList<>(faultAndTraces.keySet());

        // 计算trace与各个fault之间的距离
        for(int i = 0; i < oldFaultNameSet.size(); i++){
            String faultName = oldFaultNameSet.get(i);
            double dtf = distanceToFault(newTrace, faultAndTraces.get(faultName));

            distances.add(i, dtf);
            faultNames.add(i, faultName);
            System.out.println( faultName + "值:" + dtf);
        }

        // 挑出前K个最接近的
        for(int i = 0; i < K; i++) {
            double max_value = distances.get(0);
            int max_index = 0;
            for(int j = 1; j < faultNames.size(); j++) {
                if(distances.get(j) > max_value){
                    max_value = distances.get(j);
                    max_index = j;
                }
            }
            TopKF.add(faultNames.get(max_index));
            TopKD.add(max_value);
            faultNames.remove(max_index);
            distances.remove(max_index);
        }

        return TopKF;
    }






}
