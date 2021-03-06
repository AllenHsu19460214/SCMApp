package com.bjjc.scmapp.ui.fragment.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

/**
 * Created by Allen on 2018/11/29 9:59
 * BaseClass for all of Fragments
 */
abstract class BaseFragment:Fragment() ,AnkoLogger{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView()
    }

    /**
     * To obtain view of layout.
     */
    abstract fun initView(): View?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        initData()
    }
    protected open fun init(){

    }
    /**
     * To initialize Data.
     */
    protected open fun initData() {
    }

    /**
     * To initialize Listeners and Adapters.
     */
    protected open fun initListener() {
    }
    open fun myToast(msg:String){
        context?.runOnUiThread { toast(msg) }
    }
}