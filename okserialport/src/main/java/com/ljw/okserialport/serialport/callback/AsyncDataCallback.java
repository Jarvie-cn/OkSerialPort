package com.ljw.okserialport.serialport.callback;


import com.ljw.okserialport.serialport.bean.DataPack;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public interface AsyncDataCallback extends BaseDataCallback {


    /**
     * 主动收到回调(比如心跳包回调)
     */
    void onActivelyReceivedCommand(DataPack dataPack);
}
