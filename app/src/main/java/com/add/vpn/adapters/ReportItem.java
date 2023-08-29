package com.add.vpn.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportItem {
    private final Date time;
    private final String CH4;
    private final String consumption;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    private final String constPower;


    public ReportItem(Date time, String CH4, String consumption, String constPower) {
        this.time = time;
        this.CH4 = CH4;
        this.consumption = consumption;
        this.constPower = constPower;
    }

    public String getTime() {
        return sdf.format(time);
    }

    public String getCh4() {
        return CH4;
    }

    public String getConsumption() {
        return consumption;
    }

    public Date getDate() {
        return time;
    }
    public String getConstPower(){return constPower;}
}
