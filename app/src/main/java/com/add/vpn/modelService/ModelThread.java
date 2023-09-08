package com.add.vpn.modelService;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.R;
import com.add.vpn.roomDB.ReportItem;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.model.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ModelThread extends Thread{
    private final NotificationHelper notification;
    private final MutableLiveData<Boolean> alarm;
    private int retry = 0;
    private boolean isAlarmPlaying  = false;
    private volatile boolean interrupt = true;
    private final DataReceiver dataReceiver;
    private final DataSender dataSender;
    private final ModelRegulator regulator;
    private final AlarmReceiver alarmReceiver;
    private final Context applicationContext;
    private final MutableLiveData<List<String>> dataListLiveData;
    private final MutableLiveData<LinkedList<String>> logListLiveData;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final AlarmSound alarmSound;

    public ModelThread(MutableLiveData<List<String>> dataListLiveData,
                       MutableLiveData<LinkedList<String>> logListLiveData,
                       Context applicationContext,
                       NotificationHelper notification,
                       AlarmSound alarmSound,
                       MutableLiveData<Boolean> alarm) {
        this.dataListLiveData = dataListLiveData;
        this.logListLiveData = logListLiveData;
        this.notification = notification;
        this.alarm = alarm;

        this.dataReceiver = new DataReceiver();
        this.dataSender = new DataSender();

        this.alarmReceiver = new AlarmReceiver();
        this.applicationContext = applicationContext;
        this.alarmSound = alarmSound;
        this.regulator = new ModelRegulator(logListLiveData, applicationContext, notification, alarmSound, alarm);
    }

    @Override
    public void run() {
        while (interrupt) {
            try {
                checkInterrupt();
                receiveData();
                checkGeneratorErrors();
                Integer regulateConstant = regulator.regulate();
                if (regulateConstant != null) {
                    dataSender.setPowerConstant(regulateConstant);
                }
                Thread.sleep(1200);
            } catch (InterruptedException i) {
                interrupt = false;
            } catch (IOException e) {
                checkConnectionErrors(retry);
                retry++;
            }
        }
    }

    private void checkInterrupt() {
        if (Thread.currentThread().isInterrupted()) {
            interrupt = false;
        }
    }
    public void setInterrupt(){
        interrupt = false;
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

        dataListLiveData.postValue(DataHolder.toLis(applicationContext));

        //retry = 0;
    }

    public void checkGeneratorErrors() throws IOException, InterruptedException {
        boolean alarmReceiverAlarm = alarmReceiver.getAlarm();
        Thread.sleep(100);
        if (alarmReceiverAlarm && !isAlarmPlaying && Boolean.TRUE.equals(alarm.getValue())) {
            isAlarmPlaying = true;
            alarmSound.alarmPlay();
            notification.showNotification(applicationContext.getString(R.string.KGY_error_title), applicationContext.getString(R.string.KGY_error_msg));
        } else if (!alarmReceiverAlarm && isAlarmPlaying) {
            isAlarmPlaying = false;
        }
        //Thread.sleep(1200);
    }
    public void checkConnectionErrors(int trays) {
        if (trays == 3) {
            retry = 0;
            LinkedList<String> list = logListLiveData.getValue();
            if (list != null) {
                list.addFirst(applicationContext.getString(R.string.connection_error_log_msg, sdf.format(new Date())));
                logListLiveData.postValue(list);
            }
            if (Boolean.TRUE.equals(alarm.getValue())) alarmSound.alarmPlay();
            notification.showNotification(applicationContext.getString(R.string.connection_error_title), applicationContext.getString(R.string.connection_error_message));
        }
    }
}
