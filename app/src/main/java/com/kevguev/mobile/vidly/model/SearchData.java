package com.kevguev.mobile.vidly.model;

import com.kevguev.mobile.vidly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchData {

    private static final String[] titles = {"Nothingness cannot be defined",
            "Time is like a river made up of the events which happen, and a violent stream; " +
                    "for as soon as a thing has been seen, it is carried away, and another comes" +
                    " in its place, and this will be carried away too,",
            "But when I know that the glass is already broken, every minute with it is precious.",
            "For me, it is far better to grasp the Universe as it really is than to persist in" +
                    " delusion, however satisfying and reassuring.",
            "The seeker after the truth is not one who studies the writings of the ancients and," +
                    " following his natural disposition, puts his trust in them, but rather the" +
                    " one who suspects his faith in them and questions what he gathers from them," +
                    " the one who submits to argument and demonstration, and not to the " +
                    "sayings of a human being whose nature is fraught with all kinds " +
                    "of imperfection and deficiency.",
            "You must take personal responsibility. You cannot change the circumstances, the" +
                    " seasons, or the wind, but you can change yourself. That is something you" +
                    " have charge of."
    };
    private static final String[] subTitles = {"Bruce Lee",
            "Marcus Aurelius",
            "Meng Tzu",
            "Ajahn Chah",
            "Carl Sagan",
            "Alhazen",
            "Jim Rohn",
            "Kevin"

    };
    private static final int[] icons = {android.R.drawable.ic_popup_reminder,
            android.R.drawable.ic_menu_add, android.R.drawable.ic_menu_delete};

    private static final int icon = R.drawable.ic_tonality_black_36dp;
    public static List<ListItem> getListData(){

        List<ListItem> data = new ArrayList<>();
        //repeat process 4 times, so that we have enough data to determine a scrollable
        //recyclerview

        for (int i = 0; i < 4; i++) {
            //create ListItem with dummy data and then add it to our list
            for (int j = 0; j < titles.length; j++) {
                ListItem item = new ListItem();
                item.setImageResId(icon);
                item.setTitle(titles[j]);
                item.setSubtitle(subTitles[j]);
                data.add(item);
            }
        }
        return data;
    }
}
