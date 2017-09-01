package selft.yue.basekotlin.extension

import android.support.annotation.IdRes
import android.support.v4.content.ContextCompat
import android.widget.Toast
import selft.yue.basekotlin.activity.BaseActivity

/**
 * Created by dongc on 8/28/2017.
 */

fun BaseActivity.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun BaseActivity.shortToast(@IdRes messageResId: Int) {
    Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
}

fun BaseActivity.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun BaseActivity.longToast(@IdRes messageResId: Int) {
    Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
}

fun BaseActivity.getRealColor(@IdRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)