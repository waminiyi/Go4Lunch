package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.UserManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserManager mUserManager;
    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    TextView navUsernameTV;
    TextView navUserMailTV;

    public MainActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserManager = UserManager.getInstance();
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mNavController = Navigation.findNavController(this, R.id.main_frame_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
//        configureToolBar();
        View headerView = mNavigationView.getHeaderView(0);
        navUsernameTV = (TextView) headerView.findViewById(R.id.drawer_username_textview);
        navUserMailTV= (TextView) headerView.findViewById(R.id.drawer_user_mail);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates).setDrawerLayout(mDrawerLayout)
                        .build();
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        mNavigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);


//         tv= findViewById(R.id.tv);
        updateUIWithUserData();
    }

    private void updateUIWithUserData() {
        // If user is logged
        if (mUserManager.isCurrentUserLogged()) {
            getUserData();
        }

    }


    private void getUserData() {
        mUserManager.getUserData().addOnSuccessListener(user -> {
            String username = TextUtils.isEmpty(user.getUserName()) ? "user name not found" : user.getUserName();
            String userMail = TextUtils.isEmpty(user.getUserEmail()) ? "user name not found" : user.getUserEmail();
            navUsernameTV.setText(username);
            navUserMailTV.setText(userMail);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_your_lunch:
//                NavigationUI.onNavDestinationSelected(item, mNavController);
            case R.id.navigation_settings:

            case R.id.navigation_logout:
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//
//    private void configureToolBar() {
//        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setNavigationIcon(R.drawable.ic_burger_menu);
//        setSupportActionBar(mToolbar);
//    }
}