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
import com.add.vpn.firebase.FBreportItem;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelService extends Service {
    public static final String SOUND_OFF = "SOUND_OFF";
    public static final String STOP = "STOP";
    public static final String START = "START";

    //private ModelThread thread;
    private NotificationHelper notificationHelper;
    public static AlarmSound alarmSound;
    public static final MutableLiveData<Boolean> running = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<LinkedList<Float>> avgTemp = new MutableLiveData<>(new LinkedList<Float>());
    public static final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<String>(){{
        add("Loading... Please wait.");
    }});
    public static final MutableLiveData<RealtimeDatabase> realtimeDatabase = new MutableLiveData<>();
    public static final MutableLiveData<LinkedList<FBreportItem>> reportList = new MutableLiveData<>(new LinkedList<FBreportItem>(){{
//        FBreportItem fBreportItem = new FBreportItem(String.valueOf(new Date().getTime() / 1000));
//        fBreportItem.setCH4_1(10f);
//        fBreportItem.setCH4_2(20f);
//        fBreportItem.setPowerConstant(1000);
//        fBreportItem.setGasFlow(1000f);
//        add(fBreportItem);
    }});
    public static final MutableLiveData<LinkedList<String>> logListLiveData = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<Boolean> enableAlarm = new MutableLiveData<>(Boolean.FALSE);
    private Thread wrightToFirebase;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Boolean value = running.getValue();

        if (action.equals(START) && Boolean.FALSE.equals(value)) {
            running.postValue(Boolean.TRUE);

//            thread = new ModelThread(dataListLiveData,
//                    logListLiveData,
//                    getApplicationContext(),
//                    notificationHelper,
//                    alarmSound,
//                    enableAlarm);
//
//            thread.start();
            RealtimeDatabase realtimeDatabase = new RealtimeDatabase(getApplicationContext());
            realtimeDatabase.connect();
            wrightToFirebase = new Thread(() -> {
                boolean run = true;
                while (run) {
                    realtimeDatabase.wrightUnixTime();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                        run = false;
                    }
                }
            });
            wrightToFirebase.start();

        } else if (action.equals(STOP)) {
            running.setValue(Boolean.FALSE);

//            if (thread != null) {
//                thread.interrupt();
//                thread.setInterrupt();
//            }
            if (wrightToFirebase != null){
                wrightToFirebase.interrupt();
            }
            stopSelf(startId);
        } else if (action.equals(SOUND_OFF) && alarmSound != null) {
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
//        if (thread != null){
//            thread.interrupt();
//            thread.setInterrupt();
//        }
        if (wrightToFirebase != null){
            wrightToFirebase.interrupt();
        }
        running.postValue(false);
        stopSelf();
    }
}
