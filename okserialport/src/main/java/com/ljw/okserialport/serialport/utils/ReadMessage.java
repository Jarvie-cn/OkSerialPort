package com.ljw.okserialport.serialport.utils;


import com.ljw.okserialport.serialport.bean.DataPack;

/**
 * 收到的日志
 */

public class ReadMessage {

    private String message;
    private int readType = -1;
    private DataPack dataPack;

    public ReadMessage(@ReadType int readType, String message, DataPack dataPack) {
        this.readType = readType;
        this.message = message;
        this.dataPack = dataPack;
    }

    public String getMessage() {
        return message;
    }


    public int getReadType() {
        return readType;
    }

    public void setReadType(@ReadType int readType) {
        this.readType = readType;
    }

    public DataPack getDataPack() {
        return dataPack;
    }

    public void setDataPack(DataPack dataPack) {
        this.dataPack = dataPack;
    }
}
