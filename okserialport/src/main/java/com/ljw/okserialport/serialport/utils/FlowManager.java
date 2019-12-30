package com.ljw.okserialport.serialport.utils;

public class FlowManager {
    private int flowWater = 0;

    private static FlowManager flowManager = new FlowManager();

    public static FlowManager get() {
        return flowManager;
    }

    public synchronized int getFlowWater() {
        if (flowWater >= 0xff) {
            flowWater = 0;
        } else {
            flowWater++;
        }
        return flowWater;
    }
}
