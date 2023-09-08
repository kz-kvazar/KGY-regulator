package com.add.vpn.model;

import android.content.Context;
import com.add.vpn.R;
import com.add.vpn.roomDB.ReportItem;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.holders.DataViewModel;

import java.text.SimpleDateFormat;
import java.util.*;

public class Regulator {
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final DataViewModel dataViewModel;
    private final Context applicationContext;
    public int userMaxPower = 1560;
    private int appMaxPower = 1560;
    public int regulatePower = 10;
    private long lastTimeRegulated = new Date().getTime();
    private double opPressure = 0d;
    private short actPower = 0;
    private short constPower = 0;
    private float throttlePosition = 0;

    public Regulator(DataViewModel dataViewModel, Context applicationContext) {
        this.dataViewModel = dataViewModel;
        this.applicationContext = applicationContext;
        dataViewModel.addToLogList(applicationContext.getString(R.string.KGY_start,sdf.format(lastTimeRegulated)));
    }

    public Integer regulate() {
        getData();
        regulatePower();
        checkMaxPower();

        Date now = new Date();
        List<ReportItem> value = dataViewModel.getReportListLiveData().getValue();

        if ((value != null && value.isEmpty()) || (value != null && isNotSameHour(now, value.get(value.size()-1).getDate()))) {
            dataViewModel.addToReportList(new ReportItem(now,
                    DataHolder.getCH4Concentration().toString(),
                    DataHolder.getGasFlow().toString(),String.valueOf(DataHolder.getConstPower())));
        }

        if (actPower > 0 && (now.getTime() - this.lastTimeRegulated) >= 20_000) {

            if (opPressure < 3) {
                dataViewModel.addToLogList(applicationContext.getString(R.string.power_down, sdf.format(this.lastTimeRegulated), constPower - 100));
                this.lastTimeRegulated = now.getTime();
                return  constPower > 1000 ? (constPower - 100) : 900;
            } else if (opPressure < 4 || throttlePosition > 90 || actPower > 1560 || constPower > appMaxPower) {
                dataViewModel.addToLogList(applicationContext.getString(R.string.power_down,sdf.format(this.lastTimeRegulated), (constPower - regulatePower)));
                this.lastTimeRegulated = now.getTime();
                checkActPower();
                checkThrottle();
                return  (constPower - regulatePower);
            } else if (opPressure > 5 && (constPower + regulatePower) < appMaxPower && (constPower - actPower) <= 50 && throttlePosition < 90) {
                dataViewModel.addToLogList(applicationContext.getString(R.string.power_up, sdf.format(this.lastTimeRegulated), constPower + regulatePower));
                this.lastTimeRegulated = now.getTime();
                return  (constPower + regulatePower);
            } else if (opPressure > 5 && throttlePosition < 80 && (userMaxPower - appMaxPower) >= 10 && (now.getTime() - this.lastTimeRegulated) > 300_000)  { // 5 min
                appMaxPower += 10;
                dataViewModel.addToLogList(applicationContext.getString(R.string.power_max, sdf.format(this.lastTimeRegulated), appMaxPower));
                this.lastTimeRegulated = now.getTime();
            }
        } else if (actPower <= 0 && constPower != 800){
            DataHolder.setMaxPower(1560);
            appMaxPower = 1560;
            dataViewModel.addToLogList(applicationContext.getString(R.string.KGY_stop, sdf.format(this.lastTimeRegulated)));
            dataViewModel.playErrorSound(applicationContext.getString(R.string.app_name), applicationContext.getString(R.string.stop_notification));
            return 800;
        }
        return null;
    }
    private void regulatePower() {
        if (opPressure > 7 && actPower < 1350) {
            if (regulatePower != 20) regulatePower = 20;
        } else {
            if (regulatePower != 10) regulatePower = 10;
        }
    }
    private void checkActPower() {
        if (actPower > 1560) {
            appMaxPower = (constPower - 10);
            dataViewModel.addToLogList(applicationContext.getString(R.string.power_max, sdf.format(lastTimeRegulated), appMaxPower));
        }
    }
    private void checkThrottle(){
        if (throttlePosition > 90 && opPressure > 3){
            appMaxPower = (constPower - 10);
            dataViewModel.addToLogList(applicationContext.getString(R.string.power_max, sdf.format(lastTimeRegulated), appMaxPower));
        }
    }
    private void checkMaxPower(){
        if (userMaxPower < appMaxPower){
            appMaxPower = userMaxPower;
            dataViewModel.addToLogList(applicationContext.getString(R.string.power_max, sdf.format(lastTimeRegulated), appMaxPower));
        }
    }
    private boolean isNotSameHour(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.HOUR_OF_DAY) != cal2.get(Calendar.HOUR_OF_DAY);
    }
    private void getData(){
        actPower = DataHolder.getActPower();
        constPower = DataHolder.getConstPower();
        throttlePosition = DataHolder.getThrottlePosition();
        opPressure = DataHolder.getOpPressure();
        userMaxPower = DataHolder.getMaxPower();
    }
}
