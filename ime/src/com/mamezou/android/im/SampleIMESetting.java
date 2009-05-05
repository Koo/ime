package com.mamezou.android.im;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Locale & settingで選択できる設定画面
 * @author asanokouichi
 *
 */
public class SampleIMESetting extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
	}
}
