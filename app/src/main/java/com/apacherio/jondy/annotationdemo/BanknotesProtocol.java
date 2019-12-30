package com.apacherio.jondy.annotationdemo;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :纸币器 232协议
 */
public class BanknotesProtocol extends BaseProtocol {

    /**
     * 心跳命令-纸币器状态主动上报
     */
    public static byte HEART_BEAT_CMD = (byte) 0x33;
    /**
     * 2．获取纸币器设备参数
     */
    public static byte GET_BANKNOTES_PARAM = (byte) 0x31;
    /**
     * 3．控制纸币器工作模式
     */
    public static byte CONTROL_WAY_SHIPMENT_CMD = (byte) 0x34;
    /**
     * ．硬币器状态主动上报
     */
    public static byte COIN_HEART_BEAT_CMD = (byte) 0x0B;
    /**
     * 2．获取硬币器设备参数09H
     */
    public static byte GET_COIN_PARAM = (byte) 0x09;
    /**
     * 3．控制硬币器工作模式0CH
     */
    public static byte CONTROL_COIN_WORK_MODE = (byte) 0x0C;
    /**
     * 4．找零器动作0FH
     */
    public static byte CHANGE_ACTION = (byte) 0x0F;

}
