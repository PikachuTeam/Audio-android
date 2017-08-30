package selft.yue.basekotlin.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import selft.yue.basekotlin.common.BaseContract
import selft.yue.basekotlin.extension.shortToast

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BaseActivity : AppCompatActivity(), BaseContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            if (grantResults.indices.firstOrNull { grantResults[it] == PackageManager.PERMISSION_DENIED } != null)
                onPermissionDenied()
            else
                onPermissionGranted(requestCode)
        } else {
            onPermissionDenied()
        }
    }

    override fun showLoadingDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissLoadingDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showToast(message: String) {
        shortToast(message)
    }

    override fun showToast(messageId: Int) {
        shortToast(messageId)
    }

    fun checkAndRequestPermissions(requestCode: Int, permissions: Array<String>): Boolean =
            permissions.filter { !checkPermission(it) }.let {
                if (it.isEmpty()) {
                    true
                } else {
                    ActivityCompat.requestPermissions(this, it.toTypedArray(), requestCode)
                    false
                }
            }

    fun ifNotNull(vararg values: Any?, allNotNull: ((Array<Any>) -> Unit)? = null): Boolean {
        values.forEach {
            if (it == null) {
                return false
            }
        }
        allNotNull?.invoke(values as Array<Any>)
        return true
    }

    open fun onPermissionGranted(requestCode: Int) {
    }

    open fun onPermissionDenied() {
    }

    private fun checkPermission(permission: String): Boolean =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}