package com.add.vpn;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import android.view.View;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.add.vpn.modelService.ModelService;

public class NotificationHelper {

    private static final String CHANNEL_ID = "KGY_Notifications";
    private static final String CHANNEL_NAME = "KGY";

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(Color.RED)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(777, notification);
    }
    public Notification serviceNotification() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent btnIntent = new Intent(context, ModelService.class);
        btnIntent.setAction(ModelService.STOP);
        PendingIntent btnPendingIntent = PendingIntent.getService(context,1,btnIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent sounIntent = new Intent(context, ModelService.class);
        sounIntent.setAction(ModelService.SOUND_OFF);
        PendingIntent soundPendingIntent = PendingIntent.getService(context,2,sounIntent,PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.service_notification_message))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.notification_icon, context.getString(R.string.btn_regulateOff).toUpperCase(), btnPendingIntent)
                .addAction(R.drawable.notification_icon, context.getString(R.string.btn_soundOff).toUpperCase(), soundPendingIntent)
                .setTicker(context.getString(R.string.service_notification_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(Color.GREEN)
                .setOngoing(true)
                .build();
    }
}
