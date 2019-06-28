package com.bjjc.scmapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.AboutAty
import com.hjq.toast.ToastUtils


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
                startActivity(Intent(context, AboutAty::class.java))
            }
            "clear_cache"->{
                ToastUtils.show("点击了清除缓存")
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}