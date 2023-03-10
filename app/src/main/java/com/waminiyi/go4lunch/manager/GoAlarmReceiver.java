package com.waminiyi.go4lunch.manager;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.ui.MainActivity;
import com.waminiyi.go4lunch.util.CommonString;

public class GoAlarmReceiver extends BroadcastReceiver {
    private NotificationManagerCompat mNotificationManager;
    private static final int NOTIFICATION_ID = 121212121;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = NotificationManagerCompat.from(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("lunch_notification", true)) {
            deliverNotification(context, intent);
        }
    }


    @SuppressLint("MissingPermission")
    private void deliverNotification(Context context, Intent intent) {
        if (mNotificationManager.areNotificationsEnabled()) {
            String restaurantId = intent.getStringExtra(CommonString.RESTAURANT_ID);
            Intent contentIntent = new Intent(context, MainActivity.class);
            contentIntent.putExtra(CommonString.RESTAURANT_ID,restaurantId);


            PendingIntent contentPendingIntent = PendingIntent.getActivity
                    (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String name = intent.getStringExtra("name");
            String content = intent.getStringExtra("content");
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_lunch)
                            .setContentTitle(name)
                            .setContentText(content)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVisibility(VISIBILITY_PUBLIC)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(notification)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true)
                            .setCategory(NotificationCompat.CATEGORY_ALARM);

            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }
}