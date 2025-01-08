package org.wentura.locoway.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

fun Context.findActivity(): Activity {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }

        context = context.baseContext
    }

    throw IllegalStateException("No activity")
}

fun setBrightness(context: Context, isFull: Boolean) {
    val activity = context.findActivity()
    val layoutParams: WindowManager.LayoutParams = activity.window.attributes

    layoutParams.screenBrightness =
        if (isFull) 1.0f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

    activity.window.attributes = layoutParams
}

fun travelTime(departureDateTime: LocalDateTime, arrivalDateTime: LocalDateTime): Int {
    val minutes =
        ceil(ChronoUnit.SECONDS.between(departureDateTime, arrivalDateTime) / 60.0).toInt()

    return minutes
}
