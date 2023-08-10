package com.add.vpn.model;

import android.app.Activity;

import com.add.vpn.NotificationHelper;
import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.holders.ContextHolder;
import com.add.vpn.holders.DataHolder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class Model extends Thread {
    private final DataReceiver dataReceiver;
    private final DataSender dataSender;
    private final Regulator regulator;
    private final AlarmReceiver alarmReceiver;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private int retry = 0;
    private boolean interrupt = true;

    public Model(LinkedList<String> logList) {
        this.dataReceiver = new DataReceiver();
        this.dataSender = new DataSender();
        this.regulator = new Regulator(logList);
        this.alarmReceiver = new AlarmReceiver();

    }

    @Override
    public void run() {
        while (interrupt) {
            try {
                getInterrupt();
                receiveData();
                receiveAlarm();
                Integer regulateConstant = regulator.regulate();
                if (regulateConstant != null) {
                    dataSender.setPowerConstant(regulateConstant);
                }
                Thread.sleep(200);
                dataChange();
            } catch (InterruptedException i) {
                //logList.add("Interrupt model");
                interrupt = false;
            } catch (IOException e) {
                Throwable cause = e.getCause();
                String causeMessage = "unknown error";
                if (cause != null) {
                    causeMessage = cause.getMessage();
                }
                playAlarmSound(causeMessage, retry);
                retry++;
            }
        }
    }

    private void getInterrupt() {
        if (Thread.currentThread().isInterrupted()) {
            interrupt = false;
            //ContextHolder.setModel(null);
        }
    }

    public void playAlarmSound(String exception, int retry) {
        // Получаем URI выбранной мелодии будильника
        if (retry == 3) {
            //interrupt = false;
            LinkedList<String> log = ContextHolder.getLogList();
            if (log != null) {
                log.addFirst(sdf.format(new Date()) + " " + exception);
            }
            AlarmSound sound = ContextHolder.getAlarmSound();
            if (sound != null) {
                sound.alarmPlay();
            }
            dataChange();
        }
    }

    private void dataChange() {
        Activity activity = ContextHolder.getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> {
                DataAdapter adapter = ContextHolder.getDataAdapter();
                if (adapter != null) adapter.notifyDataSetChanged();
                LogAdapter adapter1 = ContextHolder.getLogAdapter();
                if (adapter1 != null) adapter1.notifyDataSetChanged();
            });
        }
    }

    public void receiveData() throws IOException, InterruptedException {
        Thread.sleep(100);
        DataHolder.setThrottlePosition(dataReceiver.getThrottlePosition());
        Thread.sleep(100);
        DataHolder.setOpPressure(dataReceiver.getOpPressure());
        Thread.sleep(100);
        DataHolder.setActPower(dataReceiver.getPowerActive());
        Thread.sleep(100);
        DataHolder.setConstPower(dataReceiver.getPowerConstant());
        Thread.sleep(100);
        DataHolder.setCH4Concentration(dataReceiver.getCH4Concentration());
        Thread.sleep(100);
        retry = 0;
    }

    public void receiveAlarm() throws IOException, InterruptedException {
        boolean alarm = alarmReceiver.getAlarm();
        Thread.sleep(100);
        if (alarm) {
            AlarmSound errorSound = ContextHolder.getErrorSound();
            NotificationHelper notificationHelper = ContextHolder.getNotificationHelper();
            if (notificationHelper != null){
                notificationHelper.showNotification("Ошибка КГУ", "Обнаружена ошибка КГУ требующая вашего внимания");
            }
            if (errorSound != null) {
                errorSound.alarmPlay();
            }
        }
    }
}