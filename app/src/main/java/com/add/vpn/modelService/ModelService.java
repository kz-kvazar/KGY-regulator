package com.add.vpn.modelService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.R;
import com.add.vpn.firebase.FBreportItem;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ModelService extends Service {
    public static final String SOUND_OFF = "SOUND_OFF";
    public static final String STOP = "STOP";
    public static final String START = "START";

    //private ModelThread thread;
    private NotificationHelper notificationHelper;
    public static AlarmSound alarmSound;
    //public static final MutableLiveData<Float> CH4kgy = new MutableLiveData<>(0F);
    public static final MutableLiveData<Long> serverUnixTime20 = new MutableLiveData<>(0L);
    public static final MutableLiveData<Boolean> running = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<Boolean> isAccessGranted = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<LinkedList<Float>> avgTemp = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<String>(){{
        add("Loading... Please wait.");
    }});
    public static final MutableLiveData<RealtimeDatabase> realtimeDatabase = new MutableLiveData<>();
    public static final MutableLiveData<LinkedList<FBreportItem>> reportList = new MutableLiveData<>(new LinkedList<FBreportItem>(){
    });
    public static final MutableLiveData<Boolean> enableAlarm = new MutableLiveData<>(Boolean.FALSE);
    private Thread wrightToFirebase;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Boolean value = running.getValue();

        if (action.equals(START) && Boolean.FALSE.equals(value)) {

            running.setValue(true);

//            realtimeDatabase.postValue(new RealtimeDatabase(getApplicationContext()));
//            realtimeDatabase.getValue().connect();
            wrightToFirebase = new Thread(() -> {
                do {
                    checkServer();
                    RealtimeDatabase db = realtimeDatabase.getValue();
                    if (db != null){
                        db.wrightUnixTime();
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                        running.postValue(false);
                    }
                }while (running.getValue());
            });
            wrightToFirebase.start();
        } else if (action.equals(STOP)) {
            running.postValue(Boolean.FALSE);
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
            startForeground(888, notificationHelper.serviceRegulateNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(888, notificationHelper.serviceRegulateNotification());
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
    public void checkServer(){
        RealtimeDatabase database = realtimeDatabase.getValue();
        if (database != null) {
            database.getServerUnixTime();
        }
        long time = new Date().getTime()/1000;

        Long serverUnixTime20sec = serverUnixTime20.getValue();
        if (serverUnixTime20sec != null && serverUnixTime20sec == 0) serverUnixTime20sec = time;
        boolean isServerOnline = serverUnixTime20sec == null || time - serverUnixTime20sec > 7250;

        if (isServerOnline){

            avgTemp.postValue(new LinkedList<>());
            dataListLiveData.postValue(new ArrayList<String>(){{
                add("Ошибка связи! Сервер не отвечает");
            }});

            if (Boolean.TRUE.equals(running.getValue()) && Boolean.TRUE.equals(enableAlarm.getValue())){
                alarmSound.alarmPlay();
            }
        }
    }
}
