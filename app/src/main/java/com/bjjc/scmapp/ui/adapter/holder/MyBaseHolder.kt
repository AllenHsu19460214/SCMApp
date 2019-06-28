package com.bjjc.scmapp.ui.adapter.holder

import android.view.View

/**
 * Created by Allen on 2019/03/19 8:39
 */
abstract class MyBaseHolder<T>{
    /**
     * The root layout of the view.
     */
    private var mRootView: View? = null
    private var mData: T? = null

    init {
        init()
    }

    /**
     * Will load layout,initialize component and set tag,when the object is created.
     */
    private fun init() {
        mRootView = initView()
        //3.Make a mark.
        mRootView?.tag = this
    }

    /**
     * 1.Loads layout file.
     * 2.Initialize the component.
     */
    abstract fun initView(): View?

    //The layout object is returned.
    fun getRootView(): View?{
        return mRootView
    }
    //The data of the current item.
    fun setData(data: T) {
        mData = data
        refreshView(data)
    }
    //Gets the data of the current item.
    fun getData():T?{
        return mData
    }
    //4.Refresh the interface according to the data.
    abstract fun refreshView(data: T)
}