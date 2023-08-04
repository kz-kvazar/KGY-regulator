package com.add.vpn.holders;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private static final List<String> dataList = new ArrayList<>();
    private static double opPressure = 0d;
    private static short actPower = 0;
    private static short constPower = 0;
    private static float throttlePosition = 0f;
    private static int maxPower = 1540;

    public static int getMaxPower() {
        return maxPower;
    }

    public static void setMaxPower(int maxPower) {
        if (maxPower >= 800) DataHolder.maxPower = maxPower;
    }
    //TODO reparse hardcode strings with strings.xml
    public static List<String> toLis() {
        dataList.clear();
        dataList.add("Давлене перед ОП :" + opPressure + "kPa");
        dataList.add("Положение дросселя :" + throttlePosition + "%");
        dataList.add("Активная мощность :" + actPower + "кВт");
        dataList.add("Заданная мощность :" + constPower + "кВт");
        //dataList.add("Максимальная мощность : " + maxPower + "кВт");
        return dataList;
    }

    public static double getOpPressure() {
        return opPressure;
    }

    public static void setOpPressure(Double op) {
        if (op != null) {
            opPressure = op;
            toLis();
        }

    }

    public static short getActPower() {
        return actPower;
    }

    public static void setActPower(Short act) {
        if (act != null) {
            actPower = act;
            toLis();
        }

    }

    public static short getConstPower() {
        return constPower;
    }

    public static void setConstPower(Short cons) {
        if (cons != null) {
            constPower = cons;
            toLis();
        }

    }

    public static float getThrottlePosition() {
        return throttlePosition;
    }

    public static void setThrottlePosition(Float throttle) {
        if (throttle != null) {
            throttlePosition = throttle;
            toLis();
        }
    }
}
