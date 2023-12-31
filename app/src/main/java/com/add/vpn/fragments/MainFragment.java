package com.add.vpn.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.AdManager;
import com.add.vpn.NumberPickerDialog;
import com.add.vpn.R;
import com.add.vpn.adapters.DataAdapter;
import com.add.vpn.firebase.RealtimeDatabase;
import com.add.vpn.model.AlarmSound;
import com.add.vpn.modelService.ModelService;
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

public class MainFragment extends Fragment {

    private RecyclerView dataList;
    private Button btnOnOff;
    private DataAdapter dataAdapter;
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentActivity = requireActivity();

        //dataAdapter = new DataAdapter(DataHolder.toLis(fragmentActivity.getApplicationContext()));
//        dataAdapter = new DataAdapter(ModelService.dataListLiveData.getValue());
//
//        dataList.setAdapter(dataAdapter);
//        dataList.setLayoutManager(new LinearLayoutManager(fragmentActivity));

        ModelService.running.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                btnOnOff.setText(R.string.btn_regulateOff);
            } else btnOnOff.setText(R.string.btn_regulateOn);
            regulate = aBoolean;
        });
        parMeter.setOnClickListener(view1 -> {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
            numberPickerDialog.setOnNumberSetListener(realtimeDatabase::setMaxPower);
            numberPickerDialog.show(fragmentActivity.getSupportFragmentManager(), "MaxPower");
        });

        ModelService.dataListLiveData.observe(getViewLifecycleOwner(), strings -> {
            //dataAdapter.notifyItemRangeChanged(0, 20);
            if (strings.size() > 5){
                try{
                    String[] pwr = strings.get(4).split(" ");
                    parMeter.setValueAnimated(Float.parseFloat(pwr[2]));
                    String[] opPr = strings.get(0).split(" ");
                    opPe.setValueAnimated(Float.parseFloat(opPr[3]));
                }catch (Exception e){
                    parMeter.setValueAnimated(0);
                    opPe.setValueAnimated(0);
                }
            }else {
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
            AlarmSound alarmSound = ModelService.alarmSound;
            if (alarmSound != null) alarmSound.alarmStop();
        });
        realtimeDatabase = ModelService.realtimeDatabase.getValue();
        if (realtimeDatabase == null) {
            realtimeDatabase = new RealtimeDatabase(this.fragmentActivity);
            ModelService.realtimeDatabase.setValue(realtimeDatabase);
        }
        //realtimeDatabase = new RealtimeDatabase(this.fragmentActivity);
        realtimeDatabase.connect();
        realtimeDatabase.getAvgTemp(300);
        avgTemp();

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
            accessGranted();
        }

    }

    private void avgTemp(){
        ModelService.avgTemp.removeObservers(requireActivity());
        ModelService.avgTemp.observe(requireActivity(),temp ->{
            LinkedList<String> time = new LinkedList<>();
            for (int i = 0; i < temp.size() * 2; i+=2) {
                if(i == 0){
                    time.addFirst("0");
                }else{
                    time.addFirst(String.valueOf(i));
                }
            }
            avgTemp.setData(temp,time);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(fragmentActivity, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                operator = user != null ? " " + user.getDisplayName() : "";
                accessGranted();
            } else {
                Toast.makeText(fragmentActivity, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void accessGranted() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> btnOnOff.setEnabled(true), 3000);
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
        parMeter = rootView.findViewById(R.id.wat);
        opPe = rootView.findViewById(R.id.opMetr);
        //ch4 = rootView.findViewById(R.id.chart);
        return rootView;
    }

    private void startRegulate() {
//        if (regulate) {
//
////            regulate = false;
////            serviceIntent(ModelService.STOP);
////            Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOff), Snackbar.LENGTH_LONG).show();
//
//        } else {
//            regulate = true;
//            serviceIntent(ModelService.START);
//            Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOn), Snackbar.LENGTH_LONG).show();
//            adManager.showInterstitialAd();
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (regulate){
            builder.setMessage(R.string.dialog_stop_regulate);
            builder.setTitle(R.string.KGY_stop);
        }else {
            builder.setMessage(R.string.dialog_start_regulate);
            builder.setTitle(R.string.KGY_start);
        }

            // Add the buttons.
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            if (regulate) {
                regulate = false;
                serviceIntent(ModelService.STOP);
                Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOff), Snackbar.LENGTH_LONG).show();
            }else {
                regulate = true;
                serviceIntent(ModelService.START);
                Snackbar.make(fragmentActivity, requireView(), getString(R.string.regulate_statusOn), Snackbar.LENGTH_LONG).show();
                adManager.showInterstitialAd();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancels the dialog.
        });
        builder.create().show();
    }

    private void serviceIntent(String action) {
        Intent intent = new Intent(requireContext(), ModelService.class);
        intent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(requireContext(), intent);
        } else {
            fragmentActivity.startService(intent);
        }
    }

    @Override
    public void onStop() {
        if (adManager != null) adManager.release();
        //serviceIntent(ModelService.STOP);
        super.onStop();
    }
}