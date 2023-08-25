//package com.add.vpn.holders;
//
//import android.app.Activity;
//
//import com.add.vpn.NotificationHelper;
//import com.add.vpn.adapters.DataAdapter;
//import com.add.vpn.adapters.LogAdapter;
//import com.add.vpn.model.AlarmSound;
//import com.add.vpn.model.Model;
//
//import java.lang.ref.WeakReference;
//import java.util.LinkedList;
//
//public class ContextHolder {
//    private static WeakReference<Activity> context;
//    private static WeakReference<Model> modelWeakReference;
//    private static WeakReference<LogAdapter> logAdapterWeakReference;
//    private static WeakReference<DataAdapter> dataAdapterWeakReference;
//    private static WeakReference<AlarmSound> alarmSoundWeakReference;
//    private static WeakReference<AlarmSound> errorSoundWeakReference;
//    private static WeakReference<NotificationHelper> notificationHelperWeakReference;
//
//    public static AlarmSound getAlarmSound() {
//        if (alarmSoundWeakReference != null) {
//            return alarmSoundWeakReference.get();
//        } else return null;
//    }
//
//    public static void setAlarmSound(AlarmSound alarmSound) {
//        errorSoundWeakReference = new WeakReference<>(alarmSound);
//    }
//    public static AlarmSound getErrorSound() {
//        if (errorSoundWeakReference != null) {
//            return alarmSoundWeakReference.get();
//        } else return null;
//    }
//
//    public static void setErrorSound(AlarmSound alarmSound) {
//        alarmSoundWeakReference = new WeakReference<>(alarmSound);
//    }
//
//    public static LogAdapter getLogAdapter() {
//        if (logAdapterWeakReference != null) {
//            return logAdapterWeakReference.get();
//        }
//        return null;
//    }
//
//    public static void setLogAdapter(LogAdapter logAdapter) {
//        logAdapterWeakReference = new WeakReference<>(logAdapter);
//    }
//
//    public static DataAdapter getDataAdapter() {
//        if (dataAdapterWeakReference != null) {
//            return dataAdapterWeakReference.get();
//        }
//        return null;
//    }
//
//    public static void setDataAdapter(DataAdapter dataAdapter) {
//        dataAdapterWeakReference = new WeakReference<>(dataAdapter);
//    }
//
////    public static LinkedList<String> getLogList() {
////        if (logListWeakReference != null) {
////            return logListWeakReference.get();
////        }
////        return null;
////    }
////
////    public static void setLogList(LinkedList<String> logList) {
////        logListWeakReference = new WeakReference<>(logList);
////    }
//
//    public static Model getModel() {
//        if (modelWeakReference != null) {
//            return modelWeakReference.get();
//        }
//        return null;
//    }
//
//    public static void setModel(Model model) {
//        modelWeakReference = new WeakReference<>(model);
//    }
//
//    public static Activity getActivity() {
//        if (context != null) {
//            return context.get();
//        }
//        return null;
//    }
//
//    public static void setActivity(Activity activity) {
//        context = new WeakReference<>(activity);
//    }
//
//    public static NotificationHelper getNotificationHelper() {
//         if(notificationHelperWeakReference != null) {
//             return notificationHelperWeakReference.get();
//         } else {
//             return null;
//         }
//    }
//    public static void setNotificationHelper(NotificationHelper notificationHelper) {
//        notificationHelperWeakReference = new WeakReference<>(notificationHelper);
//    }
//}