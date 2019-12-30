package com.ljw.okserialport.serialport.bean;





import com.ljw.okserialport.serialport.utils.ServiceType;

import java.util.List;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class InitSerialPortBean {

    private int serviceType;
    private String serviceName;
    private String deviceAddress;
    private int baudRate;
    private boolean isOpenHeart;
    private List<byte[]> heartCommand;


    public InitSerialPortBean() {
    }

    /**
     * @param deviceAddress 设备地址
     * @param baudRate      波特率
     * @param isOpenHeart   是否打开心跳
     * @param heartCommand  心跳命令
     */
    public InitSerialPortBean( String deviceAddress, int baudRate, boolean isOpenHeart, List<byte[]> heartCommand) {
        this.serviceType = ServiceType.AsyncServices;
        this.deviceAddress = deviceAddress;
        this.baudRate = baudRate;
        this.isOpenHeart = isOpenHeart;
        this.heartCommand = heartCommand;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public boolean isOpenHeart() {
        return isOpenHeart;
    }

    public void setOpenHeart(boolean openHeart) {
        isOpenHeart = openHeart;
    }

    public List<byte[]> getHeartCommand() {
        return heartCommand;
    }

    public void setHeartCommand(List<byte[]> heartCommand) {
        this.heartCommand = heartCommand;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


}
