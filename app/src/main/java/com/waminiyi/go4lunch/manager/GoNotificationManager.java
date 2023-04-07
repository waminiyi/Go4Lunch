package com.waminiyi.go4lunch.manager;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.waminiyi.go4lunch.util.Constants;

import org.joda.time.DateTime;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class GoNotificationManager {

    private final NotificationManagerCompat mNotificationManager;
    private final AlarmManager mAlarmManager;
    private static final int NOTIFICATION_ID = 121212121;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Inject
    public GoNotificationManager(@ApplicationContext Context context) {
        this.mNotificationManager = NotificationManagerCompat.from(context);
        this.mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createLunchNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Lunch time notification",
                            NotificationManager.IMPORTANCE_HIGH);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(notification, att);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            notificationChannel.setDescription
                    ("Remind the restaurant chosen for lunch 15 minutes before time");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint({"MissingPermission", "UnspecifiedImmutableFlag"})
    public void scheduleLunchNotification(Context context, String userId, String userName,
                                          String restaurantId, String restaurantName,
                                          String restaurantAddress) {

        Intent notifyIntent = new Intent(context.getApplicationContext(), GoAlarmReceiver.class);
        notifyIntent.putExtra(Constants.USER_ID, userId);
        notifyIntent.putExtra(Constants.USER_NAME, userName);
        notifyIntent.putExtra(Constants.RESTAURANT_ID, restaurantId);
        notifyIntent.putExtra(Constants.RESTAURANT_NAME, restaurantName);
        notifyIntent.putExtra(Constants.RESTAURANT_ADDRESS, restaurantAddress);

        PendingIntent notifyPendingIntent ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent,
                            PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }
        long now;
        long triggerTimeMillis;

        DateTime nowDateTime = DateTime.now();
        DateTime triggerDateTime =
                nowDateTime.withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0);
        now = nowDateTime.toInstant().getMillis();
        triggerTimeMillis = triggerDateTime.toInstant().getMillis();

        if (mAlarmManager != null && triggerTimeMillis > now) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerTimeMillis, notifyPendingIntent), notifyPendingIntent);
            else mAlarmManager.setExact(AlarmManager.RTC, triggerTimeMillis, notifyPendingIntent);
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void cancelLunchNotification(Context context) {

        Intent notifyIntent = new Intent(context.getApplicationContext(), GoAlarmReceiver.class);

        PendingIntent notifyPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent,
                            PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mNotificationManager.cancelAll();

        if (mAlarmManager != null) {
            mAlarmManager.cancel(notifyPendingIntent);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public boolean isNotificationAlreadyScheduled(Context context) {
        Intent notifyIntent = new Intent(context, GoAlarmReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context, NOTIFICATION_ID, notifyIntent,
                    PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT) != null;
        }else{
            return PendingIntent.getBroadcast(context, NOTIFICATION_ID, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT) != null;
        }
    }

}
