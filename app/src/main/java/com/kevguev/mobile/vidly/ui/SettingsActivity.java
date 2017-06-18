package com.kevguev.mobile.vidly.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kevguev.mobile.vidly.R;

import static com.kevguev.mobile.vidly.Constants.SETTINGS_RESULT;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.settings_frag_container, preferenceFragment);
            ft.commit();
        }
    }

    @Override
    public void finish() {
        setResult(SETTINGS_RESULT);
        super.finish();
    }
}
