//package com.add.vpn.roomDB;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.*;
//
//import java.util.List;
//
//@Dao
//public interface ReportDao {
//    @Query("SELECT * FROM report ORDER BY id DESC")
//    LiveData<List<ReportItem>> getAllLiveData();
//    @Query("SELECT * FROM report ORDER BY id DESC")
//    List<ReportItem> getAll();
//    @Query("SELECT * FROM report ORDER BY id DESC LIMIT 1")
//    ReportItem getLast();
//    @Query("SELECT * FROM report ORDER BY id DESC LIMIT 1")
//    LiveData<ReportItem> getLastLiveData();
//    @Query("SELECT * FROM report WHERE strftime('%Y-%m', datetime(date/1000, 'unixepoch', 'localtime')) = strftime('%Y-%m', 'now', 'localtime') ORDER BY id DESC")
//    LiveData<List<ReportItem>> getCurrentMonthLiveData();
//    @Query("SELECT * FROM report WHERE strftime('%Y', datetime(date/1000, 'unixepoch', 'localtime')) = strftime('%Y', 'now', 'localtime') ORDER BY id DESC")
//    LiveData<List<ReportItem>> getCurrentYearLiveData();
//    @Query("SELECT * FROM report WHERE strftime('%Y-%m-%d', datetime(date/1000, 'unixepoch', 'localtime')) = strftime('%Y-%m-%d', 'now', 'localtime') ORDER BY id DESC")
//    LiveData<List<ReportItem>> getCurrentDayLiveData();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(ReportItem... reportItems);
//    @Delete
//    void delete(ReportItem reportItem);
//}
