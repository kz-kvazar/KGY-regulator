package com.add.vpn.modelService;

import android.app.Notification;
import androidx.fragment.app.FragmentActivity;
import com.add.vpn.NotificationHelper;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import static com.add.vpn.modelService.ModelService.*;

public class ModelThread extends Thread {
    private final NotificationHelper notificationHelper;
    private final int notification_id = 778;
    //private final AlarmSound alarmSound;

    public ModelThread(FragmentActivity fragmentActivity) {
        if (alarmSound == null) alarmSound = new AlarmSound(fragmentActivity);
        notificationHelper = new NotificationHelper(fragmentActivity);
        Notification notification = notificationHelper.serviceRegulateNotification();
        notificationHelper.notificationManager.notify(notification_id, notification);
        regulationRunning.setValue(true);
    }

    @Override
    public void run() {
        regulationRunning.postValue(true);
        do {
            checkServer();
            RealtimeDatabase db = realtimeDatabase.getValue();
            if (db != null) {
                db.wrightUnixTime();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
                regulationRunning.postValue(false);
            }
        } while (Boolean.TRUE.equals(regulationRunning.getValue()));
        notificationHelper.serviceStopNotification();
        notificationHelper.notificationManager.cancel(notification_id);
        alarmSound.alarmStop();
        alarmSound.release();
        regulationRunning.postValue(false);
    }

    public void checkServer() {
        RealtimeDatabase database = realtimeDatabase.getValue();
        if (database != null) {
            database.getServerUnixTime();
        }
        long time = new Date().getTime() / 1000;

        Long serverUnixTime20sec = serverUnixTime20.getValue();
        if (serverUnixTime20sec != null && serverUnixTime20sec == 0) serverUnixTime20sec = time;
        boolean isServerOnline = serverUnixTime20sec == null || time - serverUnixTime20sec > 7250;

        if (isServerOnline) {

            avgTemp.postValue(new LinkedList<>());
            dataListLiveData.postValue(new ArrayList<String>() {{
                add("Ошибка связи! Сервер не отвечает");
            }});

            if (Boolean.TRUE.equals(regulationRunning.getValue()) && Boolean.TRUE.equals(enableAlarm.getValue())) {
                alarmSound.alarmPlay();
            }
        }
    }

}


