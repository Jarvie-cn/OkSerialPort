package com.ljw.okserialport.serialport.callback;


import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.CmdPack;

/**
 * 接口回调
 *
 * @param <T>
 */
public interface CommonCallback<T> {
    void onStart(CmdPack cmdPack);

    void onSuccess(T t);

    void onFailed(BaseSerialPortException spException);
}
