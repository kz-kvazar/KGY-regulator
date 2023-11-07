package com.add.vpn.firebase;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.add.vpn.R;
import com.add.vpn.UtilCalculations;
import com.add.vpn.modelService.ModelService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RealtimeDatabase {
    private final Context context;
    ValueEventListener eventListener;
    ValueEventListener reportListener;
    DatabaseReference databaseReference;
    private Boolean stopAlarm = false;

    public RealtimeDatabase(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context.getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kgy-regulator-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public void connect() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> arrayList = new ArrayList<>();

                arrayList.add(context.getString(R.string.op_Pressure, String.valueOf(dataSnapshot.child("now").child("opPresher").getValue(Double.class))));
                arrayList.add(context.getString(R.string.throttlePosition, String.valueOf(dataSnapshot.child("now").child("trottlePosition").getValue(Integer.class))));
                Integer powerActive = dataSnapshot.child("now").child("powerActive").getValue(Integer.class);
                arrayList.add(context.getString(R.string.act_Power, String.valueOf(powerActive)));
                arrayList.add(context.getString(R.string.const_Power, String.valueOf(dataSnapshot.child("now").child("powerConstant").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.max_power, String.valueOf(dataSnapshot.child("now").child("MaxPower").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.VNS_1, String.valueOf(dataSnapshot.child("now").child("CH4_1").getValue(Double.class))));
                arrayList.add(context.getString(R.string.VNS_2, String.valueOf(dataSnapshot.child("now").child("CH4_2").getValue(Double.class))));
                arrayList.add(context.getString(R.string.gts_pressure, String.valueOf(dataSnapshot.child("now").child("gtsPresher").getValue(Double.class))));
                arrayList.add(context.getString(R.string.kgy_pressure, String.valueOf(dataSnapshot.child("now").child("kgyPresher").getValue(Double.class))));

                Long monthStartGenerated = dataSnapshot.child("now").child("monthStartGenerated").getValue(Long.class);
                Long totalActivePower = dataSnapshot.child("now").child("totalActivePower").getValue(Long.class);

                if (monthStartGenerated != null && totalActivePower != null) {
                    arrayList.add(context.getString(R.string.month_generated, String.valueOf((totalActivePower - monthStartGenerated) / 1000)));
                }
                if (powerActive != null && powerActive > 0) stopAlarm = true;

                if (Boolean.TRUE.equals(dataSnapshot.child("now").child("alarm").getValue(Boolean.class)) || (powerActive != null && powerActive == 0 && stopAlarm)) {
                    if (Boolean.TRUE.equals(ModelService.enableAlarm.getValue()) && Boolean.TRUE.equals(ModelService.running.getValue())) {
                        ModelService.alarmSound.alarmPlay();
                        stopAlarm = false;
                    }
                    arrayList.add(context.getString(R.string.KGY_error_msg));
                }
                ModelService.dataListLiveData.setValue(arrayList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                ModelService.dataListLiveData.postValue(new ArrayList<String>() {{
                    add(context.getString(R.string.connection_error_message));
                }});
            }

        };
        databaseReference.addValueEventListener(eventListener);
    }

    public void wrightUnixTime() {
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
                    Integer cleanOil = item.child("cleanOil").getValue(Integer.class);
                    Integer avgTemp = item.child("avgTemp").getValue(Integer.class);
                    Float resTemp = item.child("resTemp").getValue(Float.class);


                    if (ch4_1 != null && ch4_2 != null && powerActive != null && date != null) {
                        FBreportItem reportItem = new FBreportItem(date);
                        reportItem.setPowerActive(powerActive);
                        reportItem.setCH4_1(ch4_1);
                        reportItem.setCH4_2(ch4_2);
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


    public void disconnect() {
        databaseReference.removeEventListener(eventListener);
    }
    public LinkedList<FBreportItem> pruneList(LinkedList<FBreportItem> list, int maxItems) {
        if (list.size() <= maxItems) {
            return list; // Если список уже не больше 48, не требуется прореживание
        }

        int interval = list.size() / maxItems;

        LinkedList<FBreportItem> prunedList = new LinkedList<>();
        int avgGenerated = 0;
        for (int i = 0; i < list.size(); i++) {
            avgGenerated += list.get(i).getPowerActive();
            if (i % interval == 0) {
                list.get(i).setPowerActive(avgGenerated/interval);
                prunedList.add(list.get(i));
                avgGenerated = 0;
            }
        }

        return prunedList;
    }

}

