package com.add.vpn;


import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.add.vpn.adapters.ViewPagerAdapter;
import com.add.vpn.adapters.ZoomOutPageTransformer;
import com.add.vpn.fragments.DataFragment;
import com.add.vpn.fragments.LogFragment;
import com.add.vpn.fragments.ReportFragment;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.holders.DataViewModel;


import java.util.ArrayList;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean connectionAlarm = false;
    private boolean generatorErrors = false;
    private MenuItem maxPower;
    private ViewPager2 viewPager2;
    private DataViewModel dataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DataFragment());
        fragments.add(new LogFragment());
        fragments.add(new ReportFragment());

        viewPager2 = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,fragments);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());

        connectionAlarm = SettingsManager.getAlarmSetting(MainActivity.this);
        generatorErrors = SettingsManager.getErrorSetting(MainActivity.this);

        dataViewModel.setAlarmState(connectionAlarm);
        dataViewModel.setErrorState(generatorErrors);

    }

    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
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

            dataViewModel.setAlarmState(connectionAlarm);
            return true;
        } else if (id == R.id.error_settings) {
            generatorErrors = !generatorErrors;
            item.setChecked(generatorErrors);
            SettingsManager.setErrorSetting(this, generatorErrors);

            dataViewModel.setErrorState(generatorErrors);
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