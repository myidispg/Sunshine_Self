package com.example.prashantgoyal.sunshine_self;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Prashant Goyal on 13-03-2018.
 * This class is for settings to be displayed when user clicks on settings inside menu
 */

public class SettingsFragment extends PreferenceFragmentCompat{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
    // after adding preference from resource here, go to activity_settings
}
