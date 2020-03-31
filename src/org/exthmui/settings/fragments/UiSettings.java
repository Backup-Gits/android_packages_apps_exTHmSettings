/*
 * Copyright (C) 2016-2019 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exthmui.settings.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.exthmui.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import org.exthmui.settings.R;
import org.exthmui.settings.fragments.ui.Animations;
import org.exthmui.settings.fragments.ui.DozeSettings;
import org.exthmui.settings.fragments.ui.RoundedCorners;
import org.exthmui.settings.fragments.ui.SmartPixels;
import org.exthmui.settings.fragments.ui.ThemeSettings;

import java.util.ArrayList;
import java.util.List;

import lineageos.providers.LineageSettings;

public class UiSettings extends SettingsPreferenceFragment implements Indexable {

    private static final String SMART_PIXELS = "smart_pixels";
    private static final String DISPLAY_CUTOUT = "display_cutout_force_fullscreen_settings";

    private Preference mSmartPixels;
    private Preference mDisplayCutout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.exthm_settings_ui);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        mSmartPixels = (Preference) prefScreen.findPreference(SMART_PIXELS);
        boolean mSmartPixelsSupported = res.getBoolean(
                com.android.internal.R.bool.config_supportSmartPixels);
        if (!mSmartPixelsSupported)
            prefScreen.removePreference(mSmartPixels);

        final boolean hasNotch = res.getBoolean(
                org.lineageos.platform.internal.R.bool.config_haveNotch);

        mDisplayCutout = (Preference) prefScreen.findPreference(DISPLAY_CUTOUT);
        if (!hasNotch)
            prefScreen.removePreference(mDisplayCutout);
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Animations.reset(mContext);
        DozeSettings.reset(mContext);
        RoundedCorners.reset(mContext);
        SmartPixels.reset(mContext);
        ThemeSettings.reset(mContext);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EXTHMUI_SETTINGS;
    }

    /**
     * For search
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.exthm_settings_ui;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();

                    boolean hasNotch = res.getBoolean(
                            org.lineageos.platform.internal.R.bool.config_haveNotch);
                    if (!hasNotch)
                        keys.add(DISPLAY_CUTOUT);
                    boolean mSmartPixelsSupported = res.getBoolean(
                            com.android.internal.R.bool.config_supportSmartPixels);
                    if (!mSmartPixelsSupported)
                        keys.add(SMART_PIXELS);

                    return keys;
                }
            };
}
