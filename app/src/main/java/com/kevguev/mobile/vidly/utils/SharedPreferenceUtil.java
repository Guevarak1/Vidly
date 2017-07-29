package com.kevguev.mobile.vidly.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kevguev.mobile.vidly.model.ListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kevin Guevara on 7/25/2017.
 */

public class SharedPreferenceUtil {


    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Video_Favorite";

    public SharedPreferenceUtil() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<ListItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, ListItem item) {
        List<ListItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<ListItem>();
        favorites.add(item);
        saveFavorites(context, favorites);
    }

    //remove based on videoId
    public void removeFavorite(Context context, ListItem item) {
        ArrayList<ListItem> favorites = getFavorites(context);
        String videoId = item.getVideoUrlId();
        if (favorites != null) {
            for (ListItem favItem: favorites) {
                if(favItem.getVideoUrlId().equals(videoId)){
                    favorites.remove(favItem);
                    break;
                }
            }
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<ListItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<ListItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            ListItem[] favoriteItems = gson.fromJson(jsonFavorites,
                    ListItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<ListItem>(favorites);
        } else
            return null;

        return (ArrayList<ListItem>) favorites;
    }

    //TODO: listitem in favorites method
}
