package com.bjjc.scmapp.ui.activity

import android.location.Location
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.GpsUtils
import kotlinx.android.synthetic.main.layout_aty_about.*


class AboutActivity : BaseActivity() {
    companion object {
        private val TAG: String = AboutActivity::class.java.simpleName
    }

    private var mLocation: Location? = null
    override fun getLayoutId(): Int = R.layout.layout_aty_about
    override fun initData() {
        mLocation = GpsUtils.getGPSContacts(this)
        tv_latitude.text = mLocation?.latitude.toString()
        tv_longitude.text = mLocation?.longitude.toString()
    }
}
