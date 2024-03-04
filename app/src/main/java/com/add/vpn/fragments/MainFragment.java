package com.add.vpn.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.AdManager;
import com.add.vpn.ModelJobService.RegulateTransferService;
import com.add.vpn.NumberPickerDialog;
import com.add.vpn.R;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;
import com.add.vpn.modelService.AlarmCH4Service;
import com.add.vpn.view.AnalogView;
import com.add.vpn.view.ChartView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.LinkedList;

import static kotlin.jvm.internal.Intrinsics.checkNotNull;

public class MainFragment extends Fragment {
    private Button btnOnOff;
    private boolean regulate;
    private AdManager adManager;
    private Button btnSoundOff;
    private FragmentActivity fragmentActivity;
    private FirebaseAuth mAuth;
    private RealtimeDatabase realtimeDatabase;
    private String operator = "";
    private AnalogView parMeter;
    private AnalogView opPe;
    private ChartView avgTemp;
    private Button btnCH4;
    private Boolean isAlarmCH4 = false;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentActivity = requireActivity();

        RegulateTransferService.running.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                btnOnOff.setText(R.string.btn_regulateOff);
            } else btnOnOff.setText(R.string.btn_regulateOn);
            regulate = aBoolean;
        });

        RegulateTransferService.dataListLiveData.observe(getViewLifecycleOwner(), strings -> {
            if (strings.size() > 5) {
                try {
                    String[] pwr = strings.get(4).split(" ");
                    parMeter.setValueAnimated(Float.parseFloat(pwr[2]));
                    String[] opPr = strings.get(0).split(" ");
                    opPe.setValueAnimated(Float.parseFloat(opPr[3]));
                } catch (Exception e) {
                    parMeter.setValueAnimated(0);
                    opPe.setValueAnimated(0);
                }
            } else {
                parMeter.setValueAnimated(0);
                opPe.setValueAnimated(0);
            }
        });

        MobileAds.initialize(fragmentActivity, initializationStatus -> {
            adManager = new AdManager(fragmentActivity);
            adManager.loadBannerAd();
            adManager.loadInterstitialAd();
        });

        btnOnOff.setOnClickListener(v -> startRegulate());

        btnSoundOff.setOnClickListener(v -> {
            AlarmSound alarmSound = RegulateTransferService.alarmSound;
            if (alarmSound != null) alarmSound.alarmStop();
            if (AlarmCH4Service.alarmCH4 != null) AlarmCH4Service.alarmCH4.alarmStop();
        });

        //RegulateTransferService.realtimeDatabase = new MutableLiveData<RealtimeDatabase>();
        MutableLiveData<RealtimeDatabase> database = RegulateTransferService.realtimeDatabase;
        if (database == null || realtimeDatabase == null) {
            realtimeDatabase = new RealtimeDatabase(this.fragmentActivity);
            database = new MutableLiveData<>();
            database.setValue(realtimeDatabase);
        }
        realtimeDatabase = database.getValue();
        if (realtimeDatabase != null) {
            realtimeDatabase.connect();
            realtimeDatabase.getAvgTemp(460);
        }

        avgTemp();

        AlarmCH4Service.running.observe(getViewLifecycleOwner(), isRunning -> {
            isAlarmCH4 = isRunning;
            if (isAlarmCH4) {
                btnCH4.setText(R.string.alarmCH4disable);
            } else {
                btnCH4.setText(R.string.alarmCH4enable);
            }

        });
        btnCH4.setOnClickListener(view1 -> {
            if (isAlarmCH4) {
                serviceIntent(AlarmCH4Service.STOP, AlarmCH4Service.class);
            } else {
                serviceIntent(AlarmCH4Service.START, AlarmCH4Service.class);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        mAuth = FirebaseAuth.getInstance();

        // Google Sign In was successful, authenticate with Firebase
        // Обработка ошибки аутентификации
        ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Обработка ошибки аутентификации
                        Toast.makeText(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        } else {
            operator = ": " + currentUser.getDisplayName();
            checkAccess(currentUser.getUid());
        }
    }

    private void avgTemp() {
        RegulateTransferService.avgTemp.removeObservers(requireActivity());
        RegulateTransferService.avgTemp.observe(requireActivity(), temp -> {
            LinkedList<String> time = new LinkedList<>();
            for (int i = 0; i < temp.size() * 2; i += 2) {
                if (i == 0) {
                    time.addFirst("0");
                } else {
                    time.addFirst(String.valueOf(i));
                }
            }
            avgTemp.setData(temp, time);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(fragmentActivity, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                //user.get

                if (user != null) {
                    checkAccess(user.getUid());
                    operator = " " + user.getDisplayName();
                }
            } else {
                Toast.makeText(fragmentActivity, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAccess(String uid) {
        realtimeDatabase.isAccessGranted(uid);
        Handler handler = new Handler(Looper.getMainLooper());

        RegulateTransferService.isAccessGranted.observe(requireActivity(), isAuth -> {
            if (isAuth) {
                parMeter.setOnClickListener(view1 -> {
                    NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
                    numberPickerDialog.setOnNumberSetListener(realtimeDatabase::setMaxPower);
                    numberPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "MaxPower");
                });
                handler.postDelayed(() -> btnOnOff.setEnabled(true), 3000);
                handler.postDelayed(() -> btnSoundOff.setEnabled(true), 3000);
                handler.postDelayed(() -> btnCH4.setEnabled(true), 3000);
            }
        });
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) fragmentActivity;
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name) + operator);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //dataList = rootView.findViewById(R.id.dataListView);
        avgTemp = rootView.findViewById(R.id.cv_avgTemp);
        btnOnOff = rootView.findViewById(R.id.on_off);
        btnSoundOff = rootView.findViewById(R.id.soundOff);
        btnCH4 = rootView.findViewById(R.id.alarmCH4);
        parMeter = rootView.findViewById(R.id.wat);
        opPe = rootView.findViewById(R.id.opMetr);
        //ch4 = rootView.findViewById(R.id.chart);
        return rootView;
    }

    private void startRegulate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (regulate) {
            builder.setMessage(R.string.dialog_stop_regulate);
            builder.setTitle(R.string.KGY_stop);
        } else {
            builder.setMessage(R.string.dialog_start_regulate);
            builder.setTitle(R.string.KGY_start);
        }

        // Add the buttons.
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            if (regulate) {
                regulate = false;
                //serviceIntent(ModelService.STOP, ModelService.class);
                startService(2);
                Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOff), Snackbar.LENGTH_LONG).show();
            } else {
                regulate = true;
                //serviceIntent(ModelService.START, ModelService.class);
                startService(1);
                Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOn), Snackbar.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancels the dialog.
        });
        builder.create().show();
    }

    private <T extends Service> void serviceIntent(String action, Class<T> serviceClass) {
        adManager.showInterstitialAd();
        Intent intent = new Intent(requireContext(), serviceClass);
        intent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireContext(), intent);
        } else {
            fragmentActivity.startService(intent);
        }
    }
    private void startService(int command){
        NetworkRequest networkRequestBuilder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                // Additional network request requirement
                .build();

        JobInfo.Builder jobInfo = new JobInfo.Builder(command, new ComponentName(requireContext(), RegulateTransferService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            jobInfo.setUserInitiated(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            jobInfo.setRequiredNetwork(networkRequestBuilder)
                    .setEstimatedNetworkBytes(1024 * 1024 * 1024,1024 * 1024 * 1024)
                    .build();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            JobScheduler jobScheduler = (JobScheduler) requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo.build());
        }else {
            // Для версий API ниже 21 используем IntentService или другие методы запуска службы
            Intent intent = new Intent(requireContext(), RegulateTransferService.class);
            intent.putExtra("command", command);
            requireContext().startService(intent);
        }
    }

    @Override
    public void onStop() {
        if (adManager != null) adManager.release();
        //serviceIntent(ModelService.STOP);
        super.onStop();
    }
}