package com.veronikalebedyuk.dialogforbetter;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.veronikalebedyuk.dialogforbetter.App.CHANNEL_1_ID;

public class BroadcastReminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent = new Intent(context,CalendarActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat notificationManager;
        notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
                .setContentTitle("в скором времени намечено событие!")
                .setContentText("сверьтесь с календарем, чтобы узнать больше информации")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(1, notification);
    }
}
