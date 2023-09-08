package com.add.vpn.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {ReportItem.class}, version = 1)

public abstract class ReportDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();
}
