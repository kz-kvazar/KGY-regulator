package com.add.vpn.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
@TypeConverters(DateConverter.class)
@Database(entities = {ReportItem.class}, version = 1, exportSchema = false)
public abstract class ReportDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();
}
