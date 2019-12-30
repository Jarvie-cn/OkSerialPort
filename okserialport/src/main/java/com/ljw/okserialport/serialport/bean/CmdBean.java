package com.ljw.okserialport.serialport.bean;


import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.utils.CmdPack;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class CmdBean {

    private CmdPack cmdPack;
    private String key;
    private SendResultCallback sendResultCallback;
    private long time;

    public CmdBean(CmdPack cmdPack, SendResultCallback sendResultCallback, String key, long time) {
        this.cmdPack = cmdPack;
        this.time = time;
        this.sendResultCallback = sendResultCallback;
        this.key = key;
    }

    public SendResultCallback getSendResultCallback() {
        return sendResultCallback;
    }

    public void setSendResultCallback(SendResultCallback sendResultCallback) {
        this.sendResultCallback = sendResultCallback;
    }

    public CmdPack getCmdPack() {
        return cmdPack;
    }

    public void setCmdPack(CmdPack cmdPack) {
        this.cmdPack = cmdPack;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
