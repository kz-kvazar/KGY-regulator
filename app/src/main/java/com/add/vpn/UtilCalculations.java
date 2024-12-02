package com.add.vpn;

public class UtilCalculations {
    public static float calculateGasFlow(Float CH4_1, Float CH4_2, Integer power) {
        float concentration = averageConcentration(CH4_1,CH4_2);
        if(power != null && power == 0) return 1f;

        if (power != null && power > 0 && concentration > 0) {
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
        } else return 1f;
    }
    public static float averageConcentration(Float ch41, Float ch42){
        float result = 1f;
        if (ch41 == null || ((ch41 > 100 || ch41 < 0) && ch42 < 100 && ch42 > 0)) {
            result = ch42;
        } else if (ch42 == null || ((ch42 > 100 || ch42 < 0) && ch41 < 100 && ch41 > 0)) {
            result = ch41;
        } else if (ch42 < 100 && ch42 > 0 && ch41 < 100 && ch41 > 0) {
            result = (ch41 + ch42)/2;
        }
        return result;
    }
}
