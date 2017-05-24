package com.kevguev.mobile.vidly.model;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.kevguev.mobile.vidly.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchData {

    private static final int icon = R.drawable.ic_local_play_black_36dp;
    private com.google.api.services.youtube.YouTube mService = null;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;


    public SearchData(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.youtube.YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
    }

    public List<Video> getDataFromApi(String queryTerm, String location, String locationRadius) throws IOException {

        try {
            YouTube.Search.List search = mService.search().list("id,snippet");

            search.setQ(queryTerm);
            search.setLocation(location);
            search.setLocationRadius(locationRadius);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // As a best practice, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            List<String> videoIds = new ArrayList<String>();

            if (searchResultList != null) {

                // Merge video IDs
                for (SearchResult searchResult : searchResultList) {
                    videoIds.add(searchResult.getId().getVideoId());
                }
                Joiner stringJoiner = Joiner.on(',');
                String videoId = stringJoiner.join(videoIds);

                // Call the YouTube Data API's youtube.videos.list method to
                // retrieve the resources that represent the specified videos.
                YouTube.Videos.List listVideosRequest = mService.videos().list("snippet, recordingDetails").setId(videoId);
                VideoListResponse listResponse = listVideosRequest.execute();

                List<Video> videoList = listResponse.getItems();

                if (videoList != null) {
                    return videoList;
                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public List<ListItem> getListData(List<Video> videos) {

        List<ListItem> data = new ArrayList<>();

        //create ListItem with dummy data and then add it to our list
        for (Video video: videos) {
            ListItem item = new ListItem();
            item.setImageResId(icon);
            item.setTitle(video.getSnippet().getTitle());
            item.setSubtitle(video.getSnippet().getDescription());
            data.add(item);
        }

        return data;
    }
}
