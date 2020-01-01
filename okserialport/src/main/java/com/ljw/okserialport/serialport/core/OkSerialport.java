package com.ljw.okserialport.serialport.core;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.bean.InitSerialPortBean;
import com.ljw.okserialport.serialport.callback.AsyncDataCallback;
import com.ljw.okserialport.serialport.callback.DataPackCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.utils.ApiException;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
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

    public int mSendOutTime = 7000;
    public int mWaitOutTime = 7000;


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
                new InitSerialPortBean(deviceAddress, baudRate, heartCommand.size() > 0 ? true : false,
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

    /**
     * @param deviceAddress   串口地址
     * @param baudRate        波特率
     * @param heartCommands   心跳命令数据
     * @param connectCallback 连接结果回调
     */
    public void init(String deviceAddress, int baudRate, List<byte[]> heartCommands,
                     SerialportConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        List<byte[]> heartCommand = new ArrayList<>();
        if (heartCommands != null && heartCommands.size() > 0) {
            heartCommand = heartCommands;
        } else {
            heartCommand = OkSerialPort_ProtocolManager.mHeartCommands;
        }
        InitSerialPortBean initSerialPortBean =
                new InitSerialPortBean(deviceAddress, baudRate, heartCommand.size() > 0 ? true : false,
                        heartCommand);
        initSerialPortBean.setServiceName("async-serial_services-thread");
        close();
        SerialPortSingletonMgr.get().init(initSerialPortBean, new AsyncDataCallback() {
            @Override
            public boolean checkData(byte[] received, int size, DataPackCallback dataPackCallback) {
                return ResultDataParseUtils.handleDataPack(byteBuffer, received, size, dataPackCallback);
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(5024);

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


    public void reConnect(String deviceAddress, int baudRate, List<byte[]> heartCommands) {
        init(deviceAddress, baudRate, heartCommands, mConnectCallback);
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
     * @param fillDatas           填充占位数据，没有则传null
     * @param data               数据
     * @param sendCommand        命令
     * @param sendResultCallback 发送回调
     */
    public void send(String[] fillDatas,String data, byte[] sendCommand, SendResultCallback sendResultCallback) {
        try {
            byte[] checkCommand = sendCommand;
            byte[] cmd = new byte[0];
            cmd = OrderAssembleUtil.allCmd(checkCommand,fillDatas, data);
            List<byte[]> checkCommands = new ArrayList<>();
            checkCommands.add(checkCommand);
            final CmdPack cmdPack = new CmdPack(0, cmd, checkCommands);
            cmdPack.setSendOutTime(mSendOutTime);
            cmdPack.setWaitOutTime(mWaitOutTime);
            SerialPortSingletonMgr.get().send(cmdPack, sendResultCallback);
        } catch (Exception e) {
            e.printStackTrace();
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException("-1","发送失败："+e.getMessage(),1));
            }
        }
    }


}
