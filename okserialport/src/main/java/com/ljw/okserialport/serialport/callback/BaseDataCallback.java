package com.ljw.okserialport.serialport.callback;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public interface BaseDataCallback {
    /**
     * 校验数据包
     *
     * @param received 接收数据
     * @param size     数据大小
     * @return 返回整理好的数据包
     */
    boolean checkData(byte[] received, int size, DataPackCallback dataPackCallback);
}
