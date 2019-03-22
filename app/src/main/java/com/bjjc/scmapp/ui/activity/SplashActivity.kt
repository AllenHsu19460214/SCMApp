package com.bjjc.scmapp.ui.activity

import android.view.animation.*
import com.bjjc.scmapp.R
import com.bjjc.scmapp.app.App
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.layout_aty_splash.*
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(), Animation.AnimationListener {


    override fun getLayoutId(): Int {
        return R.layout.layout_aty_splash
    }

    override fun initView() {
        playAnimation()
    }

    private fun playAnimation() {
        //RotateAnimation
        val animRotate =
            RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animRotate.duration = 1000
        animRotate.fillAfter = true
        //ScaleAnimation
        val animScale =
            ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animScale.duration = 1000
        animScale.fillAfter = true
        //AlphaAnimation
        val animAlpha = AlphaAnimation(0f, 1f)
        animAlpha.duration = 2000
        animAlpha.fillAfter = true
        //AnimationSet
        val animSet = AnimationSet(true)
        animSet.addAnimation(animRotate)
        animSet.addAnimation(animScale)
        animSet.addAnimation(animAlpha)
        //start animation.
        ivRoot.startAnimation(animSet)
        animSet.setAnimationListener(this)
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
        if (App.isNewApp){
            startActivity<GuideActivity>()
        }else{
            startActivity<LoginActivity>()
        }
        finish()
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}
