package com.garr.pavelbobrovko.notsimplechat.presentation.screen.login;

import android.accounts.Account;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.garr.pavelbobrovko.notsimplechat.app.JSMApp;
import com.garr.pavelbobrovko.notsimplechat.databinding.ActivityLoginBinding;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.deprecated.AuthenticationModule;
import com.garr.pavelbobrovko.notsimplechat.factory.UseCaseProvider;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.api.Scope;
import com.pavelbobrovko.garr.data.repository.OAuthRepositoryImpl;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.usecases.OAuthUseCase;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseMvvmActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends BaseMvvmActivity<LoginViewModel, LoginRouter, ActivityLoginBinding> {

    private GoogleApiClient mGoogleApiClient = null;

    private static final int RC_SIGN_IN = 9001;

    @Override
    public int provideLayoutId() {
        return R.layout.activity_login;
    }

    @NotNull
    @Override
    public LoginViewModel provideViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @NotNull
    @Override
    public LoginRouter provideRouter() {
        return new LoginRouter(this);
    }

    public void startGoogleRegistrationActivity(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/cloud-platform"))
                .build();

        Log.d(ConstantInterface.LOG_TAG,getString(R.string.default_web_client_id));
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage( this /* FragmentActivity */, getOnConnectionFailedListener())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private GoogleApiClient.OnConnectionFailedListener getOnConnectionFailedListener(){
        return new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                router.showError(new Throwable("Connection failed."));
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Toast.makeText(this,"Authentication, wait.",Toast.LENGTH_SHORT).show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                //commandListener.setAccountCreated(true);
                //this.account = account.getAccount();
                Log.d(ConstantInterface.LOG_TAG,account.getServerAuthCode());

                //Log.d(ConstantInterface.LOG_TAG, result.getSignInAccount().getIdToken());
               //new Thread(runnable).start();
                viewModel.googleRegistrationResult(account);
            } else {
                // Google Sign In failed
                router.showError(new Throwable("Google Sign In failed."));
            }
        }
    }

    String redirect_url = "https://notsimplechat-5a970.firebaseapp.com/__/auth/handler";
    private Account account;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(ConstantInterface.LOG_TAG,"oAuth: " + GoogleAuthUtil.getToken(JSMApp.instance,account,"oauth2:" + "https://www.googleapis.com/auth/cloud-platform"));
            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
                Log.d(ConstantInterface.LOG_TAG,e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient==null) {
            configureGoogleSignIn();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    private void finishWithDBError(){
        AuthenticationModule.getInstance().signOut();
        Toast.makeText(this,"Database error. App finish.",Toast.LENGTH_SHORT).show();
    }

}
