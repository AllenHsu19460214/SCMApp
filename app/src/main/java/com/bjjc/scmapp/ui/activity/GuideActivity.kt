package com.bjjc.scmapp.ui.activity

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bjjc.scmapp.R
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.UIUtils
import kotlinx.android.synthetic.main.activity_guide.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity

class GuideActivity : BaseActivity() {
    private val mImageViewList: ArrayList<ImageView> = ArrayList()
    private val mImageIds: IntArray =
        intArrayOf(R.drawable.img_center_in, R.drawable.img_center_out, R.drawable.img_center_in_mode)
    private var mPointDis: Int = 0
    override fun getLayoutId() = R.layout.activity_guide
    override fun initView() {

    }

    override fun initData() {
        SPUtils.put(this,"isNewApp",false)
        for ((id, value) in mImageIds.withIndex()) {
            val view: ImageView = ImageView(this)
            view.backgroundResource = mImageIds[id]
            mImageViewList.add(view)
            //Initialize guide point.
            val point: ImageView = ImageView(this)
            point.setImageResource(R.drawable.shap_point_gray)
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (id > 0) {
                params.leftMargin = UIUtils.dp2px(20f)
            }
            point.layoutParams = params
            llContainer.addView(point)
        }
        vpGuide.adapter = GuideAdapter()
        vpGuide.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val leftMargin: Int = (mPointDis * positionOffset).toInt() + mPointDis * position
                val params: RelativeLayout.LayoutParams = ivRedPoint.layoutParams as RelativeLayout.LayoutParams
                params.leftMargin = leftMargin
                ivRedPoint.layoutParams = params
            }

            override fun onPageSelected(position: Int) {
                if (position == mImageViewList.size - 1) {
                    btnStart.visibility = View.VISIBLE
                } else {
                    btnStart.visibility = View.GONE
                }
            }

        })
        ivRedPoint.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ivRedPoint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mPointDis = llContainer.getChildAt(1).left - llContainer.getChildAt(0).left
            }

        })


    }

    override fun initListener() {
        btnStart.setOnClickListener {
            startActivity<LoginActivity>()
            finish()
        }
    }

    inner class GuideAdapter : PagerAdapter() {
        override fun isViewFromObject(obj0: View, obj1: Any): Boolean {
            return obj0 == obj1
        }

        override fun getCount(): Int {
            return mImageViewList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: ImageView = mImageViewList[position]
            container.addView(view)
            return view
        }

    }
}
