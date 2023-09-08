package com.add.vpn.holders;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.R;
import com.add.vpn.roomDB.ReportItem;
import com.add.vpn.model.AlarmSound;
import com.add.vpn.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private final MutableLiveData<LinkedList<String>> logListLiveData = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<List<String>> dataListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<LinkedList<ReportItem>> reportListLiveData = new MutableLiveData<>(new LinkedList<>());
    private final MutableLiveData<Boolean> alarmState = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> errorState = new MutableLiveData<>(true);
    private final MutableLiveData<AlarmSound> alarmSoundLiveData = new MutableLiveData<>(new AlarmSound(getApplication()));
    private final MutableLiveData<AlarmSound> errorSoundLiveData = new MutableLiveData<>(new AlarmSound(getApplication()));
    private final MutableLiveData<Model> modelLiveData = new MutableLiveData<>();

    public MutableLiveData<Model> getModelLiveData() {
        return modelLiveData;
    }
    public void setModelLiveData(Model model){
        modelLiveData.setValue(model);
    }

    public DataViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void setAlarmState(Boolean alarm) {
        if (alarm != null) {
            this.alarmState.setValue(alarm);
            if (!alarm) stopAlarmSound();
        }
    }

    public void playAlarmSound() {
        synchronized (alarmSoundLiveData) {
            new NotificationHelper(getApplication()).showNotification(getApplication().getString(R.string.connection_error_title), getApplication().getString(R.string.connection_error_message));
            if (Boolean.TRUE.equals(alarmState.getValue()) && alarmSoundLiveData.getValue() != null) {
                alarmSoundLiveData.getValue().alarmPlay();
            }
        }
    }

    public void stopAlarmSound() {
        synchronized (alarmSoundLiveData) {
            if (alarmSoundLiveData.getValue() != null) {
                alarmSoundLiveData.getValue().alarmStop();
            }
        }
    }

    public void setErrorState(Boolean error) {
        if (error != null) {
            this.errorState.setValue(error);
            if (!error) stopErrorSound();
        }
    }

    public void playErrorSound(String title, String message) {
        synchronized (errorSoundLiveData) {
            new NotificationHelper(getApplication()).showNotification(title, message);
            if (Boolean.TRUE.equals(errorState.getValue()) && errorSoundLiveData.getValue() != null) {
                errorSoundLiveData.getValue().alarmPlay();
            }
        }
    }

    public void stopErrorSound() {
        synchronized (errorSoundLiveData) {
            if (errorSoundLiveData.getValue() != null) {
                errorSoundLiveData.getValue().alarmStop();
            }
        }
    }

    public LiveData<LinkedList<String>> getLogListLiveData() {
        synchronized (logListLiveData) {
            if (logListLiveData.getValue() == null) {
                logListLiveData.setValue(new LinkedList<>());
            }
            return logListLiveData;
        }
    }

    public LiveData<LinkedList<ReportItem>> getReportListLiveData() {
        synchronized (reportListLiveData) {
            if (reportListLiveData.getValue() == null) {
                reportListLiveData.setValue(new LinkedList<>());
            }
            return reportListLiveData;
        }
    }

    public void addToLogList(String text) {
        synchronized (logListLiveData) {
            LinkedList<String> currentList = logListLiveData.getValue();
            if (currentList != null) {
                currentList.addFirst(text);
                logListLiveData.postValue(currentList);
            }
        }
    }

    public void addToReportList(ReportItem reportItem) {
        synchronized (reportListLiveData) {
            LinkedList<ReportItem> report = reportListLiveData.getValue();
            if (reportItem != null && report != null) {
                report.addFirst(reportItem);
                reportListLiveData.postValue(report);
            }
        }
    }

    public LiveData<List<String>> getDataListLiveData() {
        synchronized (dataListLiveData) {
            if (dataListLiveData.getValue() == null) {
                dataListLiveData.setValue(new ArrayList<>());
            }
            return dataListLiveData;
        }
    }

    public void setDataList(List<String> list) {
        synchronized (dataListLiveData) {
            List<String> currentList = dataListLiveData.getValue();
            if (currentList != null) {
                dataListLiveData.postValue(list);
            }
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        AlarmSound alarmSound = alarmSoundLiveData.getValue();
        if (alarmSound != null) alarmSound.release();
        AlarmSound errorSound = errorSoundLiveData.getValue();
        if (errorSound != null) errorSound.release();
    }
}