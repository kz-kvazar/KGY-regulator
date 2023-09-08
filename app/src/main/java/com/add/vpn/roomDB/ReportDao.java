package com.add.vpn.roomDB;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM report ORDER BY id DESC")
    LiveData<List<ReportItem>> getAllLiveData();
    @Query("SELECT * FROM report ORDER BY id DESC")
    ListenableFuture<List<ReportItem>> getAll();
    @Query("SELECT * FROM report ORDER BY id DESC LIMIT 1")
    LiveData<ReportItem> getLast();
    @Query("SELECT * FROM report WHERE strftime('%Y-%m', time_and_date) = strftime('%Y-%m', 'now')")
    List<ReportItem> getReportsForCurrentMonth();
    // Для текущей недели
    @Query("SELECT * FROM report WHERE strftime('%Y-%W', time_and_date) = strftime('%Y-%W', 'now')")
    List<ReportItem> getReportsForCurrentWeek();
    @Query("SELECT * FROM report WHERE strftime('%Y-%m-%d', time_and_date) = strftime('%Y-%m-%d', 'now')")
    List<ReportItem> getReportsForCurrentDay();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ReportItem... reportItems);
    @Delete
    void delete(ReportItem reportItem);
}
