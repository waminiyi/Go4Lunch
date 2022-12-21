package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.UserManager;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private UserManager mUserManager;
    BottomNavigationView bottomNavigationView;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserManager=UserManager.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.map_view);

//         tv= findViewById(R.id.tv);
//        updateUIWithUserData();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_view:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new MapViewFragment()).commit();
                return true;

            case R.id.list_view:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new ListViewFragment()).commit();
                return true;

            case R.id.workmates:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new WorkmatesFragment()).commit();
                return true;
        }
        return false;
    }
}