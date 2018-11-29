package com.train.test.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrangeInStanceNum {

    private static List<Integer> tmpArr = new ArrayList<>();
    private static HashMap<Integer, String> tmpArrList = new HashMap<>();

    private static HashMap<Integer, List<Integer>> numList = new HashMap<>();

    /**
     ts-login-service  ts-travel2-service  ts-travel-service  ts-contacts-service
     ts-food-service  ts-preserve-service    ts-inside-payment-service   ts-execute-service

     1 1 1 1   1 1 1 1
     2 1 1 1   1 1 1 1
     1 2 1 1   1 1 1 1
     1 1 2 1   1 1 1 1
     1 1 1 2   1 1 1 1
     1 1 1 1   2 1 1 1
     1 1 1 1   1 2 1 1
     1 1 1 1   1 1 2 1
     1 1 1 1   1 1 1 2
     2 2 1 1   1 1 1 1
     2 1 2 1   1 1 1 1
     2 1 1 2   1 1 1 1
     c80 c81 c82 c83 c84
     */

    /**
     *
     * @param numKind  有8个数字， 每个数字有 1，2两种情况
     * @return
     */
    public static HashMap<Integer, List<Integer>> getAllrangeList(int numKind) {
        // 12345678
        //int[] arr = new int[]{0,1,2,3,4,5,6,7};
        int[] arr = new int[numKind];
        for(int i=0 ;i<numKind;i++){
            arr[i] = i;
        }

        int allKind = (int)Math.pow(2,numKind);

        for (int i = 0; i < allKind; i++) {
            List<Integer> temp = new ArrayList<>();
            for (int j = 0; j < numKind; j++)
                temp.add(1);
            numList.put(i, temp);
        }

        for (int i = 0; i <= numKind; i++) {
            combine(0, i, arr);
        }
        // System.out.println(tmpArrList.size()+ ",");
        // 255 种
        for(int i =0 ;i< tmpArrList.size(); i++){
            String lineString = tmpArrList.get(i).toString().replaceAll("\\[", "").replaceAll("]", "");
            lineString =lineString.replaceAll(" ","");
            String[] lineList = lineString.split(",");
            //  System.out.println(tep[0]);
            for(int ii =0 ;ii<lineList.length ; ii++)
                numList.get(i+1).set(Integer.parseInt(lineList[ii]),2);
        }
        return numList;
    }

    /**
     * 组合
     * 按一定的顺序取出元素，就是组合,元素个数[C arr.len 3]
     * @param tmpArrList 保存所有的list
     * @param tmpArr 某一行的数据list
     * @param index 开始选取元素位置
     * @param k     选取的元素个数
     * @param arr   数组
     */
    public static void combine(int index, int k, int[] arr) {
        if (k == 1) {
            for (int i = index; i < arr.length; i++) {
                tmpArr.add(arr[i]);
                //  System.out.println(tmpArr.toString() + ",");
                tmpArrList.put(tmpArrList.size(), tmpArr.toString());
                tmpArr.remove((Object) arr[i]);
            }
        } else if (k > 1) {
            for (int i = index; i <= arr.length - k; i++) {
                tmpArr.add(arr[i]); //tmpArr都是临时性存储一下
                combine(i + 1, k - 1, arr); //索引右移，内部循环，自然排除已经选择的元素
                tmpArr.remove((Object) arr[i]); //tmpArr因为是临时存储的，上一个组合找出后就该释放空间，存储下一个元素继续拼接组合了
            }
        } else {
            return;
        }
    }
}
