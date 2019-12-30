package com.ljw.okserialport.serialport.utils;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class CheckUtil {
    /**
     * @deprecated
     * 异或检验
     *(这个方法好像有点问题，使用下面的@{@link #getXOR(String)})
     * @param bytes
     * @return
     */
    public static String getXOR(byte[] bytes) {
        //byte[] bytes = stringToHexByte(source);  //把普通字符串转换成十六进制字符串
        byte[] mByte = new byte[1];

        for (int i = 0; i < bytes.length - 1; i++) {
            if (i == 0) {
                mByte[0] = (byte) (bytes[i] ^ bytes[i + 1]);
            } else {
                mByte[0] = (byte) (mByte[0] ^ bytes[i + 1]);
            }
        }

        return ByteUtil.bytes2HexStr(mByte);
    }


    /**
     * 获取异或和，支持多字节
     *
     * @param hex
     * @return
     */
    public static String getXOR(String hex) {
        if (hex.length() == 0) {
            return null;
        }
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        int or = 0;
        for (int i = 0, size = hex.length(); i < size; i = i + 2) {
            String subHex = hex.substring(i, i + 2);
            or = or ^ Integer.parseInt(subHex, 16);
        }
        String xor = Integer.toHexString(or) + "";
        if (xor.length() == 1) {
            xor = "0" + xor;
        }
        return xor;
    }
}
