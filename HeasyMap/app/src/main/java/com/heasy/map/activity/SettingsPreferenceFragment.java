package com.heasy.map.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.support.annotation.Nullable;

import com.heasy.map.R;
import com.heasy.map.utils.PreferenceUtil;

/**
 * Created by Administrator on 2020/4/4.
 */
public class SettingsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从xml文件加载选项
        addPreferencesFromResource(R.xml.settings);

        //绑定值变更事件，并显示当前值
        Preference routePlanMode = getPreferenceManager().findPreference("routePlanMode");
        routePlanMode.setOnPreferenceChangeListener(this);
        routePlanMode.setSummary(PreferenceUtil.getStringValue(getActivity(), "routePlanMode"));

        Preference traceInterval = getPreferenceManager().findPreference("traceInterval");
        traceInterval.setOnPreferenceChangeListener(this);
        traceInterval.setSummary(PreferenceUtil.getStringValue(getActivity(), "traceInterval"));

        Preference pCheckBox = getPreferenceManager().findPreference("pCheckBox");
        pCheckBox.setOnPreferenceChangeListener(this);
        pCheckBox.setSummary(Boolean.toString(PreferenceUtil.getBooleanValue(getActivity(), "pCheckBox", false)));

        Preference pSwitch = getPreferenceManager().findPreference("pSwitch");
        pSwitch.setOnPreferenceChangeListener(this);
        pSwitch.setSummary(Boolean.toString(PreferenceUtil.getBooleanValue(getActivity(), "pSwitch", false)));

        Preference pMultiSelectList = getPreferenceManager().findPreference("pMultiSelectList");
        pMultiSelectList.setOnPreferenceChangeListener(this);
        pMultiSelectList.setSummary(PreferenceUtil.getStringSetValue(getActivity(), "pMultiSelectList"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            if(index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }
}
