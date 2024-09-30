package com.add.vpn.modelService;

import androidx.fragment.app.FragmentActivity;
import com.add.vpn.NotificationHelper;
import com.add.vpn.model.AlarmSound;

import static com.add.vpn.modelService.ModelService.*;

public class AlarmCH4Thread extends Thread {
    private final NotificationHelper notificationHelper;
    private final int notification_id = 775;
    //private final AlarmSound alarmSound;

    public AlarmCH4Thread(FragmentActivity fragmentActivity) {
        alarmCH4Running.setValue(true);
        if (alarmSound == null) alarmSound = new AlarmSound(fragmentActivity);
        notificationHelper = new NotificationHelper(fragmentActivity);
        notificationHelper.alarmCH4StartNotification();
    }

    @Override
    public void run() {
        alarmCH4Running.postValue(true);
        do {
            if (CH4kgy.getValue() != null && CH4kgy.getValue() > 26.4) {
                alarmSound.alarmPlay();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
                alarmCH4Running.postValue(false);
                alarmSound.alarmStop();
                notificationHelper.alarmCH4StopNotification();
            }
        } while (Boolean.TRUE.equals(alarmCH4Running.getValue()));
    }
}