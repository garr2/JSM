package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;

public class AuthenticationModule {

    private static AuthenticationModule aModule;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private boolean isAutorised = false;

    private String displayEMail;
    private String displayName;
    private Uri photoUrl;

    private AuthenticationModule(){
        authentication();
    }

    public static AuthenticationModule getInstance (){
        if (aModule == null)aModule = new AuthenticationModule();
        return aModule;
    }

    private boolean authentication(){
        Log.d(ConstantInterface.LOG_TAG,"authentication");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser==null) {
            isAutorised=false;
        } else if (TextUtils.equals(mFirebaseUser.getDisplayName(),"null")) {
            signOut();
        }else {
            isAutorised = true;
            setDisplayEMail();
        }
        return isAutorised;
    }

    private void setDisplayEMail(){
        displayEMail=mFirebaseUser.getEmail();
        displayEMail= displayEMail.substring(0,displayEMail.lastIndexOf("."));
    }

    public void signOut() {
        Log.d(ConstantInterface.LOG_TAG,"AuthenticationModule.signOut");
        mFirebaseAuth.signOut();
        isAutorised=false;
    }

    public boolean isAutorised() {
        return isAutorised;
    }

    public String getDisplayEMail() {
        if (displayEMail == null)setDisplayEMail();
        return displayEMail;
    }

    public boolean authenticate (){
        if (mFirebaseUser == null)authentication();
        return isAutorised;
    }

    public String getDisplayName() {
        return mFirebaseUser.getDisplayName();
    }

    public Uri getPhotoUrl() {
        return mFirebaseUser.getPhotoUrl();
    }
}
