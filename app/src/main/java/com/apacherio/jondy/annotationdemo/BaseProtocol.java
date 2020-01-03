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
    @Protocol(index = 0, length = 1, value = (byte) 0x3B)
    public  byte frameHeader ;

    @Protocol(index = 1, length = 1, value = (byte) 0xB3)
    public  byte frameHeader2 ;

    /**
     * 原地址
     */
    @Protocol(index = 2, length = 1, value = 0)
    public  int rawAddress;


    /**
     * 目标地址
     */
    @Protocol(index = 3, length = 1,value = 1)
    public int deviceAddress = 1;

    /**
     * 数据长度 这里注意，如果你也有这规则字段那么传值和我一样传，我这份协议协的数据长度是包含命令吗+版本协议+数据，
     * 等于是 1+1+n 前面两个字节长度是固定的所以传2，如果你是单数据的话那么传0
     *
     */
    @Protocol(index = 4, length = 1,value = 2)
    public int dateNumber;


    /**
     * 命令码
     */
    @Protocol(index = 5, length = 1)
    public  int command ;


    /**
     * 协议版本
     */
    @Protocol(index = 6,length = 1, value = (byte) 0x10)
    public  int dealVersions;

    /**
     * 数据
     */
    @Protocol(index = 7)
    public  int data ;

    /**
     * 异或字节长度
     */
    @Protocol(index = 8, length = 1)
    public  int OXR ;


/** ---- end  按照文档协议配置结束 这部分根据自身协议自由配置   -----*/

/**  -----  分割线    --------------*/


/**  -----  下面这块注意！！，是必须要配置的    --------------*/


    /**
     * 读取数据长度这个字段字节开始位置角标从0开始,示例文档是在第5位，如果你的协议没有数据长度这个字段那么传-1
     */
    @Protocol(dataLenFirst = 4)
    public int dataLenStart;

    /**
     * 数据长度字段在你配置协议对应的角标位置 ，如果你的协议没有数据长度这个字段那么传-1
     */
    @Protocol(dataLenIndex = 4)
    public int dataLenIndex;

    /**
     * 读取数据这个字段开始字节位置,示例文档是第8位，如果你的协议没有数据这个字段那么传-1
     */
    @Protocol(dataFirst = 7)
    public int dataFirst ;

    /**
     * 读取命令码字节开始开始,例文档是第6位，如果你的协议没有这个字段那么传-1
     */
    @Protocol(commandFirst = 5)
    public int commandFirst = 5;

    /**
     * 命令码字段在你配置协议对应的角标位置，如果你的协议没有这个字段那么传-1
     */
    @Protocol(commandIndex = 5)
    public  int commandIndex ;

    /**
     * 流水号起始位置 如果你的协议没有流水号这个字段那么传-1
     */
    @Protocol(runningNumberFirst = -1)
    public int runningNumberFirst = 5;


    /**
     * 帧头字节数
     */
    @Protocol(frameHeaderCount = 2)
    public static int frameHeaderCount;
    /**
     * 校验码规则  0表示异或校验  1表示CRC16校验
     */
    @Protocol(checkCodeRule = 0)
    public static int checkCodeRule;


    /**
     * 通信协议最短字节不包含数据域
     *比如示例的：（帧头(2字节)	+源地址(1字节)+目标地址(1字节)+数据长度(1字节)+命令码(1字节)+数据(n字节)+异或校验(1字节)）
     * 帧头	    源地址      目标地址	数据长度  命令码    协议版本   	数据      异或校验
     * 2个字节	1个字节	    1个字节  	1个字节	 1个字节	1个字节    n个字节    1个字节
     */
    @Protocol(minDalaLen = 8)
    public static int minDalaLen;

//    /**
//     * 心跳命令
//     */
//    @Protocol(heartbeatCommand = (byte) 0x33)
//    public int heartbeatCommand1;
//    /**
//     * 心跳命令
//     */
//    @Protocol(heartbeatCommand = (byte) 0xB3)
//    public int heartbeatCommand2;





}
