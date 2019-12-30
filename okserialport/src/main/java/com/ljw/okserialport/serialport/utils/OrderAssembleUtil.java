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
    private static byte[] allCmd(byte[] cmd, int dataLength, String data) {
        Map<Integer, ProtocolBean> mProtocolMap;
        mProtocolMap = OkSerialPort_ProtocolManager.mProtocolMap;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mProtocolMap.keySet().size(); i++) {
            if (i == OkSerialPort_ProtocolManager.DATALENINDEX) {
                //数据长度标识
                String strDataLength = ByteUtil.integer2HexStr(dataLength +
                        ByteUtil.byteToInt(new byte[]{mProtocolMap.get(i).value}), mProtocolMap.get(i).length * 2);
                builder.append(strDataLength);
            } else if (i == OkSerialPort_ProtocolManager.DATASTARTINDEX) {
                //数据默认起始位置
                builder.append(data);
            } else if (i == OkSerialPort_ProtocolManager.COMMANDSTARTINDEX) {
                //命令码开始标识
                String command = ByteUtil.bytes2HexStr(cmd);
                builder.append(command);
            } else if (i == OkSerialPort_ProtocolManager.RUNNINGNUMBERINDEX) {
                //流水号
                builder.append(FlowManager.get().getFlowWater());
            } else if (i == mProtocolMap.keySet().size() - 1) {
                //校验码
                if (OkSerialPort_ProtocolManager.CHECKCODERULE == 0) {
                    builder.append(CheckUtil.getXOR(builder.toString()));
                }else {
                    builder.append(CRC16Utils.getCRC16(builder.toString()));
                }
            } else {
                builder.append(ByteUtil.bytes2HexStr(new byte[]{mProtocolMap.get(i).value}));
            }

        }
        LJWLogUtils.e("发送:" + builder.toString());
        return ByteUtil.hexStr2bytes(builder.toString());
    }


    public static byte[] allCmd(byte[] cmd, String data) {
        int dataLength = TextUtils.isEmpty(data) ? 0 : data.length() / 2;
        return allCmd(cmd, dataLength, data);
    }
}
