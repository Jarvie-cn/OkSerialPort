package com.ljw.okserialport.serialport.utils;

import android.util.Log;

/**
 * @author : LJW
 * @date : 2019/12/29
 * @desc :
 */
public class OkSerialPortLog {

    public static boolean isDebug = false;

    public void setDebug(boolean debug) {
        isDebug = debug;
    }


    public static void  e(String text){
        if (isDebug) {
            Log.e("OkSerialport",text);
        }
    }
}
