package com.train.test.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    /**
     * wait seconds
     * @param waitSeconds
     */
    public static void waitMinutes(int waitSeconds){
        try {
            TimeUnit.SECONDS.sleep(waitSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
