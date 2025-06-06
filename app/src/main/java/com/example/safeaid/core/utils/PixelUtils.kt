package com.example.safeaid.core.utils

import android.content.Context
import android.util.DisplayMetrics

class PixelUtils {
    companion object {
        fun dpToPx(context: Context, dp: Int): Int {
            return Math.round(
                dp * getPixelScaleFactor(
                    context
                )
            )
        }

        fun pxToDp(context: Context, px: Int): Int {
            return Math.round(
                px / getPixelScaleFactor(
                    context
                )
            )
        }

        private fun getPixelScaleFactor(context: Context): Float {
            val displayMetrics = context.resources.displayMetrics
            return displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT
        }
    }
}