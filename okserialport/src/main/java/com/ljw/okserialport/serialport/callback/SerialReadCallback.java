package com.ljw.okserialport.serialport.callback;


import com.ljw.okserialport.serialport.utils.ReadMessage;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public interface SerialReadCallback {
    void onReadMessage(ReadMessage readMessage);
}
