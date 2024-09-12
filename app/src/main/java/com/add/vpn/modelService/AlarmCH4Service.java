package com.add.vpn.modelService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.model.AlarmSound;

public class AlarmCH4Service extends Service {
    public static final MutableLiveData<Boolean> running = new MutableLiveData<>(Boolean.FALSE);
    public static final String STOP = "STOP";
    public static final String START = "START";
    public static MutableLiveData<Float> ch4KGY = ModelService.CH4kgy;
    private Thread checker;
    public static AlarmSound alarmCH4;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Boolean value = running.getValue();

        if (action.equals(START) && Boolean.FALSE.equals(value)) {
            alarmCH4 = new AlarmSound(getApplication());
            running.postValue(Boolean.TRUE);
            checker = new Thread(() -> {
                while (true) {
                    if (ch4KGY.getValue() != null && ch4KGY.getValue() > 26.4) {
                        alarmCH4.alarmPlay();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            });
            checker.start();
        } else if (action.equals(STOP)) {
            running.postValue(Boolean.FALSE);
            if (checker != null) {
                checker.interrupt();
            }
            if (alarmCH4 != null) {
                alarmCH4.alarmStop();
            }
            running.postValue(false);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper notificationHelper = new NotificationHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(999, notificationHelper.serviceCH4Notification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(999, notificationHelper.serviceCH4Notification());
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        if (checker != null) {
            checker.interrupt();
        }
        if (alarmCH4 != null) {
            alarmCH4.alarmStop();
        }
        running.postValue(false);
        stopSelf();
    }
}
