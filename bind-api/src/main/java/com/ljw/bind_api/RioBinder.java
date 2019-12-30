package com.ljw.bind_api;

import android.app.Activity;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class RioBinder {
    private  static final  String SUFFIX ="$ViewBinder";
    public static void bind(Activity target){
        Class<?>  clazz = target.getClass();
        String className =clazz.getName()+SUFFIX;
        try {
            Class<?>  binderClass = Class.forName("com.ljw.okserialport.LJWProtocolImp");
//            ViewBinder rioBind = (ViewBinder) binderClass.newInstance();
//            rioBind.bind(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
