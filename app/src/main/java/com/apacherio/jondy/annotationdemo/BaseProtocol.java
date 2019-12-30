package com.apacherio.jondy.annotationdemo;


import com.ljw.protocol.Protocol;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class BaseProtocol {


    /**
     * 帧头
     */
    @Protocol(index = 0,length = 1,value =  (byte)0x3B)
    public static byte FRAME_HEADER =  0x3B;

    @Protocol(index = 1,length = 1,value = (byte)0xB3)
    public static byte FRAME_HEADER2 =  100;

    /**
     * 原地址长度
     */
    @Protocol(index = 2,length = 1,value = 0)
    public static int RAW_ADDRESS_IEN = 1;


    /**
     * 目标地址长度
     */
    @Protocol(index = 3,length = 1,value = 1)
    public static int DEVICE_ADDRESS_IEN = 1;

    /**
     * 数据长度
     */
    @Protocol(index = 4,length = 1,value = 2)
    public static int DATE_NUMBER_LEN = 1;


    /**
     * 命令码长度
     */
    @Protocol(index = 5,length = 1)
    public static int COMMAND_LEN = 1;





    /**
     * 协议版本长度
     */
    @Protocol(index = 6,length = 1,value = (byte) 0x10)
    public static int DEAL_VERSIONS_LEN = 1;

    /**
     * 数据长度
     */
    @Protocol(index = 7)
    public static int DATA_LEN = 1;

    /**
     * 异或字节长度
     */
    @Protocol(index = 8,length = 1)
    public static int OXR_LEN = 1;


    /**
     * 数据长度标识,第5位
     */
    @Protocol(dataLenIndex = 4)
    public static int DATA_LEN_INDEX = 4;

    /**
     * 数据默认位置,第8位
     */
    @Protocol(dataStartIndex = 7)
    public static int DATA_INDEX = 7;

    /**
     * 命令码开始标识,第6位
     */
    @Protocol(commandStartIndex = 5)
    public static int COMMAND_INDEX = 5;

    /**
     * 流水号起始位置 没有默认传-1
     */
    @Protocol(runningNumberIndex = -1)
    public int RUNNING_NUMBERINDEX = 5;
    /**
     * 帧头字节数
     */
    @Protocol(frameHeaderCount = 2)
    public static int FRAME_HEADERCOUNT ;
    /**
     * 校验码规则  0表示异或校验  1表示CRC16校验
     */
    @Protocol(checkCodeRule = 0)
    public static int CHECK_CODERULE ;


    /**
     * 通信协议最短字节不包含数据域（帧头(2字节)	+源地址(1字节)+目标地址(1字节)+数据长度(1字节)+命令码(1字节)+数据(n字节)+异或校验(1字节)）
     * 帧头	    源地址      目标地址	数据长度  命令码    协议版本   数据      异或校验
     * 2个字节	1个字节	    1个字节  	1个字节	 1个字节	1个字节    n个字节    1个字节
     */
    @Protocol(minDalaLen = 8)
    public static int MIN_PACK_LEN ;


    @Protocol(heartbeatCommand = (byte) 0x33)
    public  int heartbeatCommand1 ;
    @Protocol(heartbeatCommand = (byte) 0x0B)
    public  int heartbeatCommand2 ;


}
