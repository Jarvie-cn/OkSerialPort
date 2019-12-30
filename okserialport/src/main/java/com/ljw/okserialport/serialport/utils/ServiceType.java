package com.ljw.okserialport.serialport.utils;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ljw.okserialport.serialport.utils.ServiceType.AsyncServices;
import static com.ljw.okserialport.serialport.utils.ServiceType.SyncServices;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
@IntDef({SyncServices, AsyncServices})
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceType {
    /**
     * 同步服务,适用于485
     */
    int SyncServices = 0;
    /**
     * 异步服务适用于485和232
     */
    int AsyncServices = 1;
}
