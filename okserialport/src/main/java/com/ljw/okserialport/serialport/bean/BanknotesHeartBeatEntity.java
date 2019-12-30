package com.ljw.okserialport.serialport.bean;


import com.ljw.okserialport.serialport.utils.ByteUtil;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class BanknotesHeartBeatEntity {
    /**
     * 心跳数据包
     */
    private DataPack dataPack;
    /**
     * 流水号
     */
    private int pipelineNumber;
    /**
     * 出货的货道编号，范围1-8
     * 当Z1值>=0x80H时，解析如下：
     * bit7=1
     * bit6,bit5,bit4: 纸币路径
     * 000：钱箱
     * 001：退币位
     * 010：退还顾
     * 011：未用
     * 100：不接受
     * Bit3,bit2,bit1,bit0:纸币币种
     * 0000=1美元
     * 0001=5美元
     * 0010=10美元
     * 0011=20美元
     * 当Z1值<0x80H时，解析如下：
     * 01H (纸币器)电动机失效
     * 02H 传感器问题
     * 03H 纸币器忙，无法立即响应
     * 04H ROM校验和错
     * 05H 纸币器入口堵塞
     * 06H 正处于复位状态
     * 07H 处于 escrow位置的纸币被异常取走，纸
     * 币器将返回‘BILL RETURNED’消息
     * 08H钱植被打开或者移走
     * 09H 纸币器被 VMC或由于其它原因被禁用
     * 0AH 无效的退币请求
     * 0BH 纸币无法识别，不接受
     * 0CH 纸币异常移除
     */
    private int z1;
    private static final int OX80H = 128;

    public int getUs() {
        int usDollar = -1;
        if (z1 >= 128) {
            //10进制转成16进制
            String hexStr = ByteUtil.integer2HexStr(z1);
            //16进制转成足位的二进制字符串
            String binStr = ByteUtil.hexStr2BitArr(hexStr);
            //将char[]反转因为bit0在末尾
            String binTempStr = new StringBuilder(binStr).reverse().toString();
            //string-[0,1,1,1,0]
            char[] binCharArr = binTempStr.toCharArray();
            String bit654 = "" + binCharArr[4] + binCharArr[5] + binCharArr[6];
            if (bit654.equals("000")) {//钱箱
                String bit3210 = "" + binCharArr[3] + binCharArr[2] + binCharArr[1] + binCharArr[0];
                switch (bit3210) {//钱数
                    case "0000":
                        usDollar = 1;
                        break;
                    case "0001":
                        usDollar = 5;
                        break;
                    case "0010":
                        usDollar = 10;
                        break;
                    case "0011":
                        usDollar = 20;
                        break;
                }
            }
        } 
        return usDollar;
    }

    public int getPipelineNumber() {
        return pipelineNumber;
    }

    public void setPipelineNumber(int pipelineNumber) {
        this.pipelineNumber = pipelineNumber;
    }


    public int getZ1() {
        return z1;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public DataPack getDataPack() {
        return dataPack;
    }

    public void setDataPack(DataPack dataPack) {
        this.dataPack = dataPack;
        if (dataPack != null && dataPack.getData().length > 0) {
            byte[] data = dataPack.getData();
            pipelineNumber = ByteUtil.byteToInt(
                    ByteUtil.getBytes(data, 0, 1));
            z1 = ByteUtil.byteToInt(
                    ByteUtil.getBytes(data, 1, 1));

        }
    }


    @Override
    public String toString() {
        return "BanknotesHeartBeatEntity{" +
                "dataPack=" + dataPack +
                ", pipelineNumber=" + pipelineNumber +
                ", z1=" + z1 +
                '}';
    }
}
