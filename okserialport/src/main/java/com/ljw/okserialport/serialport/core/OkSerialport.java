package com.ljw.okserialport.serialport.core;



import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.bean.InitSerialPortBean;
import com.ljw.okserialport.serialport.callback.AsyncDataCallback;
import com.ljw.okserialport.serialport.callback.DataPackCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.utils.ApiException;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.utils.OrderAssembleUtil;
import com.ljw.okserialport.serialport.utils.ResultDataParseUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :串口管理类
 */
public class OkSerialport {

    public int mSendOutTime = 10000;
    public int mWaitOutTime = 10000;


    private static OkSerialport instance;

    public static OkSerialport getInstance() {
        if (instance == null) {
            synchronized (OkSerialport.class) {
                if (instance == null) {
                    instance = new OkSerialport();
                }
            }
        }
        return instance;
    }

    SerialportConnectCallback mConnectCallback;

    /**
     * @param deviceAddress   串口地址
     * @param baudRate        波特率
     * @param connectCallback 连接结果回调
     */
    public void init(String deviceAddress, int baudRate,
                     SerialportConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        List<byte[]> heartCommand = new ArrayList<>();

        heartCommand = OkSerialPort_ProtocolManager.mHeartCommands;
        InitSerialPortBean initSerialPortBean =
                new InitSerialPortBean( deviceAddress, baudRate, heartCommand.size()>0?true:false,
                        heartCommand);
        initSerialPortBean.setServiceName("async-serial_services-thread");
        close();
        SerialPortSingletonMgr.get().init(initSerialPortBean, new AsyncDataCallback() {
            @Override
            public boolean checkData(byte[] received, int size, DataPackCallback dataPackCallback) {
                return ResultDataParseUtils.handleDataPack(byteBuffer, received, size, dataPackCallback);
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            @Override
            public void onActivelyReceivedCommand(DataPack dataPack) {

                handleHeatData(dataPack);
            }
        }, new SerialportConnectCallback() {
            @Override
            public void onError(ApiException apiException) {
                if (mConnectCallback != null) {
                    mConnectCallback.onError(apiException);
                }
            }

            @Override
            public void onOpenSerialPortSuccess() {
                if (mConnectCallback != null) {
                    mConnectCallback.onOpenSerialPortSuccess();
                }
            }

            @Override
            public void onHeatDataCallback(DataPack dataPack) {

            }
        });
    }

    public void reConnect(String deviceAddress, int baudRate) {
        init(deviceAddress, baudRate, mConnectCallback);
    }


    public void close() {
        SerialPortSingletonMgr.get().closeSerialPort();
    }

    private synchronized void handleHeatData(DataPack dataPack) {
        mConnectCallback.onHeatDataCallback(dataPack);

    }

    public void setmSendOutTime(int mSendOutTime) {
        this.mSendOutTime = mSendOutTime;
    }

    public void setmWaitOutTime(int mWaitOutTime) {
        this.mWaitOutTime = mWaitOutTime;
    }

    /**
     * @param data 数据
     * @param  sendCommand 命令
     * @param sendResultCallback 发送回调
     * */
    public void send(String data, byte[] sendCommand , SendResultCallback sendResultCallback) {
        byte[] checkCommand = sendCommand;
        byte[] cmd = OrderAssembleUtil.allCmd(checkCommand, data);
        List<byte[]> checkCommands = new ArrayList<>();
        checkCommands.add(checkCommand);
        final CmdPack cmdPack = new CmdPack(0, cmd, checkCommands);
        cmdPack.setSendOutTime(mSendOutTime);
        cmdPack.setWaitOutTime(mWaitOutTime);
        SerialPortSingletonMgr.get().send(cmdPack, sendResultCallback);
    }



}
