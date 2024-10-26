package com.add.vpn.modelService;

import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.firebase.FBreportItem;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ModelService {
    public static final String SOUND_OFF = "SOUND_OFF";
    public static final String STOP = "STOP";
    public static final String START = "START";

    //private ModelThread thread;
    public static final MutableLiveData<NotificationHelper> notificationHelper = new MutableLiveData<>();
    public static final MutableLiveData<Float> CH4kgy = new MutableLiveData<>(0F);
    public static final MutableLiveData<Long> serverUnixTime20 = new MutableLiveData<>(0L);
    public static final MutableLiveData<Boolean> regulationRunning = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<Boolean> alarmCH4Running = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<Boolean> isAccessGranted = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<LinkedList<Float>> avgTemp = new MutableLiveData<>(new LinkedList<>());
    public static final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<String>() {{
        add("Loading... Please wait.");
    }});
    public static final MutableLiveData<RealtimeDatabase> realtimeDatabase = new MutableLiveData<>();
    public static final MutableLiveData<LinkedList<FBreportItem>> reportList = new MutableLiveData<>(new LinkedList<FBreportItem>() {
    });
    public static final MutableLiveData<Boolean> enableAlarm = new MutableLiveData<>(Boolean.FALSE);
    public static final MutableLiveData<ModelThread> modelThread = new MutableLiveData<>();
    public static final MutableLiveData<AlarmCH4Thread> alarmTread = new MutableLiveData<>();
    public static AlarmSound alarmSound;
//    private Thread wrightToFirebase;
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String action = intent.getAction();
//        Boolean value = regulationRunning.getValue();
//
//        if (action.equals(START) && Boolean.FALSE.equals(value)) {
//
//            regulationRunning.setValue(true);
//
////            realtimeDatabase.postValue(new RealtimeDatabase(getApplicationContext()));
////            realtimeDatabase.getValue().connect();
//            wrightToFirebase = new Thread(() -> {
//                do {
//                    checkServer();
//                    RealtimeDatabase db = realtimeDatabase.getValue();
//                    if (db != null){
//                        db.wrightUnixTime();
//                    }
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException ignored) {
//                        regulationRunning.postValue(false);
//                    }
//                }while (regulationRunning.getValue());
//            });
//            wrightToFirebase.start();
//        } else if (action.equals(STOP)) {
//            regulationRunning.postValue(Boolean.FALSE);
//            if (wrightToFirebase != null){
//                wrightToFirebase.interrupt();
//            }
//            stopSelf(startId);
//        } else if (action.equals(SOUND_OFF) && alarmSound != null) {
//            alarmSound.alarmStop();
//        }
//        return START_REDELIVER_INTENT;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        alarmSound = new AlarmSound(getApplicationContext());
//        notificationHelper = new NotificationHelper(this);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForeground(888, notificationHelper.serviceRegulateNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
//        } else {
//            startForeground(888, notificationHelper.serviceRegulateNotification());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        alarmSound.alarmStop();
//        alarmSound.release();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        notificationHelper.regulateStopNotification();
////        if (thread != null){
////            thread.interrupt();
////            thread.setInterrupt();
////        }
//        if (wrightToFirebase != null){
//            wrightToFirebase.interrupt();
//        }
//        regulationRunning.postValue(false);
//        stopSelf();
//    }
//    public void checkServer(){
//        RealtimeDatabase database = realtimeDatabase.getValue();
//        if (database != null) {
//            database.getServerUnixTime();
//        }
//        long time = new Date().getTime()/1000;
//
//        Long serverUnixTime20sec = serverUnixTime20.getValue();
//        if (serverUnixTime20sec != null && serverUnixTime20sec == 0) serverUnixTime20sec = time;
//        boolean isServerOnline = serverUnixTime20sec == null || time - serverUnixTime20sec > 7250;
//
//        if (isServerOnline){
//
//            avgTemp.postValue(new LinkedList<>());
//            dataListLiveData.postValue(new ArrayList<String>(){{
//                add("Ошибка связи! Сервер не отвечает");
//            }});
//
//            if (Boolean.TRUE.equals(regulationRunning.getValue()) && Boolean.TRUE.equals(enableAlarm.getValue())){
//                alarmSound.alarmPlay();
//            }
//        }
//    }
}
