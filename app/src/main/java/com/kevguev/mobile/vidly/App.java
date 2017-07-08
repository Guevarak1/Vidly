package com.kevguev.mobile.vidly;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Kevin Guevara on 6/3/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
