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

public class MainListFragment extends Fragment implements SearchAdapter.ItemClickCallback, YoutubeResponseListener{

    ProgressDialog mProgress;
    private RecyclerView recView;
    public SearchAdapter adapter;
    public ArrayList listData;
    private String lastLocation;

    public MainListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            lastLocation = bundle.getString(EXTRA_LOCATION, "");
        }
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //TODO: change getResultApi to pull from current location
    //issue were after permissions are granted, user's location will be set to ny
    //figure out how to get which location user has chosen post permission
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getActivity(), "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT);
                } else {
                    getResultsFromApi("40.7417544,-74.0086348");
                }
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        App app = ((App)getActivity().getApplicationContext());
                        app.getmCredential().setSelectedAccountName(accountName);
                        getResultsFromApi("40.7417544,-74.0086348");
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi("40.7417544,-74.0086348");
                }
                break;
        }
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

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi(String locationChosen) {

        App app = ((App)getActivity().getApplicationContext());
        String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if (!((MainActivity)getActivity()).isGooglePlayServicesAvailable()) {
            ((MainActivity)getActivity()).acquireGooglePlayServices();
        } else if (accountName != null) {

            app.getmCredential().setSelectedAccountName(accountName);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String publishedAfter = prefs.getString(getString(R.string.pref_published_after), "day"); // published after 1 day
            String radius = prefs.getString(getString(R.string.pref_radius), "1km");

            mProgress.show();
            MakeRequestTask requestTask = new MakeRequestTask(publishedAfter, locationChosen, radius, getActivity());
            requestTask.delegate = this;
            requestTask.execute();

            //issue where mCredentials.setName erases after every launch
        } else if (app.getmCredential().getSelectedAccountName() == null) {
            ((MainActivity)getActivity()).chooseAccount();
        } else if (!((MainActivity)getActivity()).isDeviceOnline()) {
            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void postResult(List<Video> asyncresult) {
        mProgress.hide();
            if (asyncresult == null || asyncresult.size() == 0) {
                Toast.makeText(getActivity(), "No results returned.", Toast.LENGTH_SHORT);

            } else {
                SearchData mSearchData = new SearchData(((App)getActivity().getApplicationContext()).getmCredential());
                listData = (ArrayList) mSearchData.getListData(asyncresult);
                adapter.setListData(listData);
                adapter.notifyDataSetChanged();
            }

    }
//    public class MakeRequestTask extends AsyncTask<Void, Void, List<Video>> {
//
//        /**
//         * An asynchronous task that handles the YouTube Data API call.
//         * Placing the API calls in their own task ensures the UI stays responsive.
//         */
//        private Exception mLastError = null;
//        SearchData mSearchData;
//        String query, location, radius, publishedAfter;
//
//        public MakeRequestTask(String publishedAfter, String location, String radius) {
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
//                App app = ((App)getActivity().getApplicationContext());
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
//                listData = (ArrayList) mSearchData.getListData(output);
//                adapter.setListData(listData);
//                adapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mProgress.hide();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    ((MainActivity)getActivity()).showGooglePlayServicesAvailabilityErrorDialog(
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
