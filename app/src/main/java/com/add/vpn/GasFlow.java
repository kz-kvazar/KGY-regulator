package com.add.vpn;

public class GasFlow {
    public static float calculateGasFlow(float CH4_1, float CH4_2, int power) {
        float concentration = 0;
        if(CH4_1 > 0 && CH4_2 > 0) {
            concentration = (CH4_1 + CH4_2)/2;
        }else if(CH4_1 <= 0 && CH4_2 > 0){
            concentration = CH4_2;
        } else if (CH4_1 > 0 && CH4_2 <= 0) {
            concentration = CH4_1;
        }

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
