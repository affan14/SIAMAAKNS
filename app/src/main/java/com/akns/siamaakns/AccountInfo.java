package com.akns.siamaakns;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Affan Mohammad on 18/04/2016.
 */
public class AccountInfo {
    private static AccountInfo instance = null;
    private static String nrp = "";

    public synchronized static AccountInfo getInstance() {
        if (instance == null) instance = new AccountInfo();
        return instance;
    }

    private GoogleSignInAccount mGoogleSignInAccount;
    private GoogleApiClient mGoogleApiClient;

    private AccountInfo() {
    }

    protected void setGoogleAccount(GoogleSignInAccount mGoogleSignInAccounta) {
        mGoogleSignInAccount = mGoogleSignInAccounta;
    }

    protected void setGoogleApiClient(GoogleApiClient mGoogleApiClienta) {
        mGoogleApiClient = mGoogleApiClienta;
    }

    protected GoogleSignInAccount getGoogleAccount() {
        return mGoogleSignInAccount;
    }

    protected GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    protected String getAccountName() {
        return mGoogleSignInAccount.getDisplayName();
    }

    protected String getAccountEmail() {
        return mGoogleSignInAccount.getEmail();
    }

    protected void setImageProfil(CircleImageView imagePalaceholder) {
        Log.i("Photo", mGoogleSignInAccount.getPhotoUrl().toString());
        new LoadImageProfile(imagePalaceholder).execute(mGoogleSignInAccount.getPhotoUrl().toString());
    }

    protected AccountInfo setNrp(String n) {
        nrp = n;
        return instance;
    }

    protected String getNrp() {
        return nrp;
    }

}
