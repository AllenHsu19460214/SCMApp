package com.bjjc.scmapp.ui.fragment

import android.view.View
import android.widget.TextView
import com.bjjc.scmapp.ui.fragment.base.MyBaseFragment
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.widget.LoadingPage

/**
 * Created by Allen on 2019/03/19 13:09
 */
class HomeFragment:MyBaseFragment() {

    //Runs in a child thread and can perform time-consuming network operations
    override fun onCreateSuccessView(): View {
        return TextView(UIUtils.getContext())
    }
    //if Loading is successful,this method is invoked and run in main thread.
    override fun onLoad(): LoadingPage.ResultState {
        return LoadingPage.ResultState.STATE_SUCCESS
    }

}