package com.ljw.okserialport.serialport.core;

import com.ljw.okserialport.serialport.bean.ProtocolBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : LJW
 * @date : 2019/12/30
 * @desc :
 */
public class OkSerialPort_ProtocolManager {

    public static Map<Integer, ProtocolBean> mProtocolMap = new HashMap<>();
    public static List<byte[]> mHeartCommands = new ArrayList<>();

    public static int DATALENFIRST ;
    public static int COMMANDINDEX ;
    public static int DATALENINDEX ;
    public static int DATAFIRST ;
    public static int COMMANDFIRST ;
    public static int RUNNINGNUMBERFIRST ;
    public static int FRAMEHEADERCOUNT ;
    public static int CHECKCODERULE ;
    public static int MINDALALEN ;

    private volatile static OkSerialPort_ProtocolManager instance;

    public static OkSerialPort_ProtocolManager getInstance() {
        if (instance == null) {
            synchronized (OkSerialPort_ProtocolManager.class) {
                if (instance == null) {
                    instance = new OkSerialPort_ProtocolManager();
                }
            }
        }
        return instance;
    }

    public  void bind(int commandindex,int datalenindex,int datalenfirst,int datafirst,int commandfirst,int runningnumberfirst,int frameheadercount,int checkcoderule,int mindalalen,Map<Integer, ProtocolBean> map,List<byte[]> list){
        COMMANDINDEX = commandindex;
        DATALENINDEX = datalenindex;
        DATALENFIRST = datalenfirst;
        DATAFIRST = datafirst;
        COMMANDFIRST = commandfirst;
        RUNNINGNUMBERFIRST = runningnumberfirst;
        FRAMEHEADERCOUNT = frameheadercount;
        CHECKCODERULE = checkcoderule;
        MINDALALEN = mindalalen;
        mProtocolMap = map;
        mHeartCommands = list;
    }

}
