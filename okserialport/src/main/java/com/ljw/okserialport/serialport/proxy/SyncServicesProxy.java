package com.ljw.okserialport.serialport.proxy;


import com.ljw.okserialport.serialport.callback.BaseDataCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.utils.AbstractTaskQueue;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.utils.CmdTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class SyncServicesProxy {
    private AbstractTaskQueue mAbstractTaskQueue;
    private boolean isStart = false;
    private BaseDataCallback mBaseDataCallback;
    private OutputStream mOutputStream;
    private BufferedInputStream mBufferedInputStream;

    public SyncServicesProxy(OutputStream outputStream, BufferedInputStream bufferedInputStream, BaseDataCallback baseDataCallback) {
        mBaseDataCallback = baseDataCallback;
        mOutputStream = outputStream;
        mBufferedInputStream = bufferedInputStream;
        startTaskQueue();
    }

    private void startTaskQueue() {
        if (mAbstractTaskQueue == null) {
            mAbstractTaskQueue = new AbstractTaskQueue(1);
        }
        if (!isStart) {
            mAbstractTaskQueue.start();
            isStart = true;
        }
    }

    /**
     * 停止当前任务,释放线程
     */
    public void stopTaskQueue() {
        if (mAbstractTaskQueue != null && isStart) {
            mAbstractTaskQueue.stop();
            isStart = false;
        }
    }

    public void send(CmdPack cmdPack, SendResultCallback sendResultCallback) {
        CmdTask cmdTask = new CmdTask(cmdPack.getPriority(), sendResultCallback, cmdPack, mOutputStream, mBufferedInputStream, mBaseDataCallback);
        if (mAbstractTaskQueue == null) {
            startTaskQueue();
        }
        //加入等待任务里面
        mAbstractTaskQueue.add(cmdTask);
    }

    /**
     * 关闭
     */
    public void close() {
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
    }
}
