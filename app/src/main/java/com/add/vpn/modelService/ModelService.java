package com.add.vpn.modelService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.adapters.ReportItem;
import com.add.vpn.model.AlarmSound;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ModelService extends Service implements DefaultLifecycleObserver {
    private ModelThread thread;
    public static final MutableLiveData<Boolean> running = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<>());
    public static final MutableLiveData<LinkedList<String>> logListLiveData = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<LinkedList<ReportItem>> reportListLiveData = new MutableLiveData<>(new LinkedList<>());
    private NotificationHelper notificationHelper;
    public static AlarmSound alarmSound;
    public static MutableLiveData<Boolean> alarm = new MutableLiveData<>(Boolean.FALSE);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Boolean value = running.getValue();
        if (action.equals("START")  && Boolean.FALSE.equals(value)){

            running.postValue(Boolean.TRUE);

            thread = new ModelThread(dataListLiveData,
                    logListLiveData,
                    reportListLiveData,
                    getApplicationContext(),
                    notificationHelper,
                    alarmSound,
                    alarm);

            thread.start();

        } else if (action.equals("STOP") ) {

            running.setValue(Boolean.FALSE);

            if (thread != null) {
                thread.interrupt();
                thread.setInterrupt();
            }

            stopSelf(startId);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmSound = new AlarmSound(getApplicationContext());
        notificationHelper = new NotificationHelper(this);
        startForeground(888, notificationHelper.serviceNotification());
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
}
