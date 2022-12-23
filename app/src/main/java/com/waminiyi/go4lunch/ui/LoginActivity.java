package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;

import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ActivityLoginBinding;
import com.waminiyi.go4lunch.util.ProgressDialog;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;
import com.waminiyi.go4lunch.viewmodel.ViewModelFactory;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager mFacebookCallbackManager;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private UserViewModel mUserViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);

        mFacebookCallbackManager = CallbackManager.Factory.create();
        binding.facebookSignInButton.setReadPermissions("email", "public_profile");
        setUpCallbackForFacebookLoginButton();

        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
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
        } else {
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        boolean isNewUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                        if (isNewUser) {
                            mUserViewModel.createNewUser();
                        }

                        launchMainActivity();

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showSnackBar(getString(R.string.account_creation_failure_error));
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        mUserViewModel.createNewUser();
                        launchMainActivity();

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        showSnackBar(getString(R.string.account_creation_failure_error));
                    }
                });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.authenticationLayout, message, Snackbar.LENGTH_SHORT).show();
    }

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
}