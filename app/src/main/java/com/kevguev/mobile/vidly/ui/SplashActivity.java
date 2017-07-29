package com.kevguev.mobile.vidly.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.kevguev.mobile.vidly.Constants.EXTRA_CURRENT_LOCATION;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    protected static final String TAG = SplashActivity.class.getSimpleName();

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        buildGoogleApiClient();
        createLocationRequest();
    }

    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationPermission();
    }

    //combine permissions and then get location last then start main activity
    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_LOCATION)
    private void locationPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(
                this, perms)) {

            new BackgroundWork(this).execute();
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constants.REQUEST_PERMISSION_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
 /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "location :" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public class BackgroundWork extends AsyncTask<Void,Void, Location>{

        SplashActivity activity;

        public BackgroundWork(SplashActivity splashActivity) {
            activity = splashActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Location doInBackground(Void... voids) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (currentLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                    }
                }, Looper.getMainLooper());
            }
            else {
                return currentLocation;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);

            String locationString = currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            if(sharedPreferences.getString(getString(R.string.pref_location), null) == null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_location),locationString);
                editor.commit();
            }

            Intent i = new Intent(activity, MainActivity.class);
            i.putExtra(EXTRA_CURRENT_LOCATION ,locationString);
            startActivity(i);
            finish();
        }
    }
}
