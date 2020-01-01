package com.ljw.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2018/2/11 0011.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Protocol {
    /**
     * 协议字段角标
     */
    int index() default -1;

    /**
     * 协议字段的字节长度长度，如果该字段的字节长度是不确定的那么就不需要赋值，比如：数据域
     */
    int length() default Integer.MAX_VALUE;

    /**
     * 值为byte类型
     * 协议字段的值，如果值是相对固定的就必须要赋值，如果不固定就不需要赋值，
     * 比如：命令码，流水，数据域数据等，
     * 这要里注意一下：
     * 如果你的协议里面有数据长度这个字段，那么也必须要赋值；
     * 如果你的协议数据长度只是计算数据域那么value = 0，
     * 数据长度包含其他比如命令码等，那么value = 命令码等字节长度；数据域的长度会动态计算的
     */
    byte value() default -1;

    /**
     * 命令码长度
     */
    int commandLen() default -1;

    /**
     * 目标地址长度
     */
    int targetAddressLen() default -1;

    /**
     * 原地址长度
     */
    int rawAddressLen() default -1;

    /**
     * 协议版本长度
     */
    int protocolLen() default -1;

    /**
     * 异或字节长度
     */
    int oxrLen() default -1;

    /**
     * 数据长度
     */
    int dateLen() default -1;

    /**
     * 通信协议最短字节不包含数据域（
     */
    int minPackLen() default -1;

    /**
     * 帧头1
     */
    byte frameHeader() default -1;

    /**
     * 帧头2
     */
    byte frameHeader2() default -1;

    /**
     * 数据长度字节开始位置
     */
    int dataLenFirst() default Integer.MAX_VALUE;


    /**
     * 数据字节默认起始位置
     */
    int dataFirst() default Integer.MAX_VALUE;

    /**
     * 命令码字节开始位置
     */
    int commandFirst() default Integer.MAX_VALUE;

    /**
     * 流水号字节开始位置
     */
    int runningNumberFirst() default Integer.MAX_VALUE;
    /**
     * 数据长度配置的角标位置
     */
    int dataLenIndex() default Integer.MAX_VALUE;

    /**
     * 命令码配置的角标位置
     */
    int commandIndex() default Integer.MAX_VALUE;
    /**
     * 安卓地址
     */
    byte androidAdress() default -1;

    /**
     * 主控板地址
     */
    byte hardwareAdress() default -1;

    /**
     * 版本协议
     */
    byte dealVersions() default -1;

    /**
     * 最小数据长度
     */
    int minDalaLen() default -1;

    /**
     * 帧头字节数
     */
    int frameHeaderCount() default -1;

    /**
     * 校验规则 0表示异或校验  1表示CRC16校验
     */
    int checkCodeRule() default -1;

    /**
     * 心跳命令
     */
    byte heartbeatCommand() default -1;
}
