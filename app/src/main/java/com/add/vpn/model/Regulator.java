package com.add.vpn.model;

import com.add.vpn.adapters.ReportItem;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.holders.DataViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class Regulator {
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final DataViewModel dataViewModel;
    public int userMaxPower = 1560;
    private int appMaxPower = 1560;
    public int regulatePower = 10;
    private long now = new Date().getTime();
    private double opPressure = 0d;
    private short actPower = 0;
    private short constPower = 0;
    private float throttlePosition = 0;

    public Regulator(DataViewModel dataViewModel) {
        this.dataViewModel = dataViewModel;
        dataViewModel.addToLogList(sdf.format(now) + " Запуск регулирования");
    }

    public Integer regulate() {
        getData();
        regulatePower();
        checkMaxPower();

        Date date = new Date();
        LinkedList<ReportItem> value = dataViewModel.getReportListLiveData().getValue();
        if (value != null && value.isEmpty() || (date.getHours() != value.getFirst().getDate().getHours())) {
            dataViewModel.addToReportList(new ReportItem(date,
                    DataHolder.getCH4Concentration().toString(),
                    DataHolder.getGasFlow().toString()));
        }


        if (actPower > 0 && (date.getTime() - now) >= 20_000) {
            // TODO remove the hardcoding strings

            if (opPressure < 3) {
                dataViewModel.addToLogList(sdf.format(now) + " снижение мощности по аварийно низкому давлению до " + (constPower - 100) + "кВт");
                now = date.getTime();
                return  constPower > 1000 ? (constPower - 100) : 900;
            } else if (opPressure < 4 || throttlePosition > 90 || actPower > 1560 || constPower > appMaxPower) {
                dataViewModel.addToLogList(sdf.format(now) + " снижение мощности " + (constPower - regulatePower) + "кВт");
                now = date.getTime();
                checkActPower();
                checkThrottle();
                return  (constPower - regulatePower);
            } else if (opPressure > 5 && (constPower + regulatePower) < appMaxPower && (constPower - actPower) <= 50 && throttlePosition < 90) {
                dataViewModel.addToLogList(sdf.format(now) + " повышение мощности до " + (constPower + regulatePower) + "кВт");
                now = date.getTime();
                return  (constPower + regulatePower);
            } else if (opPressure > 5 && throttlePosition < 80 && (userMaxPower - appMaxPower) >= 10 && (date.getTime() - now) > 300_000)  { // 5 min
                appMaxPower += 10;
                dataViewModel.addToLogList(sdf.format(now) + " повышение границы регулирования мощности до " + appMaxPower + "кВт");
                now = date.getTime();
            }
        } else if (actPower <= 0 && constPower != 800){
            DataHolder.setMaxPower(1520);
            appMaxPower = 1520;
            dataViewModel.addToLogList(sdf.format(now) + " КГУ остановлена");
            dataViewModel.playErrorSound("Остановка!", "КГУ остановлена, так и задумано?");
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
            dataViewModel.addToLogList(sdf.format(now) + " снижение границы регулирования мощности по активной мощности до " + appMaxPower + "кВт");
        }
    }
    private void checkThrottle(){
        if (throttlePosition > 90 && opPressure > 3){
            appMaxPower = (constPower - 10);
            dataViewModel.addToLogList(sdf.format(now) + " снижение границы регулирования мощности по дросселю до " + appMaxPower + "кВт");
        }
    }
    private void checkMaxPower(){
        if (userMaxPower < appMaxPower){
            appMaxPower = userMaxPower;
            dataViewModel.addToLogList(sdf.format(now) + " изменение границы регулирования мощности по запросу пользователя до " + appMaxPower + "кВт");
        }
    }
    private void getData(){
        actPower = DataHolder.getActPower();
        constPower = DataHolder.getConstPower();
        throttlePosition = DataHolder.getThrottlePosition();
        opPressure = DataHolder.getOpPressure();
        userMaxPower = DataHolder.getMaxPower();
    }
}
