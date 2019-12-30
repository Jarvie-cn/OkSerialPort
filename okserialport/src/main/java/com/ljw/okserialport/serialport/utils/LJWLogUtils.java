package com.ljw.okserialport.serialport.utils;

import android.util.Log;

/**
 * @author : LJW
 * @date : 2019/12/29
 * @desc :
 */
public class LJWLogUtils {

    public static boolean isDebug = true;

    public void setDebug(boolean debug) {
        isDebug = debug;
    }


    public static void  e(String text){
        if (isDebug) {
            Log.e("LJW",text);
        }
    }
}
