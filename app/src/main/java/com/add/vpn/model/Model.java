//package com.add.vpn.model;
//
//import android.content.Context;
//import com.add.vpn.R;
//import com.add.vpn.holders.DataHolder;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class Model extends Thread {
//    private final DataReceiver dataReceiver;
//    private final DataSender dataSender;
//    private final Regulator regulator;
//    private final AlarmReceiver alarmReceiver;
//    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
//    private final Context applicationContext;
//    private int retry = 0;
//    private volatile boolean interrupt = true;
//    private final DataViewModel dataViewModel;
//    private boolean isAlarmPlaying  = false;
//
//    public Model(DataViewModel dataViewModel, Context applicationContext) {
//        this.dataViewModel = dataViewModel;
//        this.dataReceiver = new DataReceiver();
//        this.dataSender = new DataSender();
//        this.regulator = new Regulator(dataViewModel , applicationContext);
//        this.alarmReceiver = new AlarmReceiver();
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void run() {
//        while (interrupt) {
//            try {
//                checkInterrupt();
//                receiveData();
//                checkGeneratorErrors();
//                Integer regulateConstant = regulator.regulate();
//                if (regulateConstant != null) {
//                    dataSender.setPowerConstant(regulateConstant);
//                }
//                Thread.sleep(1200);
//            } catch (InterruptedException i) {
//                interrupt = false;
//            } catch (IOException e) {
//                checkConnectionErrors(retry);
//                retry++;
//            }
//        }
//    }
//
//    private void checkInterrupt() {
//        if (Thread.currentThread().isInterrupted()) {
//            interrupt = false;
//        }
//    }
//    public void setInterrupt(){
//        interrupt = false;
//    }
//
//
//
//    public void receiveData() throws IOException, InterruptedException {
//
//        Thread.sleep(100);
//
//        DataHolder.setThrottlePosition(dataReceiver.getThrottlePosition());
//        Thread.sleep(100);
//
//        DataHolder.setOpPressure(dataReceiver.getOpPressure());
//        Thread.sleep(100);
//
//        DataHolder.setActPower(dataReceiver.getPowerActive());
//        Thread.sleep(100);
//
//        DataHolder.setConstPower(dataReceiver.getPowerConstant());
//        Thread.sleep(100);
//
//        DataHolder.setCH4Concentration(dataReceiver.getCH4Concentration());
//        Thread.sleep(100);
//
//        dataViewModel.setDataList(DataHolder.toLis(applicationContext));
//
//        retry = 0;
//    }
//
//    public void checkGeneratorErrors() throws IOException, InterruptedException {
//        boolean alarmReceiverAlarm = alarmReceiver.getAlarm();
//        Thread.sleep(100);
//        if (alarmReceiverAlarm && !isAlarmPlaying) {
//            isAlarmPlaying = true;
//            dataViewModel.playErrorSound(applicationContext.getString(R.string.KGY_error_title), applicationContext.getString(R.string.KGY_error_msg));
//        } else if (!alarmReceiverAlarm && isAlarmPlaying) {
//            isAlarmPlaying = false;
//        }
//    }
//    public void checkConnectionErrors(int retry) {
//        if (retry == 3) {
//
//            dataViewModel.addToLogList(applicationContext.getString(R.string.connection_error_log_msg, sdf.format(new Date())));
//            dataViewModel.playAlarmSound();
//        }
//    }
//}