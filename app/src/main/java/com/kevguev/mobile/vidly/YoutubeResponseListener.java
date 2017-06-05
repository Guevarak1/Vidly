package com.kevguev.mobile.vidly;

import com.google.api.services.youtube.model.Video;

import java.util.List;

/**
 * Created by Kevin Guevara on 6/4/2017.
 */

public interface YoutubeResponseListener {
    void postResult(List<Video> asyncresult);
}