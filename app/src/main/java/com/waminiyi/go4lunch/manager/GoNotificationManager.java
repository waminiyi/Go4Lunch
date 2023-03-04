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

import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
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

    @SuppressLint("MissingPermission")
    public void scheduleLunchNotification(Context context, String name, String content) {

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(context.getApplicationContext(), GoAlarmReceiver.class);
        notifyIntent.putExtra("name", name);
        notifyIntent.putExtra("content", content);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy MM dd");
            String dateText = currentDate.format(dateFormatter);
            dateText = dateText + " 12:00 PM";

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd hh:mm a");

            LocalDateTime triggerDate = LocalDateTime.parse(dateText, dateTimeFormatter);
            triggerTime = triggerDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        } else {
            DateTime dateTime = DateTime.now();
            dateTime = dateTime.withHourOfDay(12).withMinuteOfHour(0).withSecondOfMinute(0);
            triggerTime = dateTime.toInstant().getMillis();
        }

        if (mAlarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerTime, notifyPendingIntent), notifyPendingIntent);
            else mAlarmManager.setExact(AlarmManager.RTC, triggerTime, notifyPendingIntent);
        }

    }

    public void cancelLunchNotification(Context context) {

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(context.getApplicationContext(), GoAlarmReceiver.class);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context.getApplicationContext(), NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationManager.cancelAll();

        if (mAlarmManager != null) {
            mAlarmManager.cancel(notifyPendingIntent);
        }

    }

    public boolean isNotificationAlreadyScheduled(Context context) {
        Intent notifyIntent = new Intent(context, GoAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }

}
