package com.ljw.okserialport.serialport.utils;


import android.os.SystemClock;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.callback.BaseDataCallback;
import com.ljw.okserialport.serialport.callback.DataPackCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class CmdTask extends BaseQueue {
    private SendResultCallback mSendResultCallback;
    private CmdPack mCmdPack;
    private BaseDataCallback mBaseDataCallback;
    private BufferedInputStream mBufferedInputStream;
    private OutputStream mOutputStream;

    public CmdTask(@Priority int priority, SendResultCallback mSendResultCallback, CmdPack mCmdPack, OutputStream outputStream,
                   BufferedInputStream bufferedInputStream, BaseDataCallback baseDataCallback) {
        this.mSendResultCallback = mSendResultCallback;
        this.mCmdPack = mCmdPack;
        mBufferedInputStream = bufferedInputStream;
        mOutputStream = outputStream;
        this.mBaseDataCallback = baseDataCallback;
        setPriority(priority);
    }

    boolean isRunning = false;

    @Override
    public void runTask() {
        boolean isSend = sendData();
        if (!isSend) {
            return;
        }
        isRunning = true;

        int size;
        // 记录一下发送后的时间，用来判断接收数据是否超时
        long sendTime = System.currentTimeMillis();
        long waitTime = 0;
        while (isRunning) {
            try {
                if (mBufferedInputStream.available() > 0) {
                    // 更新一下接收数据时间
                    waitTime = System.currentTimeMillis();
                    byte[] received = new byte[2048];
                    size = mBufferedInputStream.read(received);
                    mBaseDataCallback.checkData(received, size, new DataPackCallback() {
                        @Override
                        public void setDataPack(DataPack dataPack) {
                            if (dataPack != null) {
                                //命令码
                                if (mCmdPack.getCheckCommand() == null || mCmdPack.getCheckCommand().size() == 0) {
                                    if (mSendResultCallback != null) {
                                        mSendResultCallback.onFailed(
                                                new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "248的校验命令不能为空,请设置CmdPack中的checkCommand",
                                                        mCmdPack.getDestinationAddress()));
                                    }
                                    isRunning = false;
                                    return;
                                }
                                if (mCmdPack.getCheckCommand().size() > 1) {
                                    if (mSendResultCallback != null) {
                                        mSendResultCallback.onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR,
                                                "248的校验命令不能超过一个,checkCommand:" + mCmdPack.getCheckCommands(), mCmdPack.getDestinationAddress()));
                                    }
                                    isRunning = false;
                                    return;
                                }
                                String command = ByteUtil.bytes2HexStr(mCmdPack.getCheckCommand().get(0));
                                String readCommand = ByteUtil.bytes2HexStr(dataPack.getCommand());
                                if (readCommand.equalsIgnoreCase(command)) {
                                    if (mSendResultCallback != null) {
                                        mSendResultCallback.onSuccess(dataPack);
                                    }
                                } else {
                                    if (mSendResultCallback != null) {
                                        mSendResultCallback.onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR,
                                                "命令码不同,获取到结果为:" + dataPack.toString() + "--校验命令码:" + command, mCmdPack.getDestinationAddress()));
                                    }
                                }
                                isRunning = false;
                                return;
                            }
                        }
                    });
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
                // 检查释放超时
                boolean isTimeOut = isTimeOut(sendTime, waitTime);
                if (isTimeOut) {
                    if (mSendResultCallback != null) {
                        mSendResultCallback.onFailed(
                                new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_READ_OUT_TIME_ERROR, "读取超时", mCmdPack.getDestinationAddress()));
                    }
                    return;
                }
            } catch (IOException e) {
                if (mSendResultCallback != null) {
                    mSendResultCallback.onFailed(
                            new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, e.toString(), mCmdPack.getDestinationAddress()));
                }
                isRunning = false;
            }
        }
    }

    private boolean isTimeOut(long sendTime, long waitTime) {
        // 表示一直没收到数据
        if (waitTime == 0) {
            long sendOffset = Math.abs(System.currentTimeMillis() - sendTime);
            return sendOffset > mCmdPack.getSendOutTime();
        } else {
            // 有接收到过数据，但是距离上一个数据已经超时
            long waitOffset = Math.abs(System.currentTimeMillis() - waitTime);
            return waitOffset > mCmdPack.getWaitOutTime();
        }
    }

    private boolean sendData() {
        try {
            if (mSendResultCallback != null) {
                mSendResultCallback.onStart(mCmdPack);
            }
            SystemClock.sleep(100);
//            LogPlus.e("发送码:" + ByteUtil.bytes2HexStr(mCmdPack.getSendData()));
            mOutputStream.write(mCmdPack.getSendData());
        } catch (IOException e) {
            if (mSendResultCallback != null) {
                mSendResultCallback.onFailed(
                        new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "硬件错误:" + e.toString(), mCmdPack.getDestinationAddress()));
            }
            return false;
        }
        return true;
    }
}
