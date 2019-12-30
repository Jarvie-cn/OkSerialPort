package com.apacherio.jondy.annotationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ljw.okserialport.serialport.bean.DataPack;
import com.ljw.okserialport.serialport.callback.CommonCallback;
import com.ljw.okserialport.serialport.callback.SerialportConnectCallback;
import com.ljw.okserialport.serialport.core.OkSerialport;
import com.ljw.okserialport.serialport.utils.ApiException;
import com.ljw.okserialport.serialport.utils.BaseSerialPortException;
import com.ljw.okserialport.serialport.utils.ByteUtil;
import com.ljw.okserialport.serialport.utils.CmdPack;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                BanknotesApi.get().controlWorkMode(1,1,1,1, new CommonCallback<Integer>() {
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


//        tv.setText("静态编译模拟Butterknife绑定成功！");
        OkSerialport.getInstance().init("/dev/ttyS4", 9600, new SerialportConnectCallback() {
            @Override
            public void onError(ApiException apiException) {
                Log.e("tag", "onError ====  " + apiException.getMessage());
            }

            @Override
            public void onOpenSerialPortSuccess() {
                Log.e("tag", "onOpenSerialPortSuccess ====  ");

            }

            @Override
            public void onHeatDataCallback(DataPack dataPack) {
                String command = ByteUtil.bytes2HexStr(dataPack.getCommand());
                Log.e("ljw","心跳上来的命令："  + command + "，对应的数据 = " +ByteUtil.bytes2HexStr(dataPack.getData() ));
            }


        });
    }
}
