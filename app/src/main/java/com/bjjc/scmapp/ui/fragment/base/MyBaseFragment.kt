package com.bjjc.scmapp.ui.fragment.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bjjc.scmapp.util.UIUtils
import com.bjjc.scmapp.widget.LoadingPage

/**
 * Created by Allen on 2019/03/19 13:12
 */
abstract class MyBaseFragment : Fragment() {

    private lateinit var loadingPage: LoadingPage
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadingPage = object : LoadingPage(UIUtils.getContext()) {
            override fun onCreateSuccessView(): View {
                return this@MyBaseFragment.onCreateSuccessView()
            }

            override fun onLoad(): ResultState {
                return this@MyBaseFragment.onLoad()
            }

        }
        return loadingPage
    }

    abstract fun onLoad(): LoadingPage.ResultState

    abstract fun onCreateSuccessView(): View

}