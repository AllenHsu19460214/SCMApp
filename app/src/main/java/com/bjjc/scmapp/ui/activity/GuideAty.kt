package com.bjjc.scmapp.ui.activity

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import com.bjjc.scmapp.util.SPUtils
import com.bjjc.scmapp.util.UIUtils
import kotlinx.android.synthetic.main.layout_aty_guide.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity



class GuideAty : BaseActivity() {
    private val imageViewList: ArrayList<ImageView> = ArrayList()
    private val imageIdList: IntArray =
        intArrayOf(
            com.bjjc.scmapp.R.drawable.img_center_in,
            com.bjjc.scmapp.R.drawable.img_center_out,
            com.bjjc.scmapp.R.drawable.img_center_in_mode
        )
    private var pointDis: Int = 0
    override fun getLayoutId() = com.bjjc.scmapp.R.layout.layout_aty_guide

    override fun initData() {

        for (index in imageIdList.indices) {
            imageViewList.add(ImageView(this).apply { backgroundResource = imageIdList[index] })
            //Initialize guide point and put it into llContainer.
            val point = ImageView(this).apply {
                setImageResource(com.bjjc.scmapp.R.drawable.shap_point_gray)
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                //"index > 0" means Set parameters for other points except the first point.
                if (index > 0) {
                    params.leftMargin = UIUtils.dp2px(20f)
                }
                layoutParams = params
            }
            llContainer.addView(point)
        }
        with(vpGuide){
            adapter = GuideAdapter()
            setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    //position index of the current page.
                    //positionOffset offset percentage of the current page.(percentage of the single page)
                    //positionOffsetPixels pixel offset of the current page.
                    val leftMargin: Int = (pointDis * positionOffset).toInt() + pointDis * position
                    val params: RelativeLayout.LayoutParams = ivRedPoint.layoutParams as RelativeLayout.LayoutParams
                    params.leftMargin = leftMargin
                    ivRedPoint.layoutParams = params

                }

                override fun onPageSelected(position: Int) {
                    if (position == imageViewList.size - 1) {
                        btnStart.visibility = View.VISIBLE
                    } else {
                        btnStart.visibility = View.GONE
                    }
                }

            })
        }
        //Gets the distance between points.
        // the method ,viewTreeObserver ,is invoke when changes occurs ,such as layout of the view tree changing, the focus of the view tree changing,
        // the view tree being drawn or the view tree scrolling,etc
        ivRedPoint.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //No more notifications when the view tree layout changes
                ivRedPoint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                pointDis = llContainer.getChildAt(1).left - llContainer.getChildAt(0).left
            }

        })


    }

    override fun initListener() {
        btnStart.setOnClickListener {
            SPUtils.put("isNewlyInstalled",false)
            startActivity<LoginActivity>()
            finish()
        }
    }

    inner class GuideAdapter : PagerAdapter() {
        override fun isViewFromObject(obj0: View, obj1: Any): Boolean {
            return obj0 == obj1
        }

        override fun getCount(): Int {
            return imageViewList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: ImageView = imageViewList[position]
            container.addView(view)
            return view
        }

    }
}
