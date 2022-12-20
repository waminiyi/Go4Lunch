package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.UserManager;

public class MainActivity extends AppCompatActivity {

    private UserManager mUserManager;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserManager=UserManager.getInstance();

         tv= findViewById(R.id.tv);
        updateUIWithUserData();
    }

    private void updateUIWithUserData(){
        // If user is logged
        if(mUserManager.isCurrentUserLogged()){
            getUserData();
        }

    }


    private void getUserData(){
        mUserManager.getUserData().addOnSuccessListener(user -> {
            String username = TextUtils.isEmpty(user.getUserName()) ? "user name not found" : user.getUserName();
            tv.setText(username);
        });
    }
}