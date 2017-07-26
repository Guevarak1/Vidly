package com.kevguev.mobile.vidly;

import android.view.View;

/**
 * Created by Kevin Guevara on 7/26/2017.
 */

public interface ItemClickCallback {
    void onThumbnailClicked(int p);

    void onLikeImageClicked(View v, int p);

    void onShareImageClicked(int p);
}
