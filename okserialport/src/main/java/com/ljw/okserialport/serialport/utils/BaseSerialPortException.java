package com.ljw.okserialport.serialport.utils;

public class BaseSerialPortException extends ApiException {
    /**
     * 目标地址
     */
    private int address;

    public BaseSerialPortException(String code, String message, int address) {
        super(code, message);
        this.address = address;
    }

    public BaseSerialPortException(String code, Throwable message, int address) {
        super(code, message);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}

