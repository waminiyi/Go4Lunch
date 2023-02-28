package com.waminiyi.go4lunch;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.waminiyi.go4lunch.model.UserEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataTestUtil {

    public static UserEntity getValue(final LiveData<UserEntity> liveData) throws InterruptedException {
        final  Object[] user = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<UserEntity> observer = new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {
                user[0] = userEntity;
                latch.countDown();
                liveData.removeObserver(this);
            }

        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);

        return (UserEntity) user[0];
    }
}
