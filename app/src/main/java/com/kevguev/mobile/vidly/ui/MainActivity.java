package com.kevguev.mobile.vidly.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.kevguev.mobile.vidly.AppUtils;
import com.kevguev.mobile.vidly.BottomNavigationViewBehavior;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.PostResultsListener;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.ScrollAnimationFab;
import com.kevguev.mobile.vidly.model.SearchData;
import com.kevguev.mobile.vidly.model.jsonpojo.videoids.Items;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Item;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Videos;
import com.kevguev.mobile.vidly.model.retrofit.SearchEndpointInterface;
import com.kevguev.mobile.vidly.ui.adapter.ViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kevguev.mobile.vidly.Constants.EXTRA_CURRENT_LOCATION;
import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;
import static com.kevguev.mobile.vidly.Constants.FIELDS;
import static com.kevguev.mobile.vidly.Constants.NUMBER_OF_VIDEOS_RETURNED;
import static com.kevguev.mobile.vidly.Constants.PREF_ACCOUNT_NAME;
import static com.kevguev.mobile.vidly.Constants.RECORDING_DETAILS_PART;
import static com.kevguev.mobile.vidly.Constants.REQUEST_ACCOUNT_PICKER;
import static com.kevguev.mobile.vidly.Constants.REQUEST_AUTHORIZATION;
import static com.kevguev.mobile.vidly.Constants.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.kevguev.mobile.vidly.Constants.SETTINGS_RESULT;
import static com.kevguev.mobile.vidly.Constants.SNIPPET_PART;
import static com.kevguev.mobile.vidly.Constants.VIDEO;

//find out whats being uploaded near you
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String API_KEY = "AIzaSyCm0Kx6byqy64NO1f5XDAOoRr7jD9EZCyM";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    SearchEndpointInterface apiService = retrofit.create(SearchEndpointInterface.class);

    protected static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    ProgressDialog mProgress;
    FloatingActionButton mFab;
    ViewPagerAdapter adapter;
    String currentLocation;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};
    private PostResultsListener postResultsListener;

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

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });


        Intent i = getIntent();
        if (i != null) {
            currentLocation = i.getStringExtra(EXTRA_CURRENT_LOCATION);
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        //// TODO: 7/20/2017
        // fab communicates with list and map fragment. switches hold the video data between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                Bundle bundle;
                switch (item.getItemId()) {
                    case R.id.action_item1:
                        selectedFragment = MainListFragment.newInstance();
                        break;
                    case R.id.action_item2:
                        selectedFragment = MapLocationsFragment.newInstance();
                        bundle = new Bundle();
                        bundle.putString(EXTRA_LOCATION, currentLocation);
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.action_item3:
                        selectedFragment = FavoritesFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;

            }
        });

        CoordinatorLayout.LayoutParams layoutParamsBottonNav = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParamsBottonNav.setBehavior(new BottomNavigationViewBehavior());

        CoordinatorLayout.LayoutParams layoutParamsfab= (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        layoutParamsfab.setBehavior(new ScrollAnimationFab());

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, MainListFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

//viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager, currentLocation);
//
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

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
            default:
                if(resultCode == SETTINGS_RESULT){
                    getResultsFromApi();
                }
                break;

        }
    }

    private void fabClicked(View view) {
        mFab.setEnabled(false);
        createDialog();
        mFab.setEnabled(true);
    }

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
            int publishedAfter = Integer.parseInt(prefs.getString(getString(R.string.pref_published_after), "1")); // published after 1 day
            String radius = prefs.getString(getString(R.string.pref_radius), "1km");

            mProgress.show(); // on pre
            getVideos(locationPicked, radius, getPastDate(publishedAfter));
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

    public void setupViewPager(ViewPager viewPager, String currentLocation) {
        adapter = new ViewPagerAdapter(this, getSupportFragmentManager(), currentLocation);
        adapter.addFragment(new MainListFragment(), "LIST");
        adapter.addFragment(new MapLocationsFragment(), "MAP");
        adapter.addFragment(new FavoritesFragment(), "FAV");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    //feed the results into the other fragments
    public void postResult(List<Item> videos) {
        if (videos == null || videos.size() == 0) {
            mProgress.hide();
            Toast.makeText(this, "No results returned.", Toast.LENGTH_SHORT).show();
        } else {

            mProgress.hide();
            //populate list
            MainListFragment listFragment = (MainListFragment) MainListFragment.newInstance();
            SearchData mSearchData = new SearchData(mCredential);
            listFragment.listData = (ArrayList) mSearchData.getListData(videos);
            listFragment.adapter.setListData(listFragment.listData);
            listFragment.adapter.notifyDataSetChanged();
//
//            //populate map !
//            MapLocationsFragment mapFragment = (MapLocationsFragment) adapter.getRegisteredFragment(1);
//            mapFragment.lastLocation = videos.get(0).getRecordingDetails().getLocation().getLatitude() + ", " + videos.get(0).getRecordingDetails().getLocation().getLongitude();
//            mapFragment.updateGoogleMap(videos);
        }
    }

    public void setPostResultsListener(PostResultsListener l){
        postResultsListener = l;
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
            startActivityForResult(myIntent, SETTINGS_RESULT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //embedded web service call because we want to wait for
    //location ids first then get the location ids data
    public void getVideos(String location, String locationRadius, String publishedAfter) {

        Call<Items> itemsCall = apiService.getVideoIds(API_KEY,
                SNIPPET_PART,
                location,
                locationRadius,
                NUMBER_OF_VIDEOS_RETURNED,
                publishedAfter,
                VIDEO,
                FIELDS);

        itemsCall.enqueue(new Callback<Items>() {
            @Override
            public void onResponse(Call<Items> call, Response<Items> response) {
                int statusCode = response.code();

                Items items = response.body();
                List<String> videoIds = new ArrayList<String>();

                for (com.kevguev.mobile.vidly.model.jsonpojo.videoids.Item item : items.getItems()) {
                    videoIds.add(item.getId().getVideoId());
                }

                Joiner stringJoiner = Joiner.on(',');
                String id = stringJoiner.join(videoIds);

                Call<Videos> videosCall = apiService.getVideos(API_KEY,
                        RECORDING_DETAILS_PART,
                        id);

                videosCall.enqueue(new Callback<Videos>() {
                    @Override
                    public void onResponse(Call<Videos> call, Response<Videos> response) {
                        int statusCode = response.code();
                        Videos videos = response.body();
                        if (videos == null || videos.getItems().size() == 0) {
                            mProgress.hide();
                            Toast.makeText(getBaseContext(), "No results returned.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            List<Item> videoItems = videos.getItems();
                            postResultsListener.postResultsToFragment(videoItems, mCredential);
                            mProgress.hide();
                        }
                    }

                    @Override
                    public void onFailure(Call<Videos> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<Items> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getPastDate(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - days);

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(cal.getTime());

    }
}
