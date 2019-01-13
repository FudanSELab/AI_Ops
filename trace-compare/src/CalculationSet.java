public class CalculationSet {

    //标准差σ=sqrt(s^2)
    public static double StandardDiviation(double[] x) {
        int m = x.length;
        double sum = 0;
        for(int i = 0; i < m;i++){ //求和
            sum += x[i];
        }
        double dAve = sum / m;     //求平均值
        double dVar = 0;
        for(int i = 0;i < m;i++){  //求方差
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

}
