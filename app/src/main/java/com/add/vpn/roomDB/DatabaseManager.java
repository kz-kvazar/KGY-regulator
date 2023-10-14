//package com.add.vpn.roomDB;
//
//import android.content.Context;
//import androidx.room.Room;
//
//public class DatabaseManager {
//    private static volatile ReportDatabase INSTANCE;
//
//    public synchronized static ReportDatabase getInstance(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            ReportDatabase.class, "kgy_database.db")
//                    .fallbackToDestructiveMigration()
//                    .build();
//        }
//        return INSTANCE;
//    }
//}
