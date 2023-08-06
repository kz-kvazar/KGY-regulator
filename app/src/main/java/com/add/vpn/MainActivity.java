package com.add.vpn;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.fragments.DataFragment;
import com.add.vpn.fragments.LogFragment;
import com.add.vpn.holders.ContextHolder;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.model.AlarmSound;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private boolean connectionAlarm = false;
    private boolean generatorErrors = false;
    private MenuItem maxPower;
    private AlarmSound errorSound;
    private AlarmSound alarmSound;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextHolder.setActivity(MainActivity.this);

        notificationHelper = new NotificationHelper(this);
        ContextHolder.setNotificationHelper(notificationHelper);

        if (savedInstanceState == null) {
            LogFragment logFragment = new LogFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.logFrameLayout, logFragment).commit();

            DataFragment dataFragment = new DataFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.dataFrameLayout, dataFragment).commit();

            LinkedList<String> logList = new LinkedList<>();
            ContextHolder.setLogList(logList);

            LogAdapter logAdapter = new LogAdapter(this, R.layout.list_item, logList);
            ContextHolder.setLogAdapter(logAdapter);
            DataAdapter dataAdapter = new DataAdapter(this, R.layout.list_item, DataHolder.toLis());
            ContextHolder.setDataAdapter(dataAdapter);

            dataFragment.setAdapter(dataAdapter);
            logFragment.setAdapter(logAdapter);
        }

        AdManager.loadBannerAd();
        AdManager.loadInterstitialAd();

        connectionAlarm = SettingsManager.getAlarmSetting(MainActivity.this, connectionAlarm);
        generatorErrors = SettingsManager.getErrorSetting(MainActivity.this, generatorErrors);

        if (connectionAlarm) {
            alarmSound = new AlarmSound();
            ContextHolder.setAlarmSound(alarmSound);
        } else {
            ContextHolder.setAlarmSound(null);
        }
        if (generatorErrors) {
            errorSound = new AlarmSound();
            ContextHolder.setErrorSound(errorSound);
        } else {
            ContextHolder.setErrorSound(null);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu != null) {
            MenuItem alarmMenu = menu.findItem(R.id.alarm_settings);
            alarmMenu.setChecked(connectionAlarm);
            MenuItem errorMenu = menu.findItem(R.id.error_settings);
            errorMenu.setChecked(generatorErrors);
            maxPower = menu.findItem(R.id.max_power);
            maxPower.setTitle(getText(R.string.max_power) + " " + DataHolder.getMaxPower());
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        maxPower.setTitle(getText(R.string.max_power) + " " + DataHolder.getMaxPower());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.alarm_settings) {
            connectionAlarm = !connectionAlarm;
            item.setChecked(connectionAlarm);
            SettingsManager.setAlarmSetting(this, connectionAlarm);

            if (connectionAlarm) {
                alarmSound = new AlarmSound();
                ContextHolder.setAlarmSound(alarmSound);
            } else {
                alarmSound.alarmStop();
                ContextHolder.setAlarmSound(null);
            }
            return true;

        } else if (id == R.id.error_settings) {
            generatorErrors = !generatorErrors;
            item.setChecked(generatorErrors);
            SettingsManager.setErrorSetting(this, generatorErrors);

            if (generatorErrors) {
                errorSound = new AlarmSound();
                ContextHolder.setErrorSound(errorSound);
            } else {
                errorSound.alarmStop();
                ContextHolder.setErrorSound(null);
            }
            return true;
        } else if (id == R.id.max_power) {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
            numberPickerDialog.setOnNumberSetListener(DataHolder::setMaxPower);
            numberPickerDialog.show(getSupportFragmentManager(), "MaxPower");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("alarmBool", connectionAlarm);
        outState.putBoolean("errorBool", generatorErrors);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        connectionAlarm = savedInstanceState.getBoolean("alarmBool");
        generatorErrors = savedInstanceState.getBoolean("errorBool");
    }
}