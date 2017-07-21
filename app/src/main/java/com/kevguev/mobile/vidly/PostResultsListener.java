package com.kevguev.mobile.vidly;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Item;

import java.util.List;

/**
 * Created by Kevin Guevara on 7/19/2017.
 */

public interface  PostResultsListener {

    public void postResultsToFragment(List<Item> videos, GoogleAccountCredential mCredential    );

}
