package com.ljw.okserialport.serialport.core;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.bean.SerialPortParams;
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

    public OkSerialport() {
    }

    public static OkSerialport getInstance() {
        if (instance == null) {
            Class var0 = OkSerialport.class;
            synchronized(OkSerialport.class) {
                if (instance == null) {
                    instance = new OkSerialport();
                }
            }
        }

        return instance;
    }

    public void open(final SerialPortParams initSerialPortBean){
        close();
        SerialPortSingletonMgr.get().init(initSerialPortBean, new AsyncDataCallback() {
            @Override
            public boolean checkData(byte[] received, int size, DataPackCallback dataPackCallback) {
                return ResultDataParseUtils.handleDataPack(byteBuffer, received, size, dataPackCallback);
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            @Override
            public void onActivelyReceivedCommand(DataPack dataPack) {
                if (initSerialPortBean.getCallback() != null) {
                    initSerialPortBean.getCallback().onHeatDataCallback(dataPack);
                }
            }
        }, new SerialportConnectCallback() {
            @Override
            public void onError(ApiException apiException) {

                if (initSerialPortBean.isReconnect()) {
                    open(initSerialPortBean);
                }
                if (initSerialPortBean.getCallback() != null) {
                    initSerialPortBean.getCallback().onError(apiException);
                }
            }

            @Override
            public void onOpenSerialPortSuccess() {
                if (initSerialPortBean.getCallback() != null) {
                    initSerialPortBean.getCallback().onOpenSerialPortSuccess();
                }
            }

            @Override
            public void onHeatDataCallback(DataPack dataPack) {

            }
        });
    }



    public void close() {
        SerialPortSingletonMgr.get().closeSerialPort();
    }


    public void setmSendOutTime(int mSendOutTime) {
        this.mSendOutTime = mSendOutTime;
    }

    public void setmWaitOutTime(int mWaitOutTime) {
        this.mWaitOutTime = mWaitOutTime;
    }

    /**
     * @param fillDatas          填充占位数据，没有则传null
     * @param data               数据
     * @param sendCommand        命令
     * @param sendResultCallback 发送回调
     */
    public void send(String[] fillDatas, String data, byte[] sendCommand, SendResultCallback sendResultCallback) {
        try {
            byte[] checkCommand = sendCommand;
            byte[] cmd = OrderAssembleUtil.allCmd(checkCommand, fillDatas, data);
            List<byte[]> checkCommands = new ArrayList<>();
            checkCommands.add(checkCommand);
            final CmdPack cmdPack = new CmdPack(0, cmd, checkCommands);
            cmdPack.setSendOutTime(mSendOutTime);
            cmdPack.setWaitOutTime(mWaitOutTime);
            SerialPortSingletonMgr.get().send(cmdPack, sendResultCallback);
        } catch (Exception e) {
            e.printStackTrace();
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException("-1", "发送失败：" + e.getMessage(), 1));
            }
        }
    }

    /**
     * 心跳回答
     *
     * @param fillDatas          填充占位数据，没有则传null
     * @param data               数据
     * @param sendCommand        命令
     * @param sendResultCallback 发送回调
     */
    public void heartBeatReply(String[] fillDatas, String data, byte[] sendCommand, SendResultCallback sendResultCallback) {
        try {
            byte[] checkCommand = sendCommand;
            byte[] cmd = OrderAssembleUtil.allCmd(checkCommand, fillDatas, data);
            List<byte[]> checkCommands = new ArrayList<>();
            checkCommands.add(checkCommand);
            final CmdPack cmdPack = new CmdPack(0, cmd, checkCommands);
            cmdPack.setSendOutTime(1000);
            cmdPack.setWaitOutTime(50);
            SerialPortSingletonMgr.get().send(cmdPack, sendResultCallback);
        } catch (Exception e) {
            e.printStackTrace();
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException("-1", "发送失败：" + e.getMessage(), 1));
            }
        }
    }





}
