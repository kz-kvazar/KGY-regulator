package com.add.vpn.modelService;

import android.app.Notification;
import androidx.fragment.app.FragmentActivity;
import com.add.vpn.NotificationHelper;
import com.add.vpn.model.AlarmSound;

import static com.add.vpn.modelService.ModelService.*;

public class AlarmCH4Thread extends Thread {
    private final NotificationHelper notificationHelper;
    private final int notification_id = 775;
    //private final AlarmSound alarmSound;

    public AlarmCH4Thread(FragmentActivity fragmentActivity) {
        notificationHelper = new NotificationHelper(fragmentActivity);
        Notification notification = notificationHelper.serviceCH4Notification();
        notificationHelper.notificationManager.notify(notification_id, notification);
        if (alarmSound == null) alarmSound = new AlarmSound(fragmentActivity);
        alarmCH4Running.setValue(true);
    }

    @Override
    public void run() {
        regulationRunning.postValue(true);
        do {
            if (CH4kgy.getValue() != null && CH4kgy.getValue() > 26.4) {
                alarmSound.alarmPlay();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
                alarmCH4Running.postValue(false);
            }
        } while (Boolean.TRUE.equals(alarmCH4Running.getValue()));
        notificationHelper.notificationManager.cancel(notification_id);
        alarmSound.alarmStop();
        alarmSound.release();
        alarmCH4Running.postValue(false);
    }
}
