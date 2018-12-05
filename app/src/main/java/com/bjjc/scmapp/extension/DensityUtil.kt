package com.bjjc.scmapp.extension

import android.content.Context
import android.util.TypedValue

/**
 * Created by Allen on 2018/11/29 11:37
 */
/**
 * Int extension
 * dp转px
 */
fun Int.dp2px(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(), context.resources.displayMetrics
    ).toInt()
}