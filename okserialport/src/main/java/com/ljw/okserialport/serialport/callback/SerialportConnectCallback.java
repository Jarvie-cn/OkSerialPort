package com.ljw.okserialport.serialport.callback;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.utils.ApiException;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :连接成功回调
 */
public interface SerialportConnectCallback {
    void onError(ApiException apiException);

    void onOpenSerialPortSuccess();

    /**心跳上传回调*/
    void onHeatDataCallback(DataPack dataPack);
}
