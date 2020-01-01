package com.ljw.okserialport.serialport.utils;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.bean.ProtocolBean;
import com.ljw.okserialport.serialport.callback.DataPackCallback;
import com.ljw.okserialport.serialport.core.OkSerialPort_ProtocolManager;

import java.nio.ByteBuffer;
import java.util.Map;


/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :数据解析
 */
public class ResultDataParseUtils {

    public static boolean handleDataPack(ByteBuffer byteBuffer, byte[] received, int size, DataPackCallback dataPackCallback) {
        OkSerialPortLog.e("开始校验接收到的数据！！！");
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.clear();
        }
        //从received数组中的0到0+length区域读取数据并使用相对写写入此byteBuffer
        byteBuffer.put(received, 0, size);
        //limit = position;position = 0;mark = -1;

        // 将一个处于存数据状态的缓冲区变为一个处于准备取数据的状态
        byteBuffer.flip();
        Map<Integer, ProtocolBean> mProtocolMap;
        mProtocolMap = OkSerialPort_ProtocolManager.mProtocolMap;
        int readable;
        while ((readable = byteBuffer.remaining()) >= OkSerialPort_ProtocolManager.MINDALALEN) {
            //标记一下当前位置,调用mark()来设置mark=position，再调用reset()可以让position恢复到标记的位置
            byteBuffer.mark();
            int frameStart = byteBuffer.position();

            boolean isPass = true;

            a:
            for (int i = 0; i < OkSerialPort_ProtocolManager.FRAMEHEADERCOUNT; i++) {
                byte head = byteBuffer.get();
                if (head != mProtocolMap.get(i).value) {
                    isPass = false;
                    OkSerialPortLog.e("帧头校验不通过！" + ByteUtil.bytes2HexStr(new byte[]{head}) + " ====== "  + ByteUtil.bytes2HexStr(new byte[]{mProtocolMap.get(i).value}));
                    break a;
                }
            }
            if (!isPass) {
                continue;
            }

            //校验数据域长度,数据域在第5个位置
            int datalenCount = 0;
            switch (OkSerialPort_ProtocolManager.mProtocolMap.get(OkSerialPort_ProtocolManager.DATALENINDEX).length) {
                case 1:
                    datalenCount = ByteUtil.byteToInt(new byte[]{byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST)});
                    break;
                case 2:
                    datalenCount = ByteUtil.byteToInt(new byte[]{byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 1)});

                    break;
                case 3:
                    datalenCount = ByteUtil.byteToInt(new byte[]{byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 1), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 2)});
                    break;
                case 4:
                    datalenCount = ByteUtil.byteToInt(new byte[]{byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 1), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 2), byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENFIRST + 3)});
                    break;
                default:
                    break;
            }
//            byte[] len = new byte[]{byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENINDEX),byteBuffer.get(frameStart + OkSerialPort_ProtocolManager.DATALENINDEX + OkSerialPort_ProtocolManager.mProtocolMap.get(5).length)};
            //数据域长度(命令码+数据的长度) 减去默认的长度剩下就是数据长度
            OkSerialPortLog.e("数据长度：" + datalenCount);
            int dataLen = datalenCount - mProtocolMap.get(OkSerialPort_ProtocolManager.DATALENINDEX).value;
            OkSerialPortLog.e("去除后单纯的数据长度：" + dataLen);

            // 总数据长度(实际长度=最小长度(不包含数据域长度)+实际数据长度)
            int total = OkSerialPort_ProtocolManager.MINDALALEN + dataLen;
            OkSerialPortLog.e("整个包总数据长度：" + total);
            if (readable < total) {
                //重置处理的位置,跳出循环
                OkSerialPortLog.e("数据长度不合理：" + total + "  =====  "+readable);
                byteBuffer.reset();
                break;
            }
            //回到头
            byteBuffer.reset();
            //获取整个包
            byte[] allPack = new byte[total];
            byteBuffer.get(allPack);
            int oXRLen = mProtocolMap.get(mProtocolMap.size() - 1).length;
            OkSerialPortLog.e("整个包数据= " + ByteUtil.bytes2HexStr(allPack));

            //校验码
            if (OkSerialPort_ProtocolManager.CHECKCODERULE == 0) {
                //异或的数据是第一位到最后
                String dataToXOR = CheckUtil.getXOR(ByteUtil.getBytes(allPack, 0, total - oXRLen));
                //获取数据包中的异或值

                String currentXOR = ByteUtil.bytes2HexStr(allPack, total - oXRLen, mProtocolMap.get(mProtocolMap.size() - 1).length);
                OkSerialPortLog.e("异或校验数据：" + dataToXOR + ",被校验数据：" + currentXOR);

                if (dataToXOR.equalsIgnoreCase(currentXOR)) {
                    //获取数据码,根据数据,数据码在第六位,所以从8算起
                    byte[] data = ByteUtil.getBytes(allPack, OkSerialPort_ProtocolManager.DATAFIRST, dataLen);
                    byte[] commands = ByteUtil.getBytes(allPack, OkSerialPort_ProtocolManager.COMMANDFIRST, mProtocolMap.get(OkSerialPort_ProtocolManager.COMMANDINDEX).length);
                    //最后清掉之前处理过的不合适的数据
                    if (dataPackCallback != null) {
                        dataPackCallback.setDataPack(new DataPack(allPack, data, commands));
                    }
                } else {
                    //不一致回调这次帧头之后
                    byteBuffer.position(frameStart + mProtocolMap.get(mProtocolMap.size() - 1).length);
                }
            } else {
                // 计算crc16
                String check = CRC16Utils.getCRC16(allPack, 0, total - oXRLen);
                String recCheck = ByteUtil.bytes2HexStr(allPack, total - oXRLen, oXRLen).toUpperCase();
                OkSerialPortLog.e("异或校验数据：" + check + ",被校验数据：" + recCheck);
                // 校验通过
                if (check.equals(recCheck)) {
                    //获取数据码,根据数据,数据码在第六位,所以从8算起
                    byte[] data = ByteUtil.getBytes(allPack, OkSerialPort_ProtocolManager.DATAFIRST, dataLen);
                    byte[] commands = ByteUtil.getBytes(allPack, OkSerialPort_ProtocolManager.COMMANDFIRST, mProtocolMap.get(OkSerialPort_ProtocolManager.COMMANDINDEX).length);
                    //最后清掉之前处理过的不合适的数据
                    if (dataPackCallback != null) {
                        dataPackCallback.setDataPack(new DataPack(allPack, data, commands));
                    }
                } else {
                    //不一致回调这次帧头之后
                    byteBuffer.position(frameStart + mProtocolMap.get(mProtocolMap.size() - 1).length);
                }
            }


        }
        //最后清掉之前处理过的不合适的数据
        byteBuffer.compact();
        return true;
    }


}
