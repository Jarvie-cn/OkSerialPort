package com.ljw.okserialport.serialport.bean;


import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.core.OkSerialPort_ProtocolManager;
import com.ljw.okserialport.serialport.utils.ServiceType;

import java.util.ArrayList;
import java.util.List;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class SerialPortParams {

    private int serviceType = ServiceType.AsyncServices;
    private String serviceName = "async-serial_services-thread";
    private String deviceAddress = "/dev/ttyS0";
    private int baudRate = 115200;
    private boolean isOpenHeart;
    private boolean isReconnect;
    private List<byte[]> heartCommand = new ArrayList<>();

    private SerialportConnectCallback callback;


    public SerialportConnectCallback getCallback() {
        return callback;
    }

    public void setCallback(SerialportConnectCallback callback) {
        this.callback = callback;
    }

    private SerialPortParams() {
    }

    public int getServiceType() {
        return serviceType;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public void setReconnect(boolean reconnect) {
        isReconnect = reconnect;
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

    public static class Builder {
        private SerialPortParams serialPortBean;

        public Builder() {
            serialPortBean = new SerialPortParams();
        }

        public Builder addDeviceAddress(String address) {
            serialPortBean.setDeviceAddress(address);
            return this;
        }

        public Builder addBaudRate(int baudRate) {
            serialPortBean.setBaudRate(baudRate);
            return this;
        }

        public Builder addHeartCommands(List<byte[]> heartCommands) {
            serialPortBean.setHeartCommand(heartCommands);
            return this;
        }

        public Builder callback(SerialportConnectCallback callback) {
            serialPortBean.setCallback(callback);
            return this;
        }

        public Builder isReconnect(boolean isReconnect) {
            serialPortBean.setReconnect(isReconnect);
            return this;
        }

        public SerialPortParams build() {
            if (serialPortBean.getHeartCommand() == null || serialPortBean.getHeartCommand().size() == 0) {
                serialPortBean.setHeartCommand(OkSerialPort_ProtocolManager.mHeartCommands);
            }
            if (serialPortBean.getHeartCommand().size() >0) {
                serialPortBean.setOpenHeart(true);
            }
            return serialPortBean;
        }
    }

}
