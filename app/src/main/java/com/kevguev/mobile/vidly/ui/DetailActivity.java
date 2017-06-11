package com.kevguev.mobile.vidly.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.ListItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    private FloatingActionButton mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private ImageView thumbnail;
    TextView header;
    TextView description;
    ListItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle data = getIntent().getExtras();
        item = data.getParcelable(BUNDLE_EXTRAS);

        mFab = (FloatingActionButton) findViewById(R.id.flexible_example_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);
        toolbar.setTitle("Watch");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);

        thumbnail = (ImageView) findViewById(R.id.thumbnail_view);
        Picasso.with(this).load(item.getImgUrl()).fit().into(thumbnail);

        header = (TextView) findViewById(R.id.text_header);
        header.setText(item.getTitle());
        description = (TextView) findViewById(R.id.description_text);
        description.setText(item.getSubtitle());
    }

    private void fabClicked() {
        Intent intent = new Intent(
                Intent.ACTION_VIEW ,
                Uri.parse("https://www.youtube.com/watch?v=" + item.getVideoUrlId()));
        //intent.setComponent(new ComponentName(YOUTUBE_PACKAGE_NAME,"com.google.android.youtube.PlayerActivity"));
        PackageManager manager = getPackageManager();
        if(isPackageInstalled(YOUTUBE_PACKAGE_NAME, manager)){
            startActivity(intent);
        }else {
            //No Application can handle your intent
            Toast.makeText(this, "Install the Youtube app", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }
}
