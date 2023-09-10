package com.add.vpn.roomDB;

import androidx.room.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
@Entity(tableName = "report")
public class ReportItem {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "date")
    @TypeConverters(DateConverter.class)
    private final Date time;
    @ColumnInfo(name = "CH4_concentration")
    private final String CH4;
    @ColumnInfo(name = "gas_consumption")
    private final String consumption;
    @ColumnInfo(name = "power_constant")
    private final String constPower;
    @ColumnInfo(name = "date_string")
    private String timeString;

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public ReportItem(Date time, String CH4, String consumption, String constPower) {
        this.time = time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        this.timeString = sdf.format(time);
        this.CH4 = CH4;
        this.consumption = consumption;
        this.constPower = constPower;
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

    public Date getTime() {
        return time;
    }
    public String getConstPower(){return constPower;}
}
