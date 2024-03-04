package com.add.vpn;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.add.vpn.ModelJobService.RegulateTransferService;
import com.add.vpn.adapters.ViewPagerAdapter;
import com.add.vpn.fragments.LogFragment;
import com.add.vpn.fragments.MainFragment;
import com.add.vpn.fragments.ReportFragment;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean generatorErrors = false;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new LogFragment());
        fragments.add(new ReportFragment());

        viewPager2 = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        generatorErrors = SettingsManager.getErrorSetting(MainActivity.this);

        RegulateTransferService.enableAlarm.setValue(generatorErrors);
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.error_settings) {
            generatorErrors = !generatorErrors;
            item.setChecked(generatorErrors);
            SettingsManager.setErrorSetting(this, generatorErrors);
            RegulateTransferService.enableAlarm.setValue(generatorErrors);

            if (!generatorErrors && RegulateTransferService.alarmSound != null) {
                RegulateTransferService.alarmSound.alarmStop();
            }
            return true;
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