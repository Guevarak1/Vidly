package com.kevguev.mobile.vidly.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.services.youtube.model.Video;
import com.kevguev.mobile.vidly.App;
import com.kevguev.mobile.vidly.YoutubeResponseListener;
import com.kevguev.mobile.vidly.ui.MainListFragment;

import java.util.List;

/**
 * Created by Kevin Guevara on 6/4/2017.
 */

public class MakeRequestTask extends AsyncTask<Void, Void, List<Video>> {

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private Exception mLastError = null;
    SearchData mSearchData;
    String query, location, radius, publishedAfter;
    Context context;
    public YoutubeResponseListener delegate = null;

    public MakeRequestTask(String publishedAfter, String location, String radius, Context context) {
        //this.query = query;
        this.publishedAfter = publishedAfter;
        this.location = location;
        this.radius = radius;
        this.context = context;
    }

    //on pre make mprogress show up

    @Override
    protected List<Video> doInBackground(Void... voids) {
        try {
            App app = ((App) context.getApplicationContext());
            mSearchData = new SearchData(app.getmCredential());
            return mSearchData.getDataFromApi(publishedAfter, location, radius);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Video> videos) {
        if(delegate!=null)
        {
            delegate.postResult(videos);
        }
        else
        {
            Log.e("ApiAccess", "You have not assigned IApiAccessResponse delegate");
        }
    }
}
