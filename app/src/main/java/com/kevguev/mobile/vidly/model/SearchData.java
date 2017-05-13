package com.kevguev.mobile.vidly.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchData {

    private static final String[] titles = {"Hello", "Recycler","World"};
    private static final int[] icons = {android.R.drawable.ic_popup_reminder,
            android.R.drawable.ic_menu_add, android.R.drawable.ic_menu_delete};

    public static List<ListItem> getListData(){

        List<ListItem> data = new ArrayList<>();
        //repeat process 4 times, so that we have enough data to determine a scrollable
        //recyclerview

        for (int i = 0; i < 4; i++) {
            //create ListItem with dummy data and then add it to our list
            for (int j = 0; j < titles.length && j < icons.length; j++) {
                ListItem item = new ListItem();
                item.setImageResId(icons[j]);
                item.setTitle(titles[j]);
                data.add(item);
            }
        }
        return data;
    }
}
