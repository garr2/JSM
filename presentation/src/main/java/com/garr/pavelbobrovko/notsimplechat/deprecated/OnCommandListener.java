package com.garr.pavelbobrovko.notsimplechat.deprecated;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by garr on 22.06.2017.
 */

public interface OnCommandListener {

    void changeFragment();

    FirebaseAuth getFirebaseAuth();

    FirebaseUser getFirebaseUser();

    void setFirebaseUser(FirebaseUser user);

    //void setAccountCreated(boolean isCreated);

    //void setLoginIn(boolean loginIn);

    //boolean getAccountCreated();

    //boolean getLoginIn();

    void setResultAndFinish(int result);

    void checkPermission();

    void finishRegistration();

    boolean isSettingsMode();
}
