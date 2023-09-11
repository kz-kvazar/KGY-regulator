package com.add.vpn.holders;


import android.content.Context;
import com.add.vpn.R;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private static final List<String> dataList = new ArrayList<>();
    private static double opPressure = 0d;
    private static short actPower = 0;
    private static short constPower = 0;
    private static float throttlePosition = 0f;
    private static int maxPower = 1560;
    private static float CH4Concentration = 0f;
    private static float gasFlow;

    public static Float getGasFlow() {
        return gasFlow;
    }


    public static int getMaxPower() {
        return maxPower;
    }

    public static void setMaxPower(int maxPower) {
        if (maxPower >= 800 && maxPower <= 1560) DataHolder.maxPower = maxPower;
    }
    public static List<String> toLis(Context context) {
        dataList.clear();

        gasFlow = calculateGasFlow(CH4Concentration, actPower);

        dataList.add(context.getString(R.string.op_Pressure,String.valueOf(opPressure)));
        dataList.add(context.getString(R.string.throttlePosition,  String.valueOf(throttlePosition)));
        dataList.add(context.getString(R.string.act_Power, String.valueOf(actPower)));
        dataList.add(context.getString(R.string.const_Power, String.valueOf(constPower)));
        dataList.add(context.getString(R.string.max_power, String.valueOf(maxPower)));
        dataList.add(context.getString(R.string.gas_Flow, String.valueOf(gasFlow)));
        dataList.add(context.getString(R.string.CH4, String.valueOf(CH4Concentration)));
        return dataList;
    }

    public static double getOpPressure() {
        return opPressure;
    }

    public static void setOpPressure(Double op) {
        if (op != null) {
            opPressure = op;
        }

    }

    public static short getActPower() {
        return actPower;
    }

    public static void setActPower(Short act) {
        if (act != null) {
            actPower = act;
        }

    }

    public static short getConstPower() {
        return constPower;
    }

    public static void setConstPower(Short cons) {
        if (cons != null) {
            constPower = cons;
        }

    }

    public static float getThrottlePosition() {
        return throttlePosition;
    }

    public static void setThrottlePosition(Float throttle) {
        if (throttle != null) {
            throttlePosition = throttle;
        }
    }

    public static Float getCH4Concentration() {
        return CH4Concentration;
    }

    public static void setCH4Concentration(Float Concentration) {
        if (Concentration != null) {
            CH4Concentration = Concentration;
        }
    }

    public static float calculateGasFlow(float concentration, int power) {
        if (power > 0 && concentration > 0) {
            float concentration1 = 27.24f;
            float concentration2 = 41.5f;
            float power1 = 1560f;
            float power2 = 1170f;

            float q11 = 1192.5f;
            float q12 = 918.5f;
            float q21 = 812.8f;
            float q22 = 626.0f;

            float q1 = q11 + (q12 - q11) * (power - power1) / (power2 - power1);
            float q2 = q21 + (q22 - q21) * (power - power1) / (power2 - power1);
            float interpolatedFlow = q1 + (q2 - q1) * (concentration - concentration1) / (concentration2 - concentration1);

            return Math.round(interpolatedFlow * 10.0f) / 10.0f;
        } else return 0;
    }
}
