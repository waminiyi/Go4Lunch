package com.waminiyi.go4lunch.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LunchWorker extends ListenableWorker {

    public LunchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        return CallbackToFutureAdapter.getFuture(completer -> {

            String userId = getInputData().getString(Constants.USER_ID);
            String userName = getInputData().getString(Constants.USER_NAME);
            String restaurantName = getInputData().getString(Constants.RESTAURANT_NAME);
            String restaurantId = getInputData().getString(Constants.RESTAURANT_ID);
            String restaurantAddress = getInputData().getString(Constants.RESTAURANT_ADDRESS);

            StringBuilder contentBuilder = new StringBuilder();

            FirebaseApp.initializeApp(getApplicationContext());
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final FirebaseFirestore database = FirebaseFirestore.getInstance();
            final FirebaseHelper helper = new FirebaseHelper(auth, database);

            DocumentReference users = database.collection("snippets").document("allUsers");

            if (restaurantId != null && !restaurantId.isEmpty()) {
                contentBuilder.append("Hey ").append(userName).append(" ! It's your ").append("lunch time at ")
                        .append(restaurantName).append(". \nThe address is : ").append(restaurantAddress).append(".\n");

                helper.getLunches().addOnSuccessListener(documentSnapshot -> {

                    Map<String, Object> map = documentSnapshot.getData();
                    List<String> workmates = new ArrayList<>();
                    if (map != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            Lunch lunch = documentSnapshot.get(entry.getKey(), Lunch.class);
                            if (lunch != null && lunch.getRestaurantId().equals(restaurantId) && !lunch.getUserId().equals(userId)) {
                                workmates.add(lunch.getUserId());
                            }
                        }
                        if (workmates.size() > 0) {
                            contentBuilder.append("You are going to lunch with: \n");
                            users.get().addOnSuccessListener(documentSnapshot1 -> {

                                for (String id : workmates) {
                                    User user = documentSnapshot1.get(id, User.class);
                                    if (user != null) {
                                        contentBuilder.append("> ").append(user.getUserName()).append("\n");
                                    }
                                }
                                Data outputData = new Data.Builder()
                                        .putString(Constants.NOTIFICATION_CONTENT, contentBuilder.toString())
                                        .putString(Constants.RESTAURANT_ID, restaurantId)
                                        .build();
                                completer.set(Result.success(outputData));
                            });
                        }else{
                            Data outputData = new Data.Builder()
                                    .putString(Constants.NOTIFICATION_CONTENT, contentBuilder.toString())
                                    .putString(Constants.RESTAURANT_ID, restaurantId)
                                    .build();

                            completer.set(Result.success(outputData));
                        }
                    }
                });
            } else {
                completer.set(Result.failure());
            }


            return "Lunch details fetching started";
        });
    }

//    @NonNull
//    @Override
//    public Result doWork() {
//
//        String userId = getInputData().getString(Constants.USER_ID);
//        String userName = getInputData().getString(Constants.USER_NAME);
//        String restaurantName = getInputData().getString(Constants.RESTAURANT_NAME);
//        String restaurantId = getInputData().getString(Constants.RESTAURANT_ID);
//        String restaurantAddress = getInputData().getString(Constants.RESTAURANT_ADDRESS);
//
//        StringBuilder contentBuilder = new StringBuilder();
//        contentBuilder.append("Hey ").append(userName).append(" ! It's your ").append("lunch time at ")
//                .append(restaurantName).append(". \nThe address is : ").append(restaurantAddress).append(".\n");
//
//        FirebaseApp.initializeApp(getApplicationContext());
//        final FirebaseAuth auth = FirebaseAuth.getInstance();
//        final FirebaseFirestore database = FirebaseFirestore.getInstance();
//        final FirebaseHelper helper = new FirebaseHelper(auth, database);
//
//        DocumentReference users = database.collection("snippets").document("allUsers");
//
//        if (restaurantId != null && !restaurantId.isEmpty()) {
//            helper.getLunches().addOnSuccessListener(documentSnapshot -> {
//
//                Map<String, Object> map = documentSnapshot.getData();
//                List<String> workmates = new ArrayList<>();
//                if (map != null) {
//                    for (Map.Entry<String, Object> entry : map.entrySet()) {
//                        Lunch lunch = documentSnapshot.get(entry.getKey(), Lunch.class);
//                        if (lunch != null && lunch.getRestaurantId().equals(restaurantId) && !lunch.getUserId().equals(userId)) {
//                            workmates.add(lunch.getUserId());
//                        }
//                    }
//                    if (workmates.size() > 0) {
//                        contentBuilder.append("You are going to lunch with: \n");
//                        users.get().addOnSuccessListener(documentSnapshot1 -> {
//
//                            for (String id : workmates) {
//                                User user = documentSnapshot1.get(id, User.class);
//                                if (user != null) {
//                                    contentBuilder.append("> ").append(user.getUserName()).append("\n");
//                                }
//                            }
//                        });
//                    }
//                }
//            });
//        }
//
//        Data outputData = new Data.Builder()
//                .putString(Constants.NOTIFICATION_CONTENT, contentBuilder.toString())
//                .putString(Constants.RESTAURANT_ID, restaurantId)
//                .build();
//        return Result.success(outputData);
//    }
}
