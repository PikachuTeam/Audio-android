package selft.yue.basekotlin.extension

import android.content.Context
import android.net.ConnectivityManager
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