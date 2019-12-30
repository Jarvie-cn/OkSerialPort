package com.ljw.okserialport.serialport.bean;


import com.ljw.okserialport.serialport.utils.ByteUtil;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :命令包
 */
public class DataPack {
    private byte[] allPackData;
    private byte[] data;
    private byte[] command;

    @Override
    public String toString() {
        return "DataPack{" +
                "全部命令=" + ByteUtil.bytes2HexStr(allPackData) +
                ", 数据集=" + ByteUtil.bytes2HexStr(data) +
                ", 收到的命令码=" + ByteUtil.bytes2HexStr(command) +
                '}';
    }

    public DataPack(byte[] allPackData, byte[] data, byte[] command) {
        this.allPackData = allPackData;
        this.data = data;
        this.command = command;
    }

    public byte[] getAllPackData() {
        return allPackData;
    }

    public void setAllPackData(byte[] allPackData) {
        this.allPackData = allPackData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }
}
