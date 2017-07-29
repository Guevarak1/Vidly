package com.kevguev.mobile.vidly.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.ListItem;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kevin Guevara on 6/6/2017.
 */

public class AppUtils {

    public static int locationToDialogInt(String prefLocation) {

        int chosen;

        switch (prefLocation) {
            case "40.7417544,-74.0086348":
                chosen = 1;
                break;
            case "37.7577627,-122.4726194":
                chosen = 2;
                break;
            case "38.8994613,-77.0846063":
                chosen = 3;
                break;
            case "33.757053,-84.410121":
                chosen = 4;
                break;
            case "41.8333925,-88.0123393":
                chosen = 5;
                break;
            default:
                chosen = 0;
                break;

        }
        return chosen;
    }

    public static String toLatLng(int i,String currentLocation, Context context) {

        String chosen = "";
        String[] locationValuesList = context.getResources().getStringArray(R.array.locationsValues);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (i) {
            case 0:
                chosen = currentLocation;
                break;
            case 1:
                chosen = locationValuesList[1];
                break;
            case 2:
                chosen = locationValuesList[2];
                break;
            case 3:
                chosen = locationValuesList[3];
                break;
            case 4:
                chosen = locationValuesList[4];
                break;
            case 5:
                chosen = locationValuesList[5];
            default:
                break;

        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pref_location", chosen);
        editor.commit();

        return chosen;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    public static void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode, AppCompatActivity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    public static boolean isDeviceOnline(AppCompatActivity activity) {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String getPastDate(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - days);

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(cal.getTime());


    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    public static void acquireGooglePlayServices(Context context, AppCompatActivity activity) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            AppUtils.showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, activity);
        }
    }

    public static List<ListItem> getListData(List<Item> videos) {

        List<ListItem> data = new ArrayList<>();

        //create ListItem with dummy data and then add it to our list
        for (Item video: videos) {
            ListItem item = new ListItem();
            item.setImgUrl(video.getSnippet().getThumbnails().getMedium().getUrl());
            item.setTitle(video.getSnippet().getTitle());
            item.setSubtitle(video.getSnippet().getDescription());
            item.setVideoUrlId(video.getId());
            data.add(item);
        }

        return data;
    }
}
