package com.add.vpn.holders;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.add.vpn.NotificationHelper;
import com.add.vpn.adapters.ReportItem;
import com.add.vpn.model.AlarmSound;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
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

    //TODO cleanup cod. remove hardcoding strings.
    public DataViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
    public void setAlarmState(Boolean alarm){
        if (alarm != null) {
            this.alarmState.setValue(alarm);
            if (!alarm) stopAlarmSound();
        }
    }
    public void playAlarmSound() {
        new NotificationHelper(getApplication()).showNotification("Проблемы с соединением", "Обнаружена ошибка связи с КГУ. Проверьте Wi-Fi соединение");
        if (Boolean.TRUE.equals(alarmState.getValue()) && alarmSoundLiveData.getValue() != null){
            alarmSoundLiveData.getValue().alarmPlay();
        }
    }
    public void stopAlarmSound(){
        if (alarmSoundLiveData.getValue() != null){
            alarmSoundLiveData.getValue().alarmStop();
        }
    }
    public void setErrorState(Boolean error){
        if (error != null) {
            this.errorState.setValue(error);
            if (!error) stopErrorSound();
        }
    }
    public void playErrorSound(String title, String message) {
        new NotificationHelper(getApplication()).showNotification(title, message);
        if (Boolean.TRUE.equals(errorState.getValue()) && errorSoundLiveData.getValue() != null){
            errorSoundLiveData.getValue().alarmPlay();
        }
    }
    public void stopErrorSound(){
        if (errorSoundLiveData.getValue() != null){
            errorSoundLiveData.getValue().alarmStop();
        }
    }
    public LiveData<LinkedList<String>> getLogListLiveData() {
        if (logListLiveData.getValue() == null) {
            logListLiveData.setValue(new LinkedList<>());
        }
        return logListLiveData;
    }
    public LiveData<LinkedList<ReportItem>> getReportListLiveData(){
        if (reportListLiveData.getValue() == null){
            reportListLiveData.setValue(new LinkedList<>());
        }
        return reportListLiveData;
    }

    public void addToLogList(String text) {
        LinkedList<String> currentList = logListLiveData.getValue();
        if (currentList != null) {
            currentList.addFirst(text);
            logListLiveData.postValue(currentList);
        }
    }
    public void addToReportList(ReportItem reportItem){
        LinkedList<ReportItem> report = reportListLiveData.getValue();
        if (reportItem != null && report != null){
            report.addFirst(reportItem);
            reportListLiveData.postValue(report);
        }
    }

    public LiveData<List<String>> getDataListLiveData(){
        if (dataListLiveData.getValue() == null){
            dataListLiveData.setValue(new ArrayList<>());
        }
        return dataListLiveData;
    }
    public void setDataList(List<String> list) {
        List<String> currentList = dataListLiveData.getValue();
        if (currentList != null){
            dataListLiveData.postValue(list);
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