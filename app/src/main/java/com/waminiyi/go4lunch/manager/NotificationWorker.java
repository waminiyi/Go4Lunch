package com.waminiyi.go4lunch.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.waminiyi.go4lunch.util.Constants;
import com.waminiyi.go4lunch.util.NotificationUtils;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String content = getInputData().getString(Constants.NOTIFICATION_CONTENT);
        String restaurantId = getInputData().getString(Constants.RESTAURANT_ID);

        NotificationUtils notificationUtils = new NotificationUtils();
        notificationUtils.deliverNotification(getApplicationContext(), restaurantId, content);


        return Result.success();
    }
}
