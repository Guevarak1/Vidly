package com.kevguev.mobile.vidly.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.GeoPoint;
import com.google.api.services.youtube.model.Video;
import com.kevguev.mobile.vidly.App;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.SearchData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;
import static com.kevguev.mobile.vidly.Constants.PREF_ACCOUNT_NAME;
import static com.kevguev.mobile.vidly.Constants.REQUEST_AUTHORIZATION;

/**
 * Created by Kevin Guevara on 5/24/2017.
 */

public class MapLocationsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    public String lastLocation;
    ProgressDialog mProgress;
    //FloatingActionButton m
    // ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            lastLocation = bundle.getString(EXTRA_LOCATION, "");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling YouTube Data API ...");
//        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fabClicked(view);
//            }
//        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng currentLocation = parseLocationString(lastLocation);
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });
        return rootView;
    }

//    private void fabClicked(View view) {
//        mFab.setEnabled(false);
//        createDialog();
//        mFab.setEnabled(true);
//
//    }

//    private void createDialog() {
//
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String prefLocation = prefs.getString("pref_location", "defaultValue");
//        int prefLocationInt = locationToDialogInt(prefLocation);
//
//        new MaterialDialog.Builder(getActivity())
//                .title(R.string.dialog_location_title)
//                .items(R.array.locations)
//                .itemsCallbackSingleChoice(prefLocationInt, new MaterialDialog.ListCallbackSingleChoice() {
//
//                    @Override
//                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                        getResultsFromApi(toLatLng(which));
//                        return true;
//                    }
//                })
//                .positiveText(R.string.choose)
//                .show();
//    }

    private int locationToDialogInt(String prefLocation) {

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
            default:
                chosen = 0;
                break;

        }
        return chosen;
    }
    private String toLatLng(int i) {

        String chosen = "";
        String[] locationValuesList = getResources().getStringArray(R.array.locationsValues);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        switch (i) {

            case 0:
                chosen = lastLocation;
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
            default:
                break;

        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pref_location", chosen);
        editor.commit();

        return chosen;
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String prefLocation = prefs.getString("pref_location", "defaultValue");
//
//        getResultsFromApi(prefLocation);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private LatLng parseLocationString(String location) {

        String[] coord = location.split(",");
        double lat = Double.parseDouble(coord[0]);
        double lng = Double.parseDouble(coord[1]);
        return new LatLng(lat, lng);
    }

    public void updateGoogleMap(List<Video> output) {
        LatLng currentLocation = parseLocationString(lastLocation);
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Chosen Location"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (googleMap != null) {
            googleMap.clear();
            for (Video video : output) {
                GeoPoint locationDetails = video.getRecordingDetails().getLocation();
                LatLng coord = new LatLng(locationDetails.getLatitude(), locationDetails.getLongitude());
                googleMap.addMarker(new
                        MarkerOptions()
                        .position(coord)
                        .title(video.getSnippet().getTitle())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_local_play_black_36dp)));
            }
        }
    }
//    public class MakeLocationRequestTask extends AsyncTask<Void, Void, List<Video>> {
//
//        /**
//         * An asynchronous task that handles the YouTube Data API call.
//         * Placing the API calls in their own task ensures the UI stays responsive.
//         */
//        private Exception mLastError = null;
//        SearchData mSearchData;
//        String query, location, radius, publishedAfter;
//
//        public MakeLocationRequestTask(String publishedAfter, String location, String radius) {
//            //this.query = query;
//            this.publishedAfter = publishedAfter;
//            this.location = location;
//            this.radius = radius;
//        }
//
//        /**
//         * Background task to call YouTube Data API.
//         *
//         * @param params no parameters needed for this task.
//         */
//        @Override
//        protected List<Video> doInBackground(Void... params) {
//            try {
//                App app = ((App) getActivity().getApplicationContext());
//                mSearchData = new SearchData(app.getmCredential());
//                return mSearchData.getDataFromApi(publishedAfter, location, radius);
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        /**
//         * Fetch information about the "GoogleDevelopers" YouTube channel.
//         *
//         * @return List of Strings containing information about the channel.
//         * @throws IOException
//         */
//
//        @Override
//        protected void onPreExecute() {
//            mProgress.show();
//        }
//
//        @Override
//        protected void onPostExecute(List<Video> output) {
//            mProgress.hide();
//            if (output == null || output.size() == 0) {
//                Toast.makeText(getActivity(), "No results returned.", Toast.LENGTH_SHORT);
//
//            } else {
//                updateGoogleMap(output);
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mProgress.hide();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    ((MainActivity) getActivity()).showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            REQUEST_AUTHORIZATION);
//                } else {
//                    Toast.makeText(getActivity(), "The following error occurred:\n"
//                            + mLastError.getMessage(), Toast.LENGTH_SHORT);
//                }
//            } else {
//                Toast.makeText(getActivity(), "Request cancelled.", Toast.LENGTH_SHORT);
//            }
//        }
//    }
}
