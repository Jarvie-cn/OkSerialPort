package com.ljw.okserialport.serialport.utils;

import android.text.TextUtils;


import com.ljw.okserialport.serialport.bean.ProtocolBean;
import com.ljw.okserialport.serialport.core.OkSerialPort_ProtocolManager;

import java.util.Map;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :数据拼装
 */
public class OrderAssembleUtil {

    /**
     * 1个字节8Bit,int占4*8=32位的时候，最大可以赋值为：215
     */
    public static byte[] allCmd(byte[] cmd, String[] fillDatas, String data) {
        Map<Integer, ProtocolBean> mProtocolMap;
        mProtocolMap = OkSerialPort_ProtocolManager.mProtocolMap;

        StringBuilder builder = new StringBuilder();
        int index = 0;
        int byteIndex = 0;
        for (int i = 0; i < mProtocolMap.keySet().size(); i++) {
            if (mProtocolMap.get(i).value == -1) {
                if (byteIndex == OkSerialPort_ProtocolManager.DATASTARTINDEX) {
                    //数据默认起始位置
                    builder.append(data);
                    int dataLength = TextUtils.isEmpty(data) ? 0 : data.length() / 2;
                    byteIndex = byteIndex + dataLength;
                } else if (byteIndex == OkSerialPort_ProtocolManager.COMMANDSTARTINDEX) {
                    //命令码开始标识
                    String command = ByteUtil.bytes2HexStr(cmd);
                    builder.append(command);
                    byteIndex = byteIndex + mProtocolMap.get(i).length;

                } else if (byteIndex == OkSerialPort_ProtocolManager.RUNNINGNUMBERINDEX) {
                    //流水号
                    builder.append(FlowManager.get().getFlowWater());
                    byteIndex = byteIndex + mProtocolMap.get(i).length;

                } else if (i == mProtocolMap.keySet().size() - 1) {
                    //校验码
                    if (OkSerialPort_ProtocolManager.CHECKCODERULE == 0) {
                        builder.append(CheckUtil.getXOR(builder.toString()));
                    } else {
                        builder.append(CRC16Utils.getCRC16(builder.toString()));
                    }
                } else {
                    builder.append(fillDatas[index]);
                    index++;
                    byteIndex = byteIndex + mProtocolMap.get(i).length;

                }
            } else {
                if (byteIndex == OkSerialPort_ProtocolManager.DATALENINDEX) {
                    //数据长度标识
                    int dataLength = TextUtils.isEmpty(data) ? 0 : data.length() / 2;
                    String strDataLength = ByteUtil.integer2HexStr(dataLength +
                            ByteUtil.byteToInt(new byte[]{mProtocolMap.get(i).value}), mProtocolMap.get(i).length * 2);
                    builder.append(strDataLength);
                    byteIndex = byteIndex + mProtocolMap.get(i).length;
                } else {
                    builder.append(ByteUtil.bytes2HexStr(new byte[]{mProtocolMap.get(i).value}));
                    byteIndex = byteIndex + mProtocolMap.get(i).length;
                }


            }


        }
        LJWLogUtils.e("发送:" + builder.toString());
        return ByteUtil.hexStr2bytes(builder.toString());
    }


//    public static byte[] allCmd(byte[] cmd, String[] data) {
//
//        return allCmd(cmd, data);
//    }
}
