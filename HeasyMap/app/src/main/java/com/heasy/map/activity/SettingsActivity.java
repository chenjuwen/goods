package com.heasy.map.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2020/4/4.
 */
public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new SettingsPreferenceFragment())
                .commit();
    }

}
