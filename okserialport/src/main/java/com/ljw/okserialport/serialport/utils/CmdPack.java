package com.ljw.okserialport.serialport.utils;




import java.util.List;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class CmdPack {
    private byte[] sendData;
    /**
     * 校验结果的命令码
     */
    private List<byte[]> checkCommand;

    /**
     * 发送数据超时时间,默认3000ms秒
     */
    private int sendOutTime = 3000;
    /**
     * 等待下一条数据超时时间默认300ms
     */
    private int waitOutTime = 300;
    /**
     * 任务等级,485才需要设置任务等级
     */
    private int priority = Priority.DEFAULT;

    private int destinationAddress;

    public CmdPack(int destinationAddress, byte[] sendData, List<byte[]> checkCommand) {
        this.sendData = sendData;
        this.checkCommand = checkCommand;
        this.destinationAddress = destinationAddress;
    }

    public CmdPack(int destinationAddress, byte[] sendData, List<byte[]> checkCommand, @Priority int priority) {
        this.sendData = sendData;
        this.checkCommand = checkCommand;
        this.priority = priority;
        this.destinationAddress = destinationAddress;
    }

    public CmdPack(byte[] sendData, List<byte[]> checkCommand, int sendOutTime, int waitOutTime) {
        this.sendData = sendData;
        this.checkCommand = checkCommand;
        this.sendOutTime = sendOutTime;
        this.waitOutTime = waitOutTime;
    }

    public CmdPack(byte[] sendData, List<byte[]> checkCommand) {
        this.sendData = sendData;
        this.checkCommand = checkCommand;
    }

    public String getCheckCommands() {
        StringBuilder command = new StringBuilder();
        if (checkCommand != null && checkCommand.size() > 0) {
            for (byte[] bytes : checkCommand) {
                command.append(",").append(ByteUtil.bytes2HexStr(bytes));
            }
        }
        return command.toString().replaceFirst(",", "");
    }

    public byte[] getSendData() {
        return sendData;
    }

    public String getStrSendData() {
        return ByteUtil.bytes2HexStr(sendData);
    }

    public int getIntSendData() {
        return ByteUtil.byteToInt(sendData);
    }

    public int getSendOutTime() {
        return sendOutTime;
    }

    public void setSendOutTime(int sendOutTime) {
        this.sendOutTime = sendOutTime;
    }

    public int getWaitOutTime() {
        return waitOutTime;
    }

    public void setWaitOutTime(int waitOutTime) {
        this.waitOutTime = waitOutTime;
    }

    public void setSendData(byte[] sendData) {
        this.sendData = sendData;
    }

    public List<byte[]> getCheckCommand() {
        return checkCommand;
    }

    public void setCheckCommand(List<byte[]> checkCommand) {
        this.checkCommand = checkCommand;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(int destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
