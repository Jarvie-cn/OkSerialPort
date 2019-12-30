package com.ljw.okserialport.serialport.proxy;


import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;

import com.ljw.okserialport.serialport.bean.CmdBean;
import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.callback.BaseDataCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.callback.SerialReadCallback;
import com.ljw.okserialport.serialport.utils.ApiExceptionCode;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.ByteUtil;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.utils.LJWLogUtils;
import com.ljw.okserialport.serialport.utils.ReadMessage;
import com.ljw.okserialport.serialport.utils.ReadType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class AsyncServicesProxy {
    private boolean isStart = false;
    private BaseDataCallback mBaseDataCallback;
    private OutputStream mOutputStream;
    private BufferedInputStream mBufferedInputStream;
    private SerialReadThread mReadThread;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;
    private boolean isActivelyReceivedCommand;
    private List<byte[]> mHeartCommand;
    private List<CmdBean> mList = new ArrayList<>();

    public AsyncServicesProxy(String name, boolean isActivelyReceivedCommand, List<byte[]> heartCommand, OutputStream outputStream,
                              BufferedInputStream bufferedInputStream, BaseDataCallback baseDataCallback) {
        mBaseDataCallback = baseDataCallback;
        mOutputStream = outputStream;
        mBufferedInputStream = bufferedInputStream;
        mHeartCommand = heartCommand;
        this.isActivelyReceivedCommand = isActivelyReceivedCommand;
        startTask(name);
    }

    public AsyncServicesProxy(boolean isActivelyReceivedCommand, List<byte[]> heartCommand, OutputStream outputStream,
                              BufferedInputStream bufferedInputStream, BaseDataCallback baseDataCallback) {
        mBaseDataCallback = baseDataCallback;
        mOutputStream = outputStream;
        mBufferedInputStream = bufferedInputStream;
        mHeartCommand = heartCommand;
        this.isActivelyReceivedCommand = isActivelyReceivedCommand;
        LJWLogUtils.e( "是否开启了心跳：" + isActivelyReceivedCommand );
        startTask("");
    }

    public void startTask(String name) {
        if (mReadThread == null) {
            mReadThread =
                    new SerialReadThread(isActivelyReceivedCommand, mHeartCommand, mBufferedInputStream, mBaseDataCallback, new SerialReadCallback() {
                        @Override
                        public void onReadMessage(ReadMessage readMessage) {
                            //                    handleReadMessage(readMessage);
                            onRead(readMessage);
                        }
                    });
        }
        if (mWriteThread == null) {
            if (TextUtils.isEmpty(name)) {
                name = "async-services-thread";
            }
            mWriteThread = new HandlerThread(name);
        }
        mWriteThread.start();
        if (mSendScheduler == null) {
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());
        }
        if (!isStart) {
            mReadThread.start();
            isStart = true;
        }
    }

    /**
     * 停止当前任务,释放线程
     */
    public void stopTask() {
        if (isStart) {
            if (mReadThread != null) {
                mReadThread.close();
            }
            if (mWriteThread != null) {
                mWriteThread.quit();
            }
            isStart = false;
        }
    }

    private Disposable mDisposable, mReadDisposable;

    public void send(CmdPack cmdPack, SendResultCallback sendResultCallback) {

        long time = System.currentTimeMillis();
//        LogPlus.e("cmdPackTime=" + time + ",cmd=" + cmdPack.getDestinationAddress());
//        LogPlus.e("list 添加任务：" + cmdPack.getStrSendData() + "list.size():" + mList.size());

        if (mCmdBean != null) {
            synchronized (mList) {
                mList.add(new CmdBean(cmdPack, sendResultCallback, time + "", 0));
            }
            checkSendData();
            return;
        }
        mCmdBean = new CmdBean(cmdPack, sendResultCallback, time + "", 0);
        synchronized (mList) {
            mList.add(mCmdBean);
        }
        dispose();
        rxSendData().subscribeOn(mSendScheduler).subscribe(new Observer<CmdBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                //SerialPortLoggerFactory.info("发送数据+Disposable:" + d.isDisposed());
            }

            @Override
            public void onNext(CmdBean cmdBean) {
                //mCmdBean = cmdBean;
//                LogPlus.e("发送数据:" + ByteUtil.bytes2HexStr(cmdBean.getCmdPack().getSendData()));
            }

            @Override
            public void onError(Throwable e) {
                //                Log.e("发送失败", e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private CmdBean mCmdBean;

    /**
     * (rx包裹)发送数据
     *
     * @return
     */
    private Observable<CmdBean> rxSendData() {
        return Observable.create(new ObservableOnSubscribe<CmdBean>() {
            @Override
            public void subscribe(ObservableEmitter<CmdBean> emitter) throws Exception {
                try {
                    if (mCmdBean != null) {
                        if (mCmdBean.getSendResultCallback() != null) {
                            mCmdBean.getSendResultCallback().onStart(mCmdBean.getCmdPack());
                        }
//                        LogPlus.e("cmdPackTime=" + ",cmd=" + mCmdBean.getCmdPack().getDestinationAddress());
                        SystemClock.sleep(100);
                        mOutputStream.write(mCmdBean.getCmdPack().getSendData());
                        long time = System.currentTimeMillis();
                        mCmdBean.setTime(time);
//                        LogPlus.e("当前执行的任务：" + mCmdBean.getCmdPack().getStrSendData());
                        emitter.onNext(mCmdBean);
                        if (mCmdBean.getCmdPack().getCheckCommand() == null || mCmdBean.getCmdPack().getCheckCommand().size() == 0) {
                            mCmdBean = null;
                            removeCmdBean();
                            nextSend();
                        }
                    }
                } catch (Exception e) {
                    if (mCmdBean != null) {
//                        LogPlus.e("发送：" + ByteUtil.bytes2HexStr(mCmdBean.getCmdPack().getSendData()) + " 失败," + e.toString());
                        if (mCmdBean.getSendResultCallback() != null) {
                            mCmdBean.getSendResultCallback()
                                    .onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_ERROR, "硬件错误:" + e.toString(),
                                            mCmdBean.getCmdPack().getDestinationAddress()));
                        }
                        removeCmdBean();
                        mCmdBean = null;
                        nextSend();
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }

    /**
     * (rx包裹)发送数据
     *
     * @return
     */
    private Observable<ReadMessage> rxReadData(final ReadMessage readMessage) {
        return Observable.create(new ObservableOnSubscribe<ReadMessage>() {
            @Override
            public void subscribe(ObservableEmitter<ReadMessage> emitter) throws Exception {
                emitter.onNext(readMessage);
                emitter.onComplete();
            }
        });
    }

    private void removeCmdBean() {
        synchronized (mList) {
            if (mList.size() > 0) {
//                LogPlus.e("removeCmdBean :list.size:" + mList.size());
                mList.remove(0);
            }
        }
    }

    /**
     * 下一个数据发送
     */
    private synchronized void nextSend() {
        if (mList.size() == 0) {
            return;
        }
        //防止并发问题
        if (mCmdBean != null) {
//            LogPlus.e("防止并发问题,直接return");
            return;
        }
        mCmdBean = mList.get(0);
        dispose();
        rxSendData().subscribeOn(mSendScheduler).subscribe(new Observer<CmdBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                //  SerialPortLoggerFactory.info("发送数据+Disposable:" + d.isDisposed());
            }

            @Override
            public void onNext(CmdBean cmdBean) {
//                LogPlus.e("发送码:" + ByteUtil.bytes2HexStr(cmdBean.getCmdPack().getSendData()));
            }

            @Override
            public void onError(Throwable e) {
                //                Log.e("发送失败", e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 处理数据
     */
    private synchronized void handleReadMessage(ReadMessage readMessage) {
        if (readMessage == null) {
            return;
        }
        disposeRead();
        rxReadData(readMessage).subscribeOn(mSendScheduler).subscribe(new Observer<ReadMessage>() {
            @Override
            public void onSubscribe(Disposable d) {
                mReadDisposable = d;
                //  SerialPortLoggerFactory.info("发送数据+Disposable:" + d.isDisposed());
            }

            @Override
            public void onNext(ReadMessage cmdBean) {
                onRead(cmdBean);
            }

            @Override
            public void onError(Throwable e) {
                //                Log.e("发送失败", e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void dispose() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
//            LogPlus.e("发送数据+Disposable释放");
        }
        mDisposable = null;
    }

    private void disposeRead() {
        if (mReadDisposable != null && !mReadDisposable.isDisposed()) {
            mReadDisposable.dispose();
//            LogPlus.e("接收数据+Disposable释放");
        }
        mReadDisposable = null;
    }

    /**
     * 关闭串口
     */
    public void close() {
        dispose();
        disposeRead();
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
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mWriteThread != null) {
            mWriteThread.quit();
        }
    }

    public void onRead(ReadMessage message) {

        if (message != null) {
            switch (message.getReadType()) {
                case ReadType.CheckSendData:
                    checkSendData();
                    break;
                case ReadType.ReadDataSuccess:
                    if (message.getDataPack() != null) {
                        checkReadDataSuccess(message.getDataPack());
                    }
                    break;
                default:
            }
        }
    }

    /**
     * 校验成功数据
     */
    private void checkReadDataSuccess(DataPack dataPack) {
        if (mCmdBean == null) {
            nextSend();
            return;
        }
        String readCommand = ByteUtil.bytes2HexStr(dataPack.getCommand());
        if (mCmdBean != null) {
            boolean isRemove = false;
            boolean isOnSuccess = false;
            List<byte[]> list = null;
            if (mCmdBean.getCmdPack() != null && mCmdBean.getCmdPack().getCheckCommand() != null) {
                list = mCmdBean.getCmdPack().getCheckCommand();
                for (int i = 0; i < list.size(); i++) {
                    String command = ByteUtil.bytes2HexStr(list.get(i));
                    if (TextUtils.equals(command, readCommand)) {
                        isOnSuccess = true;
                        if (i == list.size() - 1) {
                            isRemove = true;
                        }
                        break;
                    }
                    if (i == list.size() - 1) {
                        isRemove = true;
                    }
                }
            }
            if (isOnSuccess) {
                if (mCmdBean.getSendResultCallback() != null) {
                    mCmdBean.getSendResultCallback().onSuccess(dataPack);
                }
            }
            if (isRemove) {
                removeCmdBean();
                mCmdBean = null;
                nextSend();
            }
        }
    }

    /**
     * 校验发送数据
     */
    private void checkSendData() {
        if (mCmdBean == null) {
            nextSend();
            return;
        }
        if (mCmdBean.getTime() == 0) {
            return;
        }
        if (Math.abs(System.currentTimeMillis() - mCmdBean.getTime()) >= mCmdBean.getCmdPack().getSendOutTime()) {
            if (mCmdBean.getSendResultCallback() != null) {
                mCmdBean.getSendResultCallback()
                        .onFailed(new BaseSerialPortException(ApiExceptionCode.SERIAL_PORT_READ_OUT_TIME_ERROR, "读取超时",
                                mCmdBean.getCmdPack().getDestinationAddress()));
            }
            mCmdBean = null;
            removeCmdBean();
            nextSend();
        }
    }
}
