package com.add.vpn;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.add.vpn.adapters.ViewPagerAdapter;
import com.add.vpn.fragments.DataFragment;
import com.add.vpn.fragments.LogFragment;
import com.add.vpn.fragments.ReportFragment;
import com.add.vpn.holders.DataHolder;
import com.add.vpn.modelService.ModelService;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean generatorErrors = false;
    private MenuItem maxPower;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DataFragment());
        fragments.add(new LogFragment());
        fragments.add(new ReportFragment());

        viewPager2 = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,fragments);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());

        generatorErrors = SettingsManager.getErrorSetting(MainActivity.this);

        ModelService.enableAlarm.setValue(generatorErrors);
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
            MenuItem errorMenu = menu.findItem(R.id.error_settings);
            errorMenu.setChecked(generatorErrors);
            maxPower = menu.findItem(R.id.max_power);
            maxPower.setTitle(getString(R.string.max_power, String.valueOf(DataHolder.getMaxPower())));

        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        maxPower.setTitle(getString(R.string.max_power, String.valueOf(DataHolder.getMaxPower())));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

            if (id == R.id.error_settings) {
            generatorErrors = !generatorErrors;
            item.setChecked(generatorErrors);
            SettingsManager.setErrorSetting(this, generatorErrors);

            ModelService.enableAlarm.setValue(generatorErrors);
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
        outState.putBoolean("errorBool", generatorErrors);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        generatorErrors = savedInstanceState.getBoolean("errorBool");
    }
}