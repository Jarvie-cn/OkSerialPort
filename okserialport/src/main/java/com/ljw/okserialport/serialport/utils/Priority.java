package com.ljw.okserialport.serialport.utils;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ljw.okserialport.serialport.utils.Priority.DEFAULT;
import static com.ljw.okserialport.serialport.utils.Priority.HIGH;
import static com.ljw.okserialport.serialport.utils.Priority.IMMEDTATELY;
import static com.ljw.okserialport.serialport.utils.Priority.LOW;


/**
 * @author :      fangbingran
 * @aescription : 等级
 * @date :        2019/06/05  19:34
 */
@IntDef({LOW, DEFAULT, HIGH, IMMEDTATELY})
@Retention(RetentionPolicy.SOURCE)
public @interface Priority {
    /**
     * 最低等级
     */
    int LOW = 0;
    /**
     * 默认等级
     */
    int DEFAULT = 1;
    /**
     * 高等级
     */
    int HIGH = 2;
    /**
     * 立即执行
     */
    int IMMEDTATELY = 3;
}
