package com.add.vpn.firebase;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.add.vpn.R;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.modelService.ModelService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class RealtimeDatabase {
    private final Context context;
    ValueEventListener eventListener;
    DatabaseReference databaseReference;

    public RealtimeDatabase(Context context) {
        this.context = context;
        FirebaseApp.initializeApp(context.getApplicationContext());
    }

    public void connect() {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kgy-regulator-default-rtdb.europe-west1.firebasedatabase.app/");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> arrayList = new ArrayList<>();

                arrayList.add(context.getString(R.string.op_Pressure, String.valueOf(dataSnapshot.child("opPresher").getValue(Double.class))));
                arrayList.add(context.getString(R.string.throttlePosition, String.valueOf(dataSnapshot.child("trottlePosition").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.act_Power, String.valueOf(dataSnapshot.child("powerActive").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.const_Power, String.valueOf(dataSnapshot.child("powerConstant").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.max_power, String.valueOf(dataSnapshot.child("MaxPower").getValue(Integer.class))));
                arrayList.add(context.getString(R.string.VNS_1, String.valueOf(dataSnapshot.child("CH4_1").getValue(Double.class))));
                arrayList.add(context.getString(R.string.VNS_2, String.valueOf(dataSnapshot.child("CH4_2").getValue(Double.class))));
                arrayList.add(context.getString(R.string.gts_pressure, String.valueOf(dataSnapshot.child("gtsPresher").getValue(Double.class))));
                arrayList.add(context.getString(R.string.kgy_pressure, String.valueOf(dataSnapshot.child("kgyPresher").getValue(Double.class))));



                Long monthStartGenerated = dataSnapshot.child("monthStartGenerated").getValue(Long.class);
                Long totalActivePower = dataSnapshot.child("totalActivePower").getValue(Long.class);

                if (monthStartGenerated != null && totalActivePower != null) {
                    arrayList.add(context.getString(R.string.month_generated, String.valueOf((totalActivePower - monthStartGenerated) / 1000)));
                }
                if (Boolean.TRUE.equals(dataSnapshot.child("alarm").getValue(Boolean.class))) {
                    if (Boolean.TRUE.equals(ModelService.enableAlarm.getValue()) && Boolean.TRUE.equals(ModelService.running.getValue())) {
                        ModelService.alarmSound.alarmPlay();
                    }
                    arrayList.add(context.getString(R.string.KGY_error_msg));
                }

                ModelService.dataListLiveData.setValue(arrayList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                ModelService.dataListLiveData.postValue(DataHolder.toLis(context));
            }
        };
        databaseReference.addValueEventListener(eventListener);
    }

    public void wrightUnixTime() {
        databaseReference.child("UnixTime").setValue(new Date().getTime() / 1000).addOnFailureListener(e -> {
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
    public void setMaxPower(int maxPower){
        databaseReference.child("MaxPower").setValue(maxPower);
    }

    public void disconnect() {
        databaseReference.removeEventListener(eventListener);
    }
}
