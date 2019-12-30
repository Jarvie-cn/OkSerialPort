package com.ljw.okserialport.serialport.utils;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public interface IQueue extends Comparable<IQueue> {
    /**
     * 开始工作
     */
    void runTask();

    /**
     * 设置等级
     */
    void setPriority(@Priority int priority);

    int getPriority();

    /**
     * 一个序列标记
     */
    void setSequence(int sequence);

    /**
     * 获取序列标记
     */
    int getSequence();


}
