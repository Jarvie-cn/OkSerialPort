package com.ljw.okserialport.serialport.core;


import android.serialport.SerialPort;
import android.text.TextUtils;


import com.ljw.okserialport.serialport.bean.InitSerialPortBean;
import com.ljw.okserialport.serialport.callback.BaseDataCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.proxy.AsyncServicesProxy;
import com.ljw.okserialport.serialport.proxy.SyncServicesProxy;
import com.ljw.okserialport.serialport.utils.ApiException;
import com.ljw.okserialport.serialport.utils.ApiExceptionCode;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.utils.ServiceType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class SerialPortSingletonMgr {

    private volatile static SerialPortSingletonMgr instance;

    public static SerialPortSingletonMgr get() {
        if (instance == null) {
            synchronized (SerialPortSingletonMgr.class) {
                if (instance == null) {
                    instance = new SerialPortSingletonMgr();
                }
            }
        }
        return instance;
    }
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private BufferedInputStream mBufferedInputStream;
    private int mSerialPortType;
    private SyncServicesProxy mSyncServicesProxy;
    private AsyncServicesProxy mAsyncServicesProxy;


    public void init(InitSerialPortBean initSerialPortBean, BaseDataCallback baseDataCallback, SerialportConnectCallback connectCallback) {
        if (initSerialPortBean == null) {
            throw new ApiException(ApiExceptionCode.SERIAL_PORT_ERROR, "initSerialPortBean不为null");
        }
        if (TextUtils.isEmpty(initSerialPortBean.getDeviceAddress())) {
            throw new ApiException(ApiExceptionCode.SERIAL_PORT_ERROR, "串口地址不为null");
        }
        if (initSerialPortBean.getBaudRate() == 0) {
            throw new ApiException(ApiExceptionCode.SERIAL_PORT_ERROR, "波特率不为0");
        }
        if (baseDataCallback == null) {
            throw new ApiException(ApiExceptionCode.SERIAL_PORT_ERROR, "回调不可以为空,校验方法必须实现");
        }
        closeSerialPort();
        try {
            File device = new File(initSerialPortBean.getDeviceAddress());
            mSerialPort = new SerialPort(device, initSerialPortBean.getBaudRate(), 0);
            mOutputStream = mSerialPort.getOutputStream();
            if (mSerialPort.getInputStream() != null) {
                mBufferedInputStream = new BufferedInputStream(mSerialPort.getInputStream());
            }
            if (connectCallback != null) {
                connectCallback.onOpenSerialPortSuccess();
            }
            mSerialPortType = initSerialPortBean.getServiceType();
            if (mSerialPortType == ServiceType.SyncServices) {
                mSyncServicesProxy = new SyncServicesProxy(mOutputStream, mBufferedInputStream, baseDataCallback);
            } else if (mSerialPortType == ServiceType.AsyncServices) {
                mAsyncServicesProxy = new AsyncServicesProxy(initSerialPortBean.isOpenHeart(), initSerialPortBean.getHeartCommand(), mOutputStream, mBufferedInputStream, baseDataCallback);
            }
        } catch (Exception e) {
            if (connectCallback != null) {
                connectCallback.onError( new ApiException(ApiExceptionCode.SERIAL_PORT_ERROR, "串口打开异常:" + e.toString()));
            }
            mSerialPort = null;
        }
    }

    private void startTask() {
        if (mSerialPortType == ServiceType.SyncServices) {
            if (mSyncServicesProxy != null) {
                mSyncServicesProxy.stopTaskQueue();
            } else {
            }
        } else if (mSerialPortType == ServiceType.AsyncServices) {
            if (mAsyncServicesProxy != null) {
                mAsyncServicesProxy.stopTask();
            } else {
//                LogPlus.e("mSyncServicesProxy==null,串口初始化异常");
            }
        }
    }

    /**
     * 停止当前任务,释放线程
     */
    public void stopTask() {
        if (mSerialPortType == ServiceType.SyncServices) {
            if (mSyncServicesProxy != null) {
                mSyncServicesProxy.stopTaskQueue();
            } else {
//                LogPlus.e("mSyncServicesProxy==null,串口初始化异常");
            }
        } else if (mSerialPortType == ServiceType.AsyncServices) {
            if (mAsyncServicesProxy != null) {
                mAsyncServicesProxy.stopTask();
            } else {
//                LogPlus.e("mSyncServicesProxy==null,串口初始化异常");
            }
        }
    }

    public void send(CmdPack cmdPack, SendResultCallback sendResultCallback) {
        if (mSerialPort == null) {
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "串口打开失败!",cmdPack.getDestinationAddress()));
            }
            return;
        }

        if (mOutputStream == null) {
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "串口获取,OutputStream为null",cmdPack.getDestinationAddress()));
            }
            return;
        }
        if (mBufferedInputStream == null) {
            if (sendResultCallback != null) {
                sendResultCallback.onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "串口获取,InputStream为null:",cmdPack.getDestinationAddress()));
                return;
            }
        }
        if (mSerialPortType == ServiceType.SyncServices) {
            if (mSyncServicesProxy != null) {
                mSyncServicesProxy.send(cmdPack, sendResultCallback);
            }
        } else if (mSerialPortType == ServiceType.AsyncServices) {
            if (mAsyncServicesProxy != null) {
                mAsyncServicesProxy.send(cmdPack, sendResultCallback);
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (mSerialPort != null) {
            try {
                if (mSerialPort.getOutputStream() != null) {
                    mSerialPort.getOutputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (mSerialPort.getInputStream() != null) {
                    mSerialPort.getInputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mBufferedInputStream != null) {
            try {
                mBufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSyncServicesProxy != null) {
            mSyncServicesProxy.close();
            mSyncServicesProxy = null;
        }
        if (mAsyncServicesProxy != null) {
            mAsyncServicesProxy.close();
            mAsyncServicesProxy = null;
        }
    }
}
