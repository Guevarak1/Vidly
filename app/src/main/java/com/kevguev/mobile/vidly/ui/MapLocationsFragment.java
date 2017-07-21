package com.kevguev.mobile.vidly.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.GeoPoint;
import com.google.api.services.youtube.model.Video;
import com.kevguev.mobile.vidly.App;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.PostResultsListener;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.ListItem;
import com.kevguev.mobile.vidly.model.SearchData;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;
import static com.kevguev.mobile.vidly.Constants.PREF_ACCOUNT_NAME;
import static com.kevguev.mobile.vidly.Constants.REQUEST_AUTHORIZATION;

/**
 * Created by Kevin Guevara on 5/24/2017.
 */

public class MapLocationsFragment extends Fragment implements PostResultsListener {

    MapView mMapView;
    private GoogleMap googleMap;
    public String lastLocation;
    ProgressDialog mProgress;

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

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setPostResultsListener(this);

        return rootView;
    }

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

    public void updateGoogleMap(List<Item> videos) {
        LatLng currentLocation = parseLocationString(lastLocation);
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Chosen Location"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (googleMap != null) {
            googleMap.clear();
            for (Item video : videos) {
                Double latitude = video.getRecordingDetails().getLocation().getLatitude();
                Double longitude = video.getRecordingDetails().getLocation().getLongitude();
                LatLng coord = new LatLng(latitude, longitude);

                String title = video.getSnippet().getTitle();
                String subtitle = video.getSnippet().getDescription();
                String videoUrl = video.getId();
                String imgUrl = video.getSnippet().getThumbnails().getMedium().getUrl();
                ListItem item = new ListItem(title, imgUrl, videoUrl, subtitle);

                googleMap.addMarker(new
                        MarkerOptions()
                        .position(coord)
                        .title(title)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
                        .setTag(item);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        ListItem item = (ListItem) marker.getTag();
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        i.putExtra(Constants.BUNDLE_EXTRAS, item);
                        startActivity(i);
                        return false;
                    }
                });
            }
        }
    }

    public static Fragment newInstance() {
        MapLocationsFragment fragment = new MapLocationsFragment();
        return fragment;
    }

    @Override
    public void postResultsToFragment(List<Item> videos, GoogleAccountCredential mCredential) {
        //populate map !
        lastLocation = videos.get(0).getRecordingDetails().getLocation().getLatitude() + ", " + videos.get(0).getRecordingDetails().getLocation().getLongitude();
        updateGoogleMap(videos);

    }
}
