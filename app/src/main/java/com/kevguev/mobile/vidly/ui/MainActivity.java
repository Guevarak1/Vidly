package com.kevguev.mobile.vidly.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.kevguev.mobile.vidly.App;
import com.kevguev.mobile.vidly.AppUtils;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.YoutubeResponseListener;
import com.kevguev.mobile.vidly.model.MakeRequestTask;
import com.kevguev.mobile.vidly.model.SearchData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.kevguev.mobile.vidly.Constants.EXTRA_CURRENT_LOCATION;
import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;
import static com.kevguev.mobile.vidly.Constants.PREF_ACCOUNT_NAME;
import static com.kevguev.mobile.vidly.Constants.REQUEST_ACCOUNT_PICKER;
import static com.kevguev.mobile.vidly.Constants.REQUEST_AUTHORIZATION;
import static com.kevguev.mobile.vidly.Constants.REQUEST_GOOGLE_PLAY_SERVICES;

//find out whats being uploaded near you
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        YoutubeResponseListener {

    protected static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ProgressDialog mProgress;
    FloatingActionButton mFab;
    ViewPagerAdapter adapter;
    String currentLocation;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};


    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCredential = GoogleAccountCredential.usingOAuth2(
                this.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Retrieving Videos...");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent i = getIntent();
        if (i != null) {
            currentLocation = i.getStringExtra(EXTRA_CURRENT_LOCATION);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefLocation = prefs.getString(getString(R.string.pref_location), null);

        if (prefLocation != null && !prefLocation.equals("0,0")) {
            getResultsFromApi();
        } else {
            if (currentLocation != null) {
                getResultsFromApi(currentLocation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = ((App) getApplicationContext());
        app.setmCredential(null);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    //getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        //getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //getResultsFromApi();
                }
                break;
        }
    }

    private void fabClicked(View view) {
        mFab.setEnabled(false);
        createDialog();
        mFab.setEnabled(true);
    }

    //TODO: dialog keeps initializing to ny
    private void createDialog() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefLocation = prefs.getString(getString(R.string.pref_location), "defaultValue");
        int prefLocationInt = AppUtils.locationToDialogInt(prefLocation);

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_location_title)
                .items(R.array.locations)
                .itemsCallbackSingleChoice(prefLocationInt, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        getResultsFromApi(AppUtils.toLatLng(which, currentLocation, getApplicationContext()));
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }


    private void getResultsFromApi() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefLocation = prefs.getString(getString(R.string.pref_location), "defaultValue");
        getResultsFromApi(prefLocation);
    }

    private void getResultsFromApi(String locationPicked) {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!AppUtils.isDeviceOnline(this)) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT);
        } else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //String prefLocation = prefs.getString(getString(R.string.pref_location), "defaultValue");
            String publishedAfter = prefs.getString(getString(R.string.pref_published_after), "day"); // published after 1 day
            String radius = prefs.getString(getString(R.string.pref_radius), "1km");

            mProgress.show(); // on pre
            MakeRequestTask request = new MakeRequestTask(publishedAfter, locationPicked, radius, this);
            request.delegate = this;
            request.execute();
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            AppUtils.showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, this);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    public void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = this.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                ((App) getApplicationContext()).setmCredential(mCredential);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("pref_location", currentLocation);

                getResultsFromApi(currentLocation);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainListFragment(), "LIST");
        adapter.addFragment(new MapLocationsFragment(), "MAP");
        adapter.addFragment(new FavoritesFragment(), "FAV");
        viewPager.setAdapter(adapter);
    }

    //feed the results into the other fragments
    @Override
    public void postResult(List<Video> videos) {
        if (videos == null || videos.size() == 0) {
            mProgress.hide();
            Toast.makeText(this, "No results returned.", Toast.LENGTH_SHORT).show();
        } else {

            mProgress.hide();
            //populate list
            MainListFragment listFragment = (MainListFragment) adapter.getRegisteredFragment(0);
            SearchData mSearchData = new SearchData(((App) getApplicationContext()).getmCredential());
            listFragment.listData = (ArrayList) mSearchData.getListData(videos);
            listFragment.adapter.setListData(listFragment.listData);
            listFragment.adapter.notifyDataSetChanged();

            //populate map !
            MapLocationsFragment mapFragment = (MapLocationsFragment) adapter.getRegisteredFragment(1);
            mapFragment.lastLocation = videos.get(0).getRecordingDetails().getLocation().getLatitude() + ", " + videos.get(0).getRecordingDetails().getLocation().getLongitude();
            mapFragment.updateGoogleMap(videos);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            String location = currentLocation;
            Fragment fragment;
            Bundle bundle;
            switch (position) {
                case 0:
                    fragment = new MainListFragment();
                    break;
                case 1:
                    fragment = new MapLocationsFragment();
                    bundle = new Bundle();
                    bundle.putString(EXTRA_LOCATION, location);
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new FavoritesFragment();
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
            //return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
