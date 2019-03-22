package com.bjjc.scmapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bjjc.scmapp.R
import com.bjjc.scmapp.util.UIUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Allen on 2019/03/18 11:58
 */
abstract class LoadingPage : FrameLayout {
    companion object {
        private const val STATE_LOAD_UNDO = 1
        private const val STATE_LOAD_LOADING = 2
        private const val STATE_LOAD_ERROR = 3
        private const val STATE_LOAD_EMPTY = 4
        private const val STATE_LOAD_SUCCESS = 5
    }

    private var mLoadingPage: View? = null
    private var mUnLoadingPage: View? = null
    private var mErrorPage: View? = null
    private var mEmptyPage: View? = null
    private var mSuccessPage: View? = null
    private var mCurrentState= STATE_LOAD_UNDO

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        //The layout of unloading.
        if (mUnLoadingPage == null) {
            mUnLoadingPage = UIUtils.inflate(R.layout.unloading_page)
            addView(mUnLoadingPage)
        }
        //The layout of loading.
        if (mLoadingPage == null) {
            mLoadingPage = UIUtils.inflate(R.layout.loading_page)
            addView(mLoadingPage)
        }
        //The layout of loading failure.
        if (mErrorPage == null) {
            mErrorPage = UIUtils.inflate(R.layout.error_page)
            addView(mErrorPage)
        }
        //The layout of Empty data.
        if (mEmptyPage == null) {
            mEmptyPage = UIUtils.inflate(R.layout.empty_page)
            addView(mEmptyPage)
        }
        showRightPage()
    }

    //Displays the correct layout based on the current state.
    private fun showRightPage() {
        mLoadingPage?.visibility =
            if (mCurrentState == STATE_LOAD_UNDO || mCurrentState == STATE_LOAD_LOADING) View.VISIBLE else View.GONE
        mErrorPage?.visibility=if (mCurrentState == STATE_LOAD_ERROR)View.VISIBLE else View.GONE
        mEmptyPage?.visibility=if (mCurrentState == STATE_LOAD_EMPTY)View.VISIBLE else View.GONE
        //Initializes layout of the success,when the success layout is null and the current state is success.
        if (mSuccessPage==null && mCurrentState== STATE_LOAD_SUCCESS){
            mSuccessPage=onCreateSuccessView()
            if (mSuccessPage!=null){
                addView(mSuccessPage)
            }
        }
        if(mSuccessPage!=null){
            mSuccessPage?.visibility=if (mCurrentState == STATE_LOAD_SUCCESS)View.VISIBLE else View.GONE
        }
    }

    abstract fun onCreateSuccessView():View
    abstract fun onLoad():ResultState

    fun loadData(){
        doAsync {
            val resultState:ResultState = onLoad()
            uiThread {
                mCurrentState=resultState.getSate()
                showRightPage()
            }
        }
    }

    enum class ResultState{
        STATE_SUCCESS(STATE_LOAD_SUCCESS),STATE_EMPTY(STATE_LOAD_SUCCESS),STATE_ERROR(STATE_LOAD_SUCCESS);
        private var mState:Int
        constructor(state:Int){
            mState = state
        }
        fun getSate():Int{
            return mState
        }

    }
}