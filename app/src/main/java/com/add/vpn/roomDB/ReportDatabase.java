package com.add.vpn.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ReportItem.class}, version = 1)
@TypeConverters(DateConverter.class)

public abstract class ReportDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();
}
