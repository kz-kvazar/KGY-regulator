package com.add.vpn.firebase;

public class FBreportItem {
    private String date;
    private Integer powerConstant;
    private Long generated;
    private Float CH4_1;
    private Float gasFlow;
    private Float CH4_2;
    private Integer cleanOil;
    private Integer avgTemp;
    private Float resTemp;
    private Float CH4_KGY;
    public FBreportItem(String date) {
        this.date = date;
    }

    public Long getGenerated() {
        return generated;
    }

    public void setGenerated(Long generated) {
        this.generated = generated;
    }

    public Float getCH4_KGY() {
        return CH4_KGY;
    }

    public void setCH4_KGY(Float CH4_KGY) {
        this.CH4_KGY = CH4_KGY;
    }

    public Integer getPowerConstant() {
        return powerConstant;
    }

    public void setPowerConstant(Integer powerConstant) {
        this.powerConstant = powerConstant;
    }

    public Integer getCleanOil() {
        return cleanOil;
    }

    public void setCleanOil(Integer cleanOil) {
        this.cleanOil = cleanOil;
    }

    public Integer getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(Integer avgTemp) {
        this.avgTemp = avgTemp;
    }

    public Float getResTemp() {
        return resTemp;
    }

    public void setResTemp(Float resTemp) {
        this.resTemp = resTemp;
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
