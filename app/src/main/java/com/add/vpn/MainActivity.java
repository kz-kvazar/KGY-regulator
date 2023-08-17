package com.add.vpn;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.adapters.LogAdapter;
import com.add.vpn.adapters.ViewPagerAdapter;
import com.add.vpn.adapters.ZoomOutPageTransformer;
import com.add.vpn.fragments.DataFragment;
import com.add.vpn.fragments.LogFragment;
import com.add.vpn.holders.ContextHolder;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.model.AlarmSound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean connectionAlarm = false;
    private boolean generatorErrors = false;
    private MenuItem maxPower;
    private AlarmSound errorSound;
    private AlarmSound alarmSound;
    private NotificationHelper notificationHelper;
    private ViewPager2 viewPager;
    private LinkedList<String> logList;
    private LogAdapter logAdapter;
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextHolder.setActivity(MainActivity.this);



        if (savedInstanceState == null) {
            logList = new LinkedList<>();
            ContextHolder.setLogList(logList);

            logAdapter = new LogAdapter(this, R.layout.log_item, logList);
            ContextHolder.setLogAdapter(logAdapter);

            dataAdapter = new DataAdapter(this, R.layout.data_item, DataHolder.toLis());
            ContextHolder.setDataAdapter(dataAdapter);

            notificationHelper = new NotificationHelper(this.getApplicationContext());
            ContextHolder.setNotificationHelper(notificationHelper);

        }
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DataFragment());
        fragments.add(new LogFragment());


        viewPager = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,fragments);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

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
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
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