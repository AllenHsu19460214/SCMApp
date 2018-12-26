package com.bjjc.scmapp.util

import android.app.Service
import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import com.bjjc.scmapp.R


/**
 * Created by Allen on 2018/12/24 11:15
 */
object FeedbackUtils {
     private fun voice(context: Context){
        MediaPlayer.create(context, R.raw.beep).start()
    }
    fun vibrate(context: Context,milliseconds:Long){
        (context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(milliseconds)
    }

     fun vibrate(context: Context, pattern: LongArray, isRepeat: Boolean) {
        (context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator).vibrate(pattern, if (isRepeat) 1 else -1)
    }
    fun voiceAndVibrator(context: Context,milliseconds:Long){
        voice(context)
        vibrate(context,milliseconds)
    }
}