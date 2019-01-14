import java.util.ArrayList;

public class CalculationSet {

    public static double GaussianInfluence(double distance, double sd) {
        if(sd == 0) {
            return Math.exp( - (Math.pow(distance, 2) / 2 * Math.pow(sd, 2)) );
        } else {
            return Math.exp( - (Math.pow(distance, 2) / 2 * Math.pow(sd, 2)) );
        }
    }

    public static double StandardDiviation(double[] x) {
        int m = x.length;
        double dAve = Average(x);     //求平均值
        double dVar = 0;
        for(int i = 0;i < m;i++){     //求方差
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    public static double Average(double[] x) {
        int m = x.length;
        double sum = 0;
        for(int i = 0; i < m;i++){ //求和
            sum += x[i];
        }
        double dAve = sum / m;     //求平均值
        return dAve;
    }

    public static int getMaxIndex(ArrayList<Double> x) {
        int len = x.size();
        int index = 0;
        double MAX = Double.MIN_VALUE;
        for(int i = 0; i < len; i++) {
            if(x.get(i) > MAX) {
                MAX = x.get(i);
                index = i;
            }
        }
        return index;
    }

}
