package br.com.rlmg.jokenpo;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.util.Locale;

import br.com.rlmg.jokenpo.utils.Utils;

public class SettingActivity extends PreferenceActivity  {

    public static final String KEY_PREF_LANGUAGE = "settingLanguage";
    public static final String KEY_PREF_SOUND = "settingSound";
    public static final String KEY_PREF_NOTIFICATION = "settingNotification";
    public static final String KEY_PREF_FONT_SIZE = "settingFontSize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.settings_title));
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        Context context;
        private final int smallFont = 12;
        private final int normalFont = 15;
        private final int bigFont = 19;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference listLanguage = (ListPreference) findPreference(KEY_PREF_LANGUAGE);
            final ListPreference listFontSize = (ListPreference) findPreference(KEY_PREF_FONT_SIZE);
            final SwitchPreference switchSound = (SwitchPreference) findPreference(KEY_PREF_SOUND);
            final SwitchPreference switchNotification = (SwitchPreference) findPreference(KEY_PREF_NOTIFICATION);

            listLanguage.setValue(String.valueOf(getDefaultValueLocale()));
            listFontSize.setValue(String.valueOf(getDefaultValueFontSize()));
            setListPreferenceData(listFontSize);

            context = getActivity();

            listLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return setLanguage((String) newValue);
                }
            });

            listFontSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return setFontSize((String) newValue);
                }
            });

            switchSound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return adjustVolume((Boolean) newValue);
                }
            });

            switchNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Utils.sShowNotification = (Boolean) newValue;
                    return true;
                }
            });
        }

        private void setListPreferenceData(ListPreference listFontSize) {
            CharSequence[] entries = {
                    getResources().getString(R.string.settings_font_size_small),
                    getResources().getString(R.string.settings_font_size_normal),
                    getResources().getString(R.string.settings_font_size_large) };
            CharSequence[] entryValues = {"1" , "2", "3"};
            listFontSize.setEntries(entries);
            listFontSize.setDefaultValue("1");
            listFontSize.setEntryValues(entryValues);
        }

        private boolean adjustVolume(Boolean newValue) {
            Boolean enableSound = newValue;

            //AudioManager mAlramMAnager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

            if(enableSound) {
                //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            }
            else{
                //mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            }

            return true;
        }

        private boolean setLanguage(String newValue) {
            int value = Integer.valueOf(newValue);
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

        private boolean setFontSize(String newValue) {
            int value = Integer.valueOf(newValue);
            switch (value){
                case 1:
                    Utils.sSTextSize = smallFont;
                    break;
                case 2:
                    Utils.sSTextSize = normalFont;
                    break;
                case 3:
                    Utils.sSTextSize = bigFont;
                    break;
            }

            return true;
        }

        private int getDefaultValueFontSize(){
            switch (Utils.sSTextSize){
                case smallFont:
                    return 1;
                case normalFont:
                    return 2;
                case bigFont:
                    return 3;
            }

            return 1;
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