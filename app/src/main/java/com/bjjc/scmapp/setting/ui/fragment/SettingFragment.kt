package com.bjjc.scmapp.setting.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.AboutActivity


/**
 * Created by Allen on 2018/12/06 11:33
 */
class SettingFragment: PreferenceFragmentCompat() {
    val TAG = SettingFragment::class.java.simpleName
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key
        when(key){
            "about"->{
                startActivity(Intent(context, AboutActivity::class.java))
            }
            "clear_cache"->{
                Log.d(TAG,"点击了清除缓存")
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}