package com.add.vpn.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
@Entity(tableName = "report")
public class ReportItem {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @Ignore
    private Date time;
    @ColumnInfo(name = "CH4_concentration")
    private final String CH4;
    @ColumnInfo(name = "gas_consumption")
    private final String consumption;
    @Ignore
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    @ColumnInfo(name = "power_constant")
    private final String constPower;
    @ColumnInfo(name = "time_and_date")

    private final String timeString;


    public ReportItem(Date time, String CH4, String consumption, String constPower) {
        this.time = time;
        this.timeString = sdf.format(time);
        this.CH4 = CH4;
        this.consumption = consumption;
        this.constPower = constPower;
    }

    public ReportItem(String CH4, String consumption, String constPower, String timeString) {
        this.CH4 = CH4;
        this.consumption = consumption;
        this.constPower = constPower;
        this.timeString = timeString;
        try {
            this.time = sdf.parse(timeString);
        } catch (ParseException ignored) {
        }
    }

    public String getTimeString() {
        return timeString;
    }

    public String getCH4() {
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
