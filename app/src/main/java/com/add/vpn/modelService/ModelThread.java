package com.add.vpn.modelService;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import static com.add.vpn.modelService.ModelService.*;

public class ModelThread extends Thread {
    private final MutableLiveData<NotificationHelper> notifier = ModelService.notificationHelper;

    private int notification_id = 777;
    //private final AlarmSound alarmSound;

    public ModelThread(FragmentActivity fragmentActivity) {
        if (alarmSound == null) alarmSound = new AlarmSound(fragmentActivity);

        if (notifier.getValue() == null){
            ModelService.notificationHelper.setValue(new NotificationHelper(fragmentActivity));
            notifier.getValue().regulateStartNotification();
        }else{
            notifier.getValue().regulateStartNotification();
        }
        //notificationHelper = new NotificationHelper(fragmentActivity);
        //regulationRunning.setValue(true);
    }

    @Override
    public void run() {
        //regulationRunning.postValue(true);
        do {
            checkServer();
            RealtimeDatabase db = realtimeDatabase.getValue();
            if (db != null) {
                db.wrightUnixTime();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
                //notificationHelper.notificationManager.cancel(notification_id);


                if (notifier.getValue() != null) {
                    notifier.getValue().regulateStopNotification();
                }
                alarmSound.alarmStop();
                regulationRunning.postValue(false);
                //alarmSound.release();
            }
        } while (Boolean.TRUE.equals(regulationRunning.getValue()));
//        regulationRunning.postValue(false);
//        notificationHelper.serviceStopNotification();
//        notificationHelper.notificationManager.cancel(notification_id);
//        alarmSound.alarmStop();
//        //alarmSound.release();
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


