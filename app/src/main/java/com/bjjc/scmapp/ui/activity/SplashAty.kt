package com.bjjc.scmapp.ui.activity

import android.view.animation.*
import com.bjjc.scmapp.R
import com.bjjc.scmapp.model.entity.VersionEntity
import com.bjjc.scmapp.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.layout_aty_splash.*

class SplashAty : BaseActivity(), Animation.AnimationListener {

    override fun getLayoutId(): Int = R.layout.layout_aty_splash

    override fun initView() = playAnimation()

    private fun playAnimation() {
        //RotateAnimation
        val animRotate =
            RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
                .apply {
                    duration = 1000
                    fillAfter = true
                }
        //ScaleAnimation
        val animScale =
            ScaleAnimation(
                0f,
                1f,
                0f,
                1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
                .apply {
                    duration = 1000
                    fillAfter = true
                }
        //AlphaAnimation
        val animAlpha = AlphaAnimation(0f, 1f)
            .apply {
                duration = 2000
                fillAfter = true
            }

        //AnimationSet
        val animSet = AnimationSet(true).apply {
            addAnimation(animRotate)
            addAnimation(animScale)
            addAnimation(animAlpha)
            setAnimationListener(this@SplashAty)
        }

        //start animation.
        ivRoot.startAnimation(animSet)
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
        if (VersionEntity.isNewlyInstalled) startActivityAndFinish<GuideAty>()else startActivityAndFinish<LoginActivity>()
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}
