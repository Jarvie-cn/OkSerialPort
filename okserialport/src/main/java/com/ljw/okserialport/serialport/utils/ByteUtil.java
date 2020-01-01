package com.ljw.okserialport.serialport.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @author : LJW
 * @date : 2019/11/22
 * @desc :
 */
public class ByteUtil {
    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * ASCII, 字符串,以空字符结束
     *
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        // 49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            // grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            // convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            // convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }


    /**
     * 字节数组（高位在前）转换成对应的非负整数
     *
     * @param ori    需要转换的字节数组
     * @param offset 目标位置偏移
     * @param len    目标数组长度
     * @return
     */
    public static int bytes2long(byte[] ori, int offset, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            result = result | ((0xff & ori[offset + i]) << (len - 1 - i) * 8);
        }
        return result;
    }

    /**
     * int转换为ASCII, 字符串,不够位数以空字符开头
     *
     * @param value
     * @param length 长度2   SLEN
     * @return
     */
    public static String convertStringAsciiToHex(int value, int length) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        String strValue = value + "";
        for (int i = 0; i < strValue.length(); i++) {
            int ch = strValue.charAt(i);
            sb.append(Integer.toHexString(ch));
        }
        int result = length - strValue.length();
        if (result > 0) {
            for (int i = 0; i < result; i++) {
                //空字符串的ascii为32
                temp.append(Integer.toHexString(32));
            }
        }
        temp.append(sb.toString());
        return temp.toString();

    }




    /**
     * int类型转成高位在前的字节数组
     *
     * @param ori
     * @param arrayAmount 字节数组长度
     * @return
     */
    public static byte[] long2bytes(long ori, byte[] targetBytes, int offset, int arrayAmount) {
        for (int i = 0; i < arrayAmount; i++) {
            // 高位在前
            targetBytes[offset + i] = (byte) ((ori >>> (arrayAmount - i - 1) * 8) & 0xff);
        }
        return targetBytes;
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src    目标数组
     * @param dec    起始位置8
     * @param length 长度2   SLEN
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }

    /**
     * 获取十六进制字节数组
     *
     * @param src    目标数组
     * @param dec    起始位置8
     * @param length 长度2   SLEN
     * @return
     */
    public static byte[] getBytes(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return temp;
    }


    /*
      计算有误
     */
    @Deprecated
    public static float byte2Float(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }


    /**
     * 16 进制转float （可能有小数的情况）
     */
    public static float hexStr2Float(String hexStr) {
        return Float.intBitsToFloat(new BigInteger(hexStr, 16).intValue());
    }

    /**
     * 16 进制转float （可能有小数的情况）
     */
    public static float hexStr2Float(byte[] b) {
        String hexStr = bytes2HexStr(b);

        return Float.intBitsToFloat(new BigInteger(hexStr, 16).intValue());
    }

    public static String formatBytes(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (byte item : b) {
            String hex = Integer.toHexString(item & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex.toUpperCase());
            builder.append(" ");
        }
        return builder.toString();
    }

    public static int byteToInt(byte[] b) {
        int mask = 0xff;
        int temp;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
     *
     * @param
     * @return byte[]
     */
    public static byte[] hexStr2bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 把16进制字符[0123456789abcde]（含大小写）转成字节
     *
     * @param c
     * @return
     */
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String integer2HexStr(int num) {
        return integer2HexStr(num, 2);
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String integer2HexStr(int num, int strLength) {
        String hexStr = Integer.toHexString(num).toUpperCase();
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    public static String hexStr2decimalStr(String hex) {
        return new BigInteger(hex, 16).toString(10);
    }

    private final static String hexStr = "0123456789ABCDEF";
    private final static String[] binaryArray =
            {"0000", "0001", "0010", "0011",
                    "0100", "0101", "0110", "0111",
                    "1000", "1001", "1010", "1011",
                    "1100", "1101", "1110", "1111"};

    /**
     * @param hexString
     * @return 将十六进制转换为二进制字节数组   16-2
     */
    public static String hexStr2BitArr(String hexString) {
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        //字节高四位
        byte high = 0;
        //字节低四位
        byte low = 0;
        for (int i = 0; i < len; i++) {
            //右移四位得到高位
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            //高地位做或运算
            bytes[i] = (byte) (high | low);
        }
        return bytes2BinStr(bytes);
    }

    public static int convertToDecimal(String binary) {
        return Integer.valueOf(binary, 2);
    }


    /**
     * @param
     * @return 二进制数组转换为二进制字符串   2-2
     */
    public static String bytes2BinStr(byte[] bArray) {

        StringBuilder outStr = new StringBuilder();
        int pos = 0;
        for (byte b : bArray) {
            //高四位
            pos = (b & 0xF0) >> 4;
            outStr.append(binaryArray[pos]);
            //低四位
            pos = b & 0x0F;
            outStr.append(binaryArray[pos]);
        }
        return outStr.toString();
    }

    /**
     * 16进制转2进制
     */
    public static String hexStringToByte(int strLength, String hex) {
        int i = Integer.parseInt(hex, 16);
        String str2 = Integer.toBinaryString(i);
        StringBuilder stringBuilder = new StringBuilder(str2);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);

            stringBuilder.append(i).append(":");

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append(";");
        }
        return stringBuilder.toString();
    }

    /**
     * 转16进制字符串
     *
     * @param str
     * @return
     */
    public static String hexString(String str, int length) {
        StringBuilder ret = new StringBuilder();
        byte[] b;
        b = str.getBytes(StandardCharsets.UTF_8);
        for (int i = b.length - 1; i >= 0; i--) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret.append(hex.toUpperCase());
        }
        while (ret.length() < length) {
            ret.insert(0, "0");
        }
        return ret.toString();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String decimal2fitHex(long num, int strLength) {
        String hexStr = Long.toHexString(num).toUpperCase();
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    /**
     * @param hexString
     * @return 将十六进制转换为二进制字节数组   16-2
     */
    public static String hexStr2BinArr(String hexString) {
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        //字节高四位
        byte high = 0;
        //字节低四位
        byte low = 0;
        for (int i = 0; i < len; i++) {
            //右移四位得到高位
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            //高地位做或运算
            bytes[i] = (byte) (high | low);
        }
        return bytes2BinStr(bytes);
        //return new BigInteger(1, bytes).toString(2);// 这里的1代表正数
    }
}
