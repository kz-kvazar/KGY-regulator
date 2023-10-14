package com.add.vpn.firebase;

public class FBreportItem {
    private String date;
    private Integer powerConstant;
    private Float CH4_1;
    private Float gasFlow;
    private Float CH4_2;

    public FBreportItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getGasFlow() {
        return gasFlow;
    }

    public void setGasFlow(Float gasFlow) {
        this.gasFlow = gasFlow;
    }

    public Integer getPowerActive() {
        return powerConstant;
    }

    public void setPowerActive(Integer powerConstant) {
        this.powerConstant = powerConstant;
    }

    public Float getCH4_1() {
        return CH4_1;
    }

    public void setCH4_1(Float CH4_1) {
        this.CH4_1 = CH4_1;
    }

    public Float getCH4_2() {
        return CH4_2;
    }

    public void setCH4_2(Float CH4_2) {
        this.CH4_2 = CH4_2;
    }
}
