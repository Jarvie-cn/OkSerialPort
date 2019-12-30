package com.apacherio.jondy.annotationdemo;


import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.callback.CommonCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.ByteUtil;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.core.OkSerialport;

/**
 * @author : LJW
 * @date : 2019/11/23
 * @desc :232纸币器硬件方法
 */
public class BanknotesApi {


    private volatile static BanknotesApi instance;

    public static BanknotesApi get() {
        if (instance == null) {
            synchronized (BanknotesApi.class) {
                if (instance == null) {
                    instance = new BanknotesApi();
                }
            }
        }
        return instance;
    }



    public void executeCoinChange(int number, final CommonCallback<Integer> commonCallback) {
        StringBuilder data = new StringBuilder();
        data.append(ByteUtil.integer2HexStr(2));
        data.append(ByteUtil.integer2HexStr(number * 2));
        OkSerialport.getInstance().send(data.toString(), BanknotesProtocol.CHANGE_ACTION, new SendResultCallback() {
            @Override
            public void onStart(CmdPack cmdPack) {
                if (commonCallback != null) {
                    commonCallback.onStart(cmdPack);
                }
            }

            @Override
            public void onSuccess(DataPack dataPack) {
                if (commonCallback != null) {
                    int result = -1;
                    result = (ByteUtil.byteToInt(
                            ByteUtil.getBytes(dataPack.getData(), 0, 1)));
                    commonCallback.onSuccess(result);
                }
            }

            @Override
            public void onFailed(BaseSerialPortException dLCException) {
                if (commonCallback != null) {
                    commonCallback.onFailed(dLCException);
                }
            }
        });
    }


    /**
     * 控制纸币器工作模式 让纸币器开始工作调用此命令，停止工作也调用此命令，所有参数传0即可
     *
     * @param us1  1 表示接收1美元的纸币  0 表示不接收
     * @param us5  1 表示接收5美元的纸币  0 表示不接收
     * @param us10 1 表示接收10美元的纸币  0 表示不接收
     * @param us20 1 表示接收20美元的纸币  0 表示不接收
     * @Callback 执行结果 int类型  00H=控制成功  FFH=控制失败
     */
    public void controlWorkMode(int us1, int us5, int us10, int us20, final CommonCallback<Integer> commonCallback) {
        StringBuilder data = new StringBuilder();
        data.append(ByteUtil.integer2HexStr(0));
        String s = "0000" + us20 + us10 + us5 + us1;
        int i = ByteUtil.convertToDecimal(s);
        data.append(ByteUtil.integer2HexStr(i));
        data.append(ByteUtil.integer2HexStr(0));
        data.append(ByteUtil.integer2HexStr(0));
        OkSerialport.getInstance().send(data.toString(),BanknotesProtocol.CONTROL_WAY_SHIPMENT_CMD,new SendResultCallback() {
            @Override
            public void onStart(CmdPack cmdPack) {
                if (commonCallback != null) {
                    commonCallback.onStart(cmdPack);
                }
            }

            @Override
            public void onSuccess(DataPack dataPack) {
                if (commonCallback != null) {
                    int result = -1;
                    result = (ByteUtil.byteToInt(
                            ByteUtil.getBytes(dataPack.getData(), 0, 1)));
                    commonCallback.onSuccess(result);
                }
            }

            @Override
            public void onFailed(BaseSerialPortException dLCException) {
                if (commonCallback != null) {
                    commonCallback.onFailed(dLCException);
                }
            }
        });
    }


    /**
     * 控制硬币器工作模式0CH  让硬币器开始工作调用此命令，停止工作也调用此命令，所有参数传0即可
     *
     * @param cent25 1 表示接收25美分硬币  0表示不接收
     * @callback 执行结果 int类型 00H=控制成功   FFH=控制失败
     */
    public void controlCoinWorkMode(int cent25, final CommonCallback<Integer> commonCallback) {
        StringBuilder data = new StringBuilder();
        data.append(ByteUtil.integer2HexStr(0));

        String s = "00000" + cent25 + cent25 + cent25;
        int i = ByteUtil.convertToDecimal(s);
        data.append(ByteUtil.integer2HexStr(i));
        data.append(ByteUtil.integer2HexStr(0));
        data.append(ByteUtil.integer2HexStr(0));
        OkSerialport.getInstance().send(data.toString(),BanknotesProtocol.CONTROL_COIN_WORK_MODE, new SendResultCallback() {
            @Override
            public void onStart(CmdPack cmdPack) {
                if (commonCallback != null) {
                    commonCallback.onStart(cmdPack);
                }
            }

            @Override
            public void onSuccess(DataPack dataPack) {
                if (commonCallback != null) {
                    int result = -1;
                    result = (ByteUtil.byteToInt(
                            ByteUtil.getBytes(dataPack.getData(), 0, 1)));
                    commonCallback.onSuccess(result);
                }
            }

            @Override
            public void onFailed(BaseSerialPortException dLCException) {
                if (commonCallback != null) {
                    commonCallback.onFailed(dLCException);
                }
            }
        });
    }

}
