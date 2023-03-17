package com.waminiyi.go4lunch.util;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.ui.RestaurantDetailsActivity;

public class NotificationUtils {

    @SuppressLint("MissingPermission")
    public void deliverNotification(Context context,String restaurantId, String content) {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);

        if (mNotificationManager.areNotificationsEnabled()) {
            Intent contentIntent = new Intent(context, RestaurantDetailsActivity.class);
            contentIntent.putExtra(Constants.RESTAURANT_ID, restaurantId);

            PendingIntent contentPendingIntent = PendingIntent.getActivity
                    (context, Constants.NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, Constants.PRIMARY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(Constants.NOTIFICATION_TITLE)
                            .setContentText(content)
                            .setContentIntent(contentPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVisibility(VISIBILITY_PUBLIC)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(notification)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true)
                            .setCategory(NotificationCompat.CATEGORY_ALARM);

            mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
        }

    }
}
