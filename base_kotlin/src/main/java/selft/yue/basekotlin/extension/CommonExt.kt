package selft.yue.basekotlin.extension

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.IntegerRes
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.widget.EditText

/**
 * Created by dongc on 8/27/2017.
 *
 * This extension includes common functions for all projects
 */

fun EditText.asString(): String = text.toString()

fun Context.isNetworkAvailable(): Boolean = with(getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
    activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

fun Context.getRealColor(@IntegerRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)

fun Context.convertDpToPixel(dimension: Float): Int {
    val displayMetrics = resources.displayMetrics
    return (dimension * displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toInt()
}