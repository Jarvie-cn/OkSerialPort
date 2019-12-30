package com.ljw.okserialport.serialport.utils;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ljw.okserialport.serialport.utils.ReadType.CheckSendData;
import static com.ljw.okserialport.serialport.utils.ReadType.ReadDataSuccess;


@IntDef({CheckSendData, ReadDataSuccess})
@Retention(RetentionPolicy.SOURCE)
public @interface ReadType {
    /**
     * 检测发送数据包的回调
     */
    int CheckSendData = 0;
    /**
     * 读取数据成功
     */
    int ReadDataSuccess = 1;


}
