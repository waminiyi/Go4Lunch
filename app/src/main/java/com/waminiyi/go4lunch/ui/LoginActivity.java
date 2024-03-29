package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ActivityLoginBinding;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.LocationPermissionObserver;
import com.waminiyi.go4lunch.util.ProgressDialog;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements LocationPermissionObserver.PermissionListener, LocationManager.LocationListener {
    private CallbackManager mFacebookCallbackManager;
    @Inject
    FirebaseAuth mAuth;
    private UserViewModel mUserViewModel;
    private ActivityLoginBinding binding;
    private LocationManager locationManager;
    private LocationPermissionObserver permissionObserver;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mActivityResultLauncher=registerForActivityResult();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        permissionObserver = new LocationPermissionObserver(getActivityResultRegistry());
        getLifecycle().addObserver(permissionObserver);
        permissionObserver.setListener(this);
        locationManager = new LocationManager(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        binding.facebookSignInButton.setPermissions(getString(R.string.permission_email), getString(R.string.permission_profile));
        setUpCallbackForFacebookLoginButton();

        if (!Places.isInitialized()) {
            String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }

        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private  ActivityResultLauncher<Intent> registerForActivityResult(){
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("Problem found", result.toString());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        handleGoogleSignInResult( data);
                    }else{
                        Log.d(TAG, "failed");
                    }
                });
    }

    private void handleGoogleSignInResult(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {

            switch (e.getStatusCode()) {
                case NETWORK_ERROR:
                    showSnackBar(getString(R.string.network_error));
                    break;
                case SIGN_IN_CANCELLED:
                    showSnackBar(getString(R.string.google_sign_in_cancelled));
                    break;

                case SIGN_IN_FAILED:
                    showSnackBar(getString(R.string.google_sign_in_failed));
                    break;
            }
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    private void setUpCallbackForFacebookLoginButton() {
        binding.facebookSignInButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                showSnackBar(getString(R.string.facebook_sign_in_cancelled));
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                showSnackBar(getString(R.string.facebook_sign_in_failed));
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    /**
     * Authenticate the user to firebase with google
     *
     * @param idToken: Google account idToken
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        boolean isNewUser =
                                Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                        if (isNewUser) { //First time use setup if it is a new user
                            setUpFirstTimeUse();
                        } else {
                            permissionObserver.requestPermission();
                        }

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showSnackBar(getString(R.string.account_creation_failure_error));
                    }
                });
    }

    /**
     * Authenticate the user to firebase with Facebook
     *
     * @param token: Facebook AccessToken
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser =
                                Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                        if (isNewUser) {
                            setUpFirstTimeUse();
                        } else {
                            permissionObserver.requestPermission();
                        }

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showSnackBar(getString(R.string.account_creation_failure_error));
                    }
                });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        mActivityResultLauncher.launch(signInIntent);
    }

    /**
     * Show a message in a snack-bar
     *
     * @param message: Message to show
     */
    private void showSnackBar(String message) {
        Snackbar.make(binding.authenticationLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Show a progress dialog for 5s (to be sure data have been written to Cloud Firestore
     * before launching main activity
     */
    private void launchMainActivity() {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.show(getSupportFragmentManager(), "Loading");
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, 5000);
    }

    /**
     * Create user in Cloud Firestore and request location permission at the first use
     */
    private void setUpFirstTimeUse() {
        FirebaseUser user = mUserViewModel.getCurrentUser();
        if (user != null) {
            mUserViewModel.createNewUserInDatabase(user);
        }
        permissionObserver.requestPermission();
    }


    @Override
    public void onLocationFetched(Location location) {
        launchMainActivity();
    }

    @Override
    public void onLocationError(Exception e) {
        showSnackBar(getString(R.string.location_error_message));
        finish();
    }

    @Override
    public void onLocationPermissionGranted() {
        locationManager.getCurrentLocation();
    }

    @Override
    public void onLocationPermissionDenied() {
        permissionObserver.showPermissionPurpose(this);

    }
}