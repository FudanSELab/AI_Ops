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
            gaussianInfluence[i] = Math.exp( - Math.pow(distances[i], 2) / 2 * Math.pow(sd, 2));
        }

        return CalculationSet.Average(gaussianInfluence);
    }

    public static ArrayList<String> TopKNearestFaults(ArrayList<String> newTrace, int K,
                                                      HashMap<String, ArrayList<ArrayList<String>>> faultAndTraces) {
        ArrayList<String> TopKF = new ArrayList<>();
        ArrayList<Double> TopKD = new ArrayList<>();

        ArrayList<String> faultNameSet = new ArrayList<>(faultAndTraces.keySet());
        for(int i = 0; i < faultNameSet.size(); i++){
            String faultName = faultNameSet.get(i);
            double dtf = distanceToFault(newTrace, faultAndTraces.get(faultName));

            // TODO
            double dmax = 0;
            String fmax = "";

            //TODO
            if(TopKF.size() < K) {
                TopKF.add(faultName);
                TopKD.add(dtf);
                if(dtf < dmax){
                    TopKF.remove(fmax);
                    TopKD.remove(dmax);
                }
            }
        }
        return TopKF;
    }

}
