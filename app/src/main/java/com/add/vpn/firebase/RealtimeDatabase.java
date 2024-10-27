package com.add.vpn.firebase;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.add.vpn.R;
import com.add.vpn.SettingsManager;
import com.add.vpn.UtilCalculations;
import com.add.vpn.modelService.ModelService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class RealtimeDatabase {
    private final Context context;
    ValueEventListener eventListener;
    ValueEventListener reportListener;
    ValueEventListener avgTempListener;
    DatabaseReference databaseReference;
    private Boolean stopAlarm = false;
    private Boolean isServerOnline = true;
    private Long serverUnixTime20 = 0L;
    private Long time = 0L;

    public RealtimeDatabase(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context.getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kgy-regulator-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public void isAccessGranted(String uid) {
        DatabaseReference reference = databaseReference.child("AccessGrantedUsers");

        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && Boolean.FALSE.equals(ModelService.isAccessGranted.getValue())) {
                    ModelService.isAccessGranted.postValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
            }
        });
    }

    public void connect() {
        Handler handler = new Handler(Looper.getMainLooper());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> arrayList = new ArrayList<>();

                serverUnixTime20 = dataSnapshot.child("serverUnixTime20").getValue(Long.class);


                if (serverUnixTime20 == null || serverUnixTime20.equals(SettingsManager.getServerUnixTime(context))) {
                    isServerOnline = true;
                } else {
                    SettingsManager.setServerUnixTime(context, serverUnixTime20);
                    isServerOnline = false;
                    time = (new Date().getTime() / 1000) - serverUnixTime20;
                }


                if (!isServerOnline) {

                    handler.postDelayed(() -> {
//                        Toast.makeText(context, "new time ="+(((new Date().getTime() / 1000) - serverUnixTime20)), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "old time ="+time, Toast.LENGTH_SHORT).show();
                        if (((new Date().getTime() / 1000) - serverUnixTime20) > 45 + time) {
                            ModelService.dataListLiveData.postValue(new ArrayList<String>() {{
                                add(context.getString(R.string.connection_error_message));
                            }});
                            ModelService.avgTemp.postValue(new LinkedList<>());
                        }
                    }, 60000);

                    arrayList.add(context.getString(R.string.op_Pressure, String.valueOf(dataSnapshot.child("opPresher").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.gts_pressure, String.valueOf(dataSnapshot.child("gtsPresher").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.kgy_pressure, String.valueOf(dataSnapshot.child("kgyPresher").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.throttlePosition, String.valueOf(dataSnapshot.child("trottlePosition").getValue(Integer.class))));
                    Integer powerActive = dataSnapshot.child("powerActive").getValue(Integer.class);
                    arrayList.add(context.getString(R.string.act_Power, String.valueOf(powerActive)));
                    arrayList.add(context.getString(R.string.const_Power, String.valueOf(dataSnapshot.child("powerConstant").getValue(Integer.class))));
                    arrayList.add(context.getString(R.string.max_power, String.valueOf(dataSnapshot.child("MaxPower").getValue(Integer.class))));
                    Double ch4_1 = dataSnapshot.child("CH4_1").getValue(Double.class);
                    Double ch4_2 = dataSnapshot.child("CH4_2").getValue(Double.class);
                    arrayList.add(context.getString(R.string.VNS, String.valueOf(ch4_1), String.valueOf(ch4_2)));
                    Float ch4Kgy = dataSnapshot.child("CH4_KGY").getValue(Float.class);

                    arrayList.add(context.getString(R.string.ch4_kgy, String.valueOf(ch4Kgy)));
                    arrayList.add(context.getString(R.string.gas_Flow, String.valueOf(UtilCalculations.calculateGasFlow(Float.valueOf(String.valueOf(ch4_1)), Float.valueOf(String.valueOf(ch4_2)), powerActive))));
                    arrayList.add(context.getString(R.string.gasTempIN, String.valueOf(dataSnapshot.child("gasTempIN").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.gasTempOUT, String.valueOf(dataSnapshot.child("gasTempOUT").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.cleanOil, String.valueOf(dataSnapshot.child("cleanOil").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.resTemp, String.valueOf(dataSnapshot.child("resTemp").getValue(Double.class))));
                    arrayList.add(context.getString(R.string.avgTemp, String.valueOf(dataSnapshot.child("avgTemp").getValue(Double.class))));
                    String bearing1Temp = String.valueOf(dataSnapshot.child("bearing1Temp").getValue(Double.class));
                    String bearing2Temp = String.valueOf(dataSnapshot.child("bearing2Temp").getValue(Double.class));
                    arrayList.add(context.getString(R.string.bearingTemp, bearing1Temp, bearing2Temp));
                    String wnding1Temp = String.valueOf(dataSnapshot.child("wnding1Temp").getValue(Double.class));
                    String wnding2Temp = String.valueOf(dataSnapshot.child("wnding2Temp").getValue(Double.class));
                    String wnding3Temp = String.valueOf(dataSnapshot.child("wnding3Temp").getValue(Double.class));
                    arrayList.add(context.getString(R.string.wndingTemp, wnding1Temp, wnding2Temp, wnding3Temp));
                    String l1N = String.valueOf(dataSnapshot.child("l1N").getValue(Integer.class));
                    String l2N = String.valueOf(dataSnapshot.child("l2N").getValue(Integer.class));
                    String l3N = String.valueOf(dataSnapshot.child("l3N").getValue(Integer.class));
                    arrayList.add(context.getString(R.string.voltageGen, l1N, l2N, l3N));
                    String U12 = String.valueOf(dataSnapshot.child("U12").getValue(Integer.class));
                    String U23 = String.valueOf(dataSnapshot.child("U23").getValue(Integer.class));
                    String U31 = String.valueOf(dataSnapshot.child("U31").getValue(Integer.class));
                    arrayList.add(context.getString(R.string.voltageKtp, U12, U23, U31));


                    Long monthStartGenerated = dataSnapshot.child("monthStartGenerated").getValue(Long.class);
                    Long totalActivePower = dataSnapshot.child("totalActivePower").getValue(Long.class);

                    if (monthStartGenerated != null && totalActivePower != null) {
                        arrayList.add(context.getString(R.string.month_generated, String.valueOf((totalActivePower - monthStartGenerated) / 1000)));
                    }
                    if (powerActive != null && powerActive == 0) {
                        String engineStopTime = String.valueOf(dataSnapshot.child("engineStopTime").getValue(String.class));
                        engineStopTime = engineStopTime.replace("T", " ");
                        arrayList.add(context.getString(R.string.engineStopTime, engineStopTime));
                    }

                    boolean alarm = Boolean.TRUE.equals(dataSnapshot.child("alarm").getValue(Boolean.class));

                    if (powerActive != null && powerActive > 0 && !alarm) stopAlarm = true;

                    if (alarm && powerActive != null && powerActive > 0 && stopAlarm) {
                        if (Boolean.TRUE.equals(ModelService.enableAlarm.getValue()) && Boolean.TRUE.equals(ModelService.regulationRunning.getValue())) {
                            ModelService.alarmSound.alarmPlay();
                            stopAlarm = false;
                        }
                        arrayList.add(context.getString(R.string.KGY_error_msg));
                    }
                    ModelService.dataListLiveData.postValue(arrayList);
                    if (ch4Kgy != null) {
                        ModelService.CH4kgy.setValue(ch4Kgy);
                    }
                }
                //else {
//                    ModelService.dataListLiveData.postValue(new ArrayList<String>() {{
//                        add(context.getString(R.string.connection_error_message));
//                    }});
//                    ModelService.avgTemp.postValue(new LinkedList<>());
//                    ModelService.dataListLiveData.postValue(new ArrayList<String>() {{
//                        add("Ошибка связи! Сервер не отвечает");
//                    }});
//                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                ModelService.dataListLiveData.postValue(new ArrayList<String>() {{
                    add(context.getString(R.string.connection_error_message));
                }});
                ModelService.avgTemp.postValue(new LinkedList<>());
            }

        };
        databaseReference.child("now").addValueEventListener(eventListener);
    }

    public void wrightUnixTime() {
        //databaseReference.child("now").child("UnixTime").setValue(1000);
        databaseReference.child("now").child("UnixTime").setValue(new Date().getTime() / 1000).addOnFailureListener(e -> {
            //
            Intent intent = new Intent(context, ModelService.class);
            intent.setAction("STOP");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent);
            } else {
                context.startService(intent);
            }
        });
    }

    public void setMaxPower(int maxPower) {
        databaseReference.child("now").child("MaxPower").setValue(maxPower);
    }

    public void getReportList(int hours) {
        DatabaseReference hourReport = databaseReference.child("HourReport");
        Query query = hourReport.orderByKey().limitToLast(hours); // Здесь устанавливается количество элементов (последних 50)
        if (reportListener != null) query.removeEventListener(reportListener);

        reportListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                LinkedList<FBreportItem> reportList = new LinkedList<>();
                for (DataSnapshot item : children) {
                    String date = item.child("date").getValue(String.class);
                    Integer powerActive = item.child("powerActive").getValue(Integer.class);
                    Float ch4_1 = item.child("CH4_1").getValue(Float.class);
                    Float ch4_2 = item.child("CH4_2").getValue(Float.class);
                    Float ch4_kgy = item.child("CH4_KGY").getValue(Float.class);
                    Integer cleanOil = item.child("cleanOil").getValue(Integer.class);
                    Integer avgTemp = item.child("avgTemp").getValue(Integer.class);
                    Float resTemp = item.child("resTemp").getValue(Float.class);


                    if (ch4_1 != null && ch4_2 != null && powerActive != null && date != null && cleanOil != null && avgTemp != null && resTemp != null && ch4_kgy != null) {
                        FBreportItem reportItem = new FBreportItem(date);
                        reportItem.setPowerActive(powerActive);
                        reportItem.setCH4_1(ch4_1);
                        reportItem.setCH4_2(ch4_2);
                        reportItem.setCH4_KGY(ch4_kgy);
                        reportItem.setGasFlow(UtilCalculations.calculateGasFlow(ch4_1, ch4_2, powerActive));
                        reportItem.setCleanOil(cleanOil);
                        reportItem.setAvgTemp(avgTemp);
                        reportItem.setResTemp(resTemp);
                        reportList.add(reportItem);
                    }
                }
                //LinkedList<FBreportItem> prunedList = pruneList(reportList, 31);
                ModelService.reportList.setValue(reportList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // Обработка ошибки
            }
        };
        query.addValueEventListener(reportListener);
    }

    public void getServerUnixTime() {
        DatabaseReference serverTime = databaseReference.child("now").child("serverUnixTime20");
        serverTime.get().addOnSuccessListener(dataSnapshot -> {
            Long serverTime20 = dataSnapshot.getValue(Long.class);
            ModelService.serverUnixTime20.postValue(serverTime20);
        });
    }

    public void getAvgTemp(int maxItems) {
        if (avgTempListener == null) {
            DatabaseReference avgTempReport = databaseReference.child("avgTemp").child("time");

            avgTempListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    LinkedList<Float> avgTempValue = ModelService.avgTemp.getValue();
                    databaseReference.child("avgTemp").child("0").get().addOnSuccessListener(dataSnapshot -> {
                        Integer value = dataSnapshot.getValue(Integer.class);
                        if (avgTempValue != null && avgTempValue.size() > maxItems) {
                            avgTempValue.removeFirst();
                        }
                        if (value != null) {
                            if (avgTempValue != null) {
                                avgTempValue.addLast(Float.valueOf(value));
                                ModelService.avgTemp.setValue(avgTempValue);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            };
            avgTempReport.addValueEventListener(avgTempListener);
        }
    }

    public void disconnect() {
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
        if (avgTempListener != null) {
            databaseReference.removeEventListener(avgTempListener);
        }
        if (reportListener != null) {
            databaseReference.removeEventListener(reportListener);
        }
    }
}

