package com.add.vpn.modelService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.R;
import com.add.vpn.adapters.ReportItem;
import com.add.vpn.model.AlarmSound;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelService extends Service {
    public static final String SOUND_OFF = "SOUND_OFF";
    public static final String STOP = "STOP";
    public static final String START = "START";

    private ModelThread thread;
    private NotificationHelper notificationHelper;
    public static AlarmSound alarmSound;

    public static final MutableLiveData<Boolean> running = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<>());
    public static final MutableLiveData<LinkedList<String>> logListLiveData = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<LinkedList<ReportItem>> reportListLiveData = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<Boolean> enableAlarm = new MutableLiveData<>(Boolean.FALSE);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Boolean value = running.getValue();

        if (action.equals(START) && Boolean.FALSE.equals(value)) {
            running.postValue(Boolean.TRUE);

            thread = new ModelThread(dataListLiveData,
                    logListLiveData,
                    reportListLiveData,
                    getApplicationContext(),
                    notificationHelper,
                    alarmSound,
                    enableAlarm);

            thread.start();

        } else if (action.equals(STOP)) {
            running.setValue(Boolean.FALSE);

            if (thread != null) {
                thread.interrupt();
                thread.setInterrupt();
            }
            stopSelf(startId);
        } else if (action.equals(SOUND_OFF)) {
            alarmSound.alarmStop();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmSound = new AlarmSound(getApplicationContext());
        notificationHelper = new NotificationHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(888, notificationHelper.serviceNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(888, notificationHelper.serviceNotification());
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        alarmSound.alarmStop();
        alarmSound.release();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notificationHelper.showNotification(getString(R.string.app_name), getString(R.string.backgrounded_message));
        thread.interrupt();
        thread.setInterrupt();
        running.postValue(false);
        stopSelf();
    }
}
