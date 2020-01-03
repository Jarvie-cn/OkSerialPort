package com.apacherio.jondy.annotationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.bean.SerialPortParams;
import com.ljw.okserialport.serialport.callback.CommonCallback;
import com.ljw.okserialport.serialport.callback.SendResultCallback;
import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.core.OkSerialport;
import com.ljw.okserialport.serialport.utils.ApiException;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.ByteUtil;
import com.ljw.okserialport.serialport.utils.CmdPack;
import com.ljw.okserialport.serialport.utils.OkSerialPortLog;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    /**
     * 3．控制纸币器工作模式
     */
    public static byte CONTROL_0A = (byte) 0x0A;
    public static byte CONTROL_06 = (byte) 0x06;
    public static byte CONTROL_09 = (byte) 0x09;
    public static byte CONTROL_03 = (byte) 0x01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkSerialPortLog.isDebug = true;
        com.apacherio.jondy.annotationdemo.OkSerialPort_Protocol.bind();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BanknotesApi.get().executeCoinChange(1, new CommonCallback<Integer>() {
                    @Override
                    public void onStart(CmdPack cmdPack) {
                        Log.e("tag", "onStart ====  ");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("tag", "onSuccess ====  " + integer);

                    }

                    @Override
                    public void onFailed(BaseSerialPortException dLCException) {
                        Log.e("tag", "onFailed ====  " + dLCException.getMessage());
                    }
                });
            }
        });

        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BanknotesApi.get().controlCoinWorkMode(1, new CommonCallback<Integer>() {
                    @Override
                    public void onStart(CmdPack cmdPack) {
                        Log.e("tag", "onStart ====  ");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("tag", "onSuccess ====  " + integer);
                    }

                    @Override
                    public void onFailed(BaseSerialPortException dLCException) {
                        Log.e("tag", "onFailed ====  " + dLCException.getMessage());
                    }
                });
            }
        });
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BanknotesApi.get().controlWorkMode(1, 1, 1, 1, new CommonCallback<Integer>() {
                    @Override
                    public void onStart(CmdPack cmdPack) {
                        Log.e("tag", "onStart ====  ");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("tag", "onSuccess ====  " + integer);
                    }

                    @Override
                    public void onFailed(BaseSerialPortException dLCException) {
                        Log.e("tag", "onFailed ====  " + dLCException.getMessage());
                    }
                });
            }
        });
        List<byte[]> heartCommands = new ArrayList<>();
        heartCommands.add(new byte[]{CONTROL_09,CONTROL_03});
        OkSerialport.getInstance().open(new SerialPortParams.Builder()
                .addDeviceAddress("/dev/ttyS4")
                .addBaudRate(9600)
                .addHeartCommands(heartCommands)
                .isReconnect(true)
                .callback(new SerialportConnectCallback() {
                    @Override
                    public void onError(ApiException apiException) {

                    }

                    @Override
                    public void onOpenSerialPortSuccess() {
                        Log.e("ljw", "onOpenSerialPortSuccess" );

                    }

                    @Override
                    public void onHeatDataCallback(DataPack dataPack) {
                        String command = ByteUtil.bytes2HexStr(dataPack.getCommand());
                        Log.e("ljw", "心跳上来的命令：" + command + "，对应的数据 = " + ByteUtil.bytes2HexStr(dataPack.getData()));
                    }
                })
                .build());

    }


}
