package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import com.waminiyi.go4lunch.manager.PermissionManager;
import com.waminiyi.go4lunch.manager.PreferenceManager;
import com.waminiyi.go4lunch.util.DefaultLocationDialog;
import com.waminiyi.go4lunch.util.ProgressDialog;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements PermissionManager.PermissionListener, LocationManager.LocationListener {
    private CallbackManager mFacebookCallbackManager;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private UserViewModel mUserViewModel;
    private ActivityLoginBinding binding;
    private PreferenceManager locationPrefManager;
    private DefaultLocationDialog locationDialog;
    private LocationManager locationManager;
    private PermissionManager permissionManager;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        locationPrefManager = new PreferenceManager(this);
        permissionManager = new PermissionManager();
        permissionManager.registerForPermissionResult(this);
        locationManager = new LocationManager(this);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        binding.facebookSignInButton.setReadPermissions(getString(R.string.permission_email), getString(R.string.permission_profile));
        setUpCallbackForFacebookLoginButton();

        //Initialize the places API if needed
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),MAPS_API_KEY);
        }

        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) { //This is google sign in result
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
                        showSnackBar(getString(R.string.google_signin_cancelled));
                        break;

                    case SIGN_IN_FAILED:
                        showSnackBar(getString(R.string.google_signin_failed));
                        break;
                }
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {//This is facebook sign in result
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                showSnackBar(getString(R.string.facebook_signin_cancelled));
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                showSnackBar(getString(R.string.facebook_signin_failed));
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
                            launchMainActivity();
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
                        if (isNewUser) {//First time use setup if it is a new user
                            setUpFirstTimeUse();
                        } else {
                            launchMainActivity();
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
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            setContentView(R.layout.activity_main);
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
        permissionManager.requestPermission();
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
    public void onPermissionGranted() {
        locationManager.getCurrentLocation();
    }

    @Override
    public void onPermissionDenied() {
        showSnackBar(getString(R.string.authorization_denied_message));
        finish();
    }
}