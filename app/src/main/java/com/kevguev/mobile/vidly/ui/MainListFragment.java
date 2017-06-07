package com.kevguev.mobile.vidly.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.kevguev.mobile.vidly.App;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.YoutubeResponseListener;
import com.kevguev.mobile.vidly.adapter.SearchAdapter;
import com.kevguev.mobile.vidly.model.ListItem;
import com.kevguev.mobile.vidly.model.MakeRequestTask;
import com.kevguev.mobile.vidly.model.SearchData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;
import static com.kevguev.mobile.vidly.Constants.PREF_ACCOUNT_NAME;
import static com.kevguev.mobile.vidly.Constants.REQUEST_AUTHORIZATION;

import com.kevguev.mobile.vidly.Constants;

public class MainListFragment extends Fragment implements SearchAdapter.ItemClickCallback{

    ProgressDialog mProgress;
    private RecyclerView recView;
    public SearchAdapter adapter;
    public ArrayList listData;

    public MainListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling YouTube Data API ...");
        recView = (RecyclerView) view.findViewById(R.id.rec_list);
        //layout manager: girdlayout manager or staggered grid layout manager
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter(getActivity());
        adapter.setItemClickCallback(this);
        recView.setAdapter(adapter);

        return view;
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClick(int p) {
        ListItem item = (ListItem) listData.get(p);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        Bundle extras = new Bundle();

        extras.putString(Constants.EXTRA_QUOTE, item.getTitle());
        extras.putString(Constants.EXTRA_ATTR, item.getSubtitle());

        i.putExtra(Constants.BUNDLE_EXTRAS, extras);
        startActivity(i);
    }

    @Override
    public void onSecondaryIconClick(int p) {
        ListItem item = (ListItem) listData.get(p);
        //update our data
        if (item.isFavorite()) {
            item.setFavorite(false);
        } else {
            item.setFavorite(true);
        }
        // pass new data to adapter and update
        adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }
}
