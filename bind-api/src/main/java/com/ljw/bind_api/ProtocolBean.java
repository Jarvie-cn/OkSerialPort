package com.ljw.bind_api;

/**
 * @author : LJW
 * @date : 2019/12/27
 * @desc :
 */
public class ProtocolBean {
    /**
     * 位置
     * */
    public int index;
    /**
     * 字节长度
     * */
    public int length;

    /**
     * 字节长度
     * */
    public byte value;

    public ProtocolBean(int index, int length, byte value) {
        this.index = index;
        this.length = length;
        this.value = value;
    }
}
