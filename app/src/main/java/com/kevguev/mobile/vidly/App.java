package com.kevguev.mobile.vidly;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by Kevin Guevara on 6/3/2017.
 */

public class App extends Application {

    public GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public void setmCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }

    private GoogleAccountCredential mCredential;

}
