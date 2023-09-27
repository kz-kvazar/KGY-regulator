package com.add.vpn;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    private boolean generatorErrors = false;
    private MenuItem maxPower;
    private ViewPager2 viewPager2;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DataFragment());
        fragments.add(new LogFragment());
        fragments.add(new ReportFragment());

        viewPager2 = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        generatorErrors = SettingsManager.getErrorSetting(MainActivity.this);
        ModelService.enableAlarm.setValue(generatorErrors);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                // Обработка успешной аутентификации
                                // ...
                            } catch (ApiException e) {
                                // Обработка ошибки аутентификации
                                // ...
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            //startActivityForResult(signInIntent, RC_SIGN_IN);
            signInLauncher.launch(signInIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Toast.makeText(this, getString(R.string.google_sign_in_failed) + account.getId(), Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                    //Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
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