package com.example.mypractice.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

object BitmapUtil {
    var TAG = "BitmapUtil"
    fun getBitmap(
        context: Context,
        reuseBitmap: Bitmap,
        resId: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, options)
        if (canUseForInBitmap(reuseBitmap, options)) {
            Log.e(TAG, "reuseBitmap is reusable")
            options.inMutable = true
            options.inBitmap = reuseBitmap
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(context.resources, resId, options)
    }

    fun canUseForInBitmap(candidate: Bitmap, targetOptions: BitmapFactory.Options): Boolean {
        val width = targetOptions.outWidth / Math.max(targetOptions.inSampleSize, 1)
        val height =
            targetOptions.outHeight / Math.max(targetOptions.inSampleSize, 1)
        val byteCount =
            width * height * getBytesPerPixel(candidate.config)
        return byteCount <= candidate.allocationByteCount
    }

        private fun getBytesPerPixel(config: Bitmap.Config): Int {
            val bytesPerPixel: Int
            bytesPerPixel = when (config) {
                Bitmap.Config.ALPHA_8 -> 1
                Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
                else -> 4
            }
            return bytesPerPixel
        }
}