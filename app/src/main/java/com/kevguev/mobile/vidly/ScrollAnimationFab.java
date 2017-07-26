package com.kevguev.mobile.vidly;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;

/**
 * Created by Kevin Guevara on 7/19/2017.
 */

public class ScrollAnimationFab extends FloatingActionButton.Behavior {

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
            // we dodge any Snackbars
            lp.dodgeInsetEdges = Gravity.BOTTOM;
        }
    }
}
