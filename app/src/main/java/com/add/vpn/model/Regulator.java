package com.add.vpn.model;

import com.add.vpn.NotificationHelper;
import com.add.vpn.holders.ContextHolder;
import com.add.vpn.holders.DataHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class Regulator {
    private final LinkedList<String> logList;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public int maxPower = 1550;
    public int regulatePower = 10;
    private long now = new Date().getTime();
    private double opPressure = 0d;
    private short actPower = 0;
    private short constPower = 0;
    private float throttlePosition = 0;

    public Regulator(LinkedList<String> logList) {
        this.logList = logList;

    }

    public Integer regulate() {
        getData();
        if (actPower > 0 && (new Date().getTime() - now) >= 20_000) {

            regulatePower();
            LinkedList<String> list = ContextHolder.getLogList();

            if (opPressure < 3) {
                if (list != null)list.addFirst(sdf.format(now) + " снижение мощности до " + (constPower - 100) + "кВт");
                now = new Date().getTime();
                return  constPower > 1000 ? (constPower - 100) : 900;
            } else if (opPressure < 4 || throttlePosition > 90 || actPower > 1560 || constPower > maxPower) {
                if (list != null)list.addFirst(sdf.format(now) + " снижение мощности до " + (constPower - regulatePower) + "кВт");
                now = new Date().getTime();
                checkActPower();
                checkThrottle();
                return  (constPower - regulatePower);
            } else if (opPressure > 5 && (constPower + regulatePower) < maxPower && (constPower - actPower) <= 50 && throttlePosition < 90) {
                if (list != null)list.addFirst(sdf.format(now) + " повышение мощности до " + (constPower + regulatePower) + "кВт");
                now = new Date().getTime();
                return  (constPower + regulatePower);
            } else if (opPressure > 5 && throttlePosition < 80 && maxPower < 1500) {
                DataHolder.setMaxPower(maxPower + 10);
                if (list != null)list.addFirst(sdf.format(now) + " повышение макс.мощности до " + (maxPower + 10) + "кВт");
                now = new Date().getTime();
            }
        } else if (actPower <= 0 && constPower != 900){
            DataHolder.setMaxPower(1540);
            logList.addFirst(sdf.format(now) + " остановка");
            AlarmSound errorSound = ContextHolder.getErrorSound();
            if (errorSound != null) errorSound.alarmPlay();
            NotificationHelper notificationHelper = ContextHolder.getNotificationHelper();
            if (notificationHelper != null) notificationHelper.showNotification("Остановка!", "КГУ остановлена, так и задуманно?");
            return 900;
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
            DataHolder.setMaxPower(constPower - 10);
            logList.addFirst(sdf.format(now) + " снижение макс.мощности по активной мощности до " + DataHolder.getMaxPower() + "кВт");
        }
    }
    private void checkThrottle(){
        if (throttlePosition > 90 && opPressure > 3){
            DataHolder.setMaxPower(constPower - 10);
            logList.addFirst(sdf.format(now) + " снижение макс.мощности по дросселю до " + DataHolder.getMaxPower() + "кВт");
        }
    }
    private void getData(){
        actPower = DataHolder.getActPower();
        constPower = DataHolder.getConstPower();
        throttlePosition = DataHolder.getThrottlePosition();
        opPressure = DataHolder.getOpPressure();
        maxPower = DataHolder.getMaxPower();
    }

}
