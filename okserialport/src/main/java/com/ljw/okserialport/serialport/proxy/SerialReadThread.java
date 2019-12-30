package com.ljw.okserialport.serialport.proxy;

import android.os.SystemClock;
import android.text.TextUtils;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.callback.AsyncDataCallback;
import com.ljw.okserialport.serialport.callback.BaseDataCallback;
import com.ljw.okserialport.serialport.callback.DataPackCallback;
import com.ljw.okserialport.serialport.callback.SerialReadCallback;
import com.ljw.okserialport.serialport.utils.ByteUtil;
import com.ljw.okserialport.serialport.utils.LJWLogUtils;
import com.ljw.okserialport.serialport.utils.ReadMessage;
import com.ljw.okserialport.serialport.utils.ReadType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;


/**
 * 读串口线程
 */
public class SerialReadThread extends Thread {


    private BufferedInputStream mBufferedInputStream;
    private BaseDataCallback mBaseDataCallback;
    private SerialReadCallback mSerialReadCallback;
    private boolean isActivelyReceivedCommand;
    private List<byte[]> mHeartCommand;
    /**
     * 记录一下发送后的时间，用来判断接收数据是否超时
     */
    private long checkSendDataTime = System.currentTimeMillis();
    private long errorTime = System.currentTimeMillis();


    public SerialReadThread(boolean isActivelyReceivedCommand, List<byte[]> heartCommand, BufferedInputStream bufferedInputStream,
                            BaseDataCallback baseDataCallback, SerialReadCallback serialReadCallback) {
        mBufferedInputStream = bufferedInputStream;
        mBaseDataCallback = baseDataCallback;
        mSerialReadCallback = serialReadCallback;
        mHeartCommand = heartCommand;
        this.isActivelyReceivedCommand = isActivelyReceivedCommand;
    }

    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;


        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                int available = mBufferedInputStream.available();
                boolean isSend = false;
                if (available > 0) {
                    size = mBufferedInputStream.read(received);
                    if (size > 0) {
                        isSend = onDataReceive(received, size);
                        // 暂停一点时间，免得处理数据太快
                        SystemClock.sleep(1);
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
                if (!isSend && isTimeOut()) {
                    if (mSerialReadCallback != null) {
                        mSerialReadCallback.onReadMessage(new ReadMessage(ReadType.CheckSendData, "CheckSendData", null));
                    }
                }
            } catch (IOException e) {
                if (isErrorTimeOut()) {
                    if (mSerialReadCallback != null) {
                        mSerialReadCallback.onReadMessage(new ReadMessage(ReadType.CheckSendData, "ReadDataError:" +
                                "读取数据失败:" + e.toString(), null));
                    }
                }
            }
        }

    }

    /**
     * 处理获取到的数据
     *
     * @param received
     * @param size
     */
    private boolean onDataReceive(byte[] received, int size) {
        return mBaseDataCallback.checkData(received, size, new DataPackCallback() {
            @Override
            public void setDataPack(DataPack dataPack) {
                if (dataPack != null) {
//                    LogPlus.e("接收数据:" + ByteUtil.bytes2HexStr(dataPack.getAllPackData()));
                    if (isActivelyReceivedCommand && isActivelyReceivedCommand(dataPack)) {
                        if (mBaseDataCallback instanceof AsyncDataCallback) {
//                            LogPlus.e("心跳数据");
                            LJWLogUtils.e("心跳数据");
                            ((AsyncDataCallback) mBaseDataCallback).onActivelyReceivedCommand(dataPack);
                        }
                        return;
                    }

                    //不是心跳数据
                    if (mSerialReadCallback != null) {
                        LJWLogUtils.e("不是心跳数据");
                        mSerialReadCallback.onReadMessage(new ReadMessage(ReadType.ReadDataSuccess, "ReadDataSuccess", dataPack));
                    }

                }
            }
        });

    }

    /**
     * 是否是主动收到命令
     *
     * @return
     */
    private boolean isActivelyReceivedCommand(DataPack dataPack) {
        if (mHeartCommand != null && mHeartCommand.size() > 0) {
            for (byte[] bytes : mHeartCommand) {
                String command = ByteUtil.bytes2HexStr(bytes);
                String readCommand = ByteUtil.bytes2HexStr(dataPack.getCommand());
                if (TextUtils.equals(command, readCommand)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTimeOut() {
        // 在规定时间内校验
        if (Math.abs(System.currentTimeMillis() - checkSendDataTime) > 100) {
            checkSendDataTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private boolean isErrorTimeOut() {
        // 在规定时间内校验
        if (Math.abs(System.currentTimeMillis() - errorTime) > 1000) {
            errorTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * 停止读线程
     */
    public void close() {
        try {
            mBufferedInputStream.close();
        } catch (IOException e) {
            LJWLogUtils.e("异常:" + e.toString());
        } finally {
            super.interrupt();
        }
    }
}
