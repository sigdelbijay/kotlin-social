package com.example.kotlin_social.ui

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.kotlin_social.R

fun View.slideUp(context: Context, animTime: Long, startOffset: Long) {
    var slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = animTime
        interpolator= FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}

fun slideUpViews(context: Context, vararg views: View, animTime:Long = 300L, startOffset: Long = 150L) {
    for(i in views.indices) {
        views[i].slideUp(context, animTime, startOffset*i)
    }
}