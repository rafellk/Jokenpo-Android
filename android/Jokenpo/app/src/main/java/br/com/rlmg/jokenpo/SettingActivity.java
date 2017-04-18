package br.com.rlmg.jokenpo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.util.Locale;

import br.com.rlmg.jokenpo.utils.Utils;

public class SettingActivity extends PreferenceActivity  {

    public static final String KEY_PREF_LANGUAGE = "settingLanguage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.settings_title));
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        Context context;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final ListPreference listPreference = (ListPreference) findPreference(KEY_PREF_LANGUAGE);

            listPreference.setValue(String.valueOf(getDefaultValueLocale()));

            context = getActivity();

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.valueOf((String) newValue);
                    switch (value){
                        case 1:
                            setLocale("pt", "BR");
                            break;
                        case 2:
                            setLocale("en", "US");
                            break;
                    }
                    SettingsFragment fragment = new SettingsFragment();
                    getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

                    return true;
                }
            });
        }

        private void setLocale(String lang, String region) {
            Locale locale = new Locale(lang, region);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }

        private int getDefaultValueLocale(){
            Locale current = getResources().getConfiguration().locale;

            Integer index = 0;

            if(Languages.Portugues.getLanguage().equals(current.getLanguage())){
                index = Languages.Portugues.getIndex();
            }
            else if(Languages.English.getLanguage().equals(current.getLanguage())){
                index = Languages.English.getIndex();
            }

            return index;
        }

        private enum Languages{
            Portugues(1, "pt"),
            English(2, "en");

            private int index;
            private String language;

            Languages(int index, String language){
                this.index = index;
                this.language = language;
            }

            public int getIndex(){
                return index;
            }

            public String getLanguage(){
                return language;
            }
        }
    }
}
