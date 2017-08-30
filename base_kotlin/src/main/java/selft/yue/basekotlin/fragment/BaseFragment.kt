package selft.yue.basekotlin.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import selft.yue.basekotlin.R
import selft.yue.basekotlin.activity.BaseFragmentActivity
import selft.yue.basekotlin.common.BaseContract
import selft.yue.basekotlin.widget.FractionFrameLayout

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BaseFragment : Fragment(), BaseContract.View {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(getLayoutResId(), container, false)
        if (view !is FractionFrameLayout) {
            val parent = FractionFrameLayout(activity)
            parent.addView(view)
            return parent
        }
        return view
    }

    override fun showLoadingDialog() {
        getFragmentActivity()?.showLoadingDialog()
    }

    override fun dismissLoadingDialog() {
        getFragmentActivity()?.dismissLoadingDialog()
    }

    override fun showToast(messageId: Int) {
        getFragmentActivity()?.showToast(messageId)
    }

    override fun showToast(message: String) {
        getFragmentActivity()?.showToast(message)
    }

    fun getFragmentActivity(): BaseFragmentActivity? =
            if (activity is BaseFragmentActivity) activity as BaseFragmentActivity else null

    fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            getFragmentActivity()?.finish()
        }
    }

    fun replaceFragment1(fragment: BaseFragment,
                         @IdRes frameId: Int = R.id.fragment_container, tag: String? = null,
                         @IdRes enter: Int = R.animator.slide_up, @IdRes exit: Int = R.animator.slide_down) {
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, exit)
            replace(frameId, fragment)
            if (!tag.isNullOrBlank())
                addToBackStack(tag)
        }.commit()
    }

    fun replaceFragment2(fragment: BaseFragment,
                         @IdRes frameId: Int = R.id.fragment_container, tag: String? = null,
                         @IdRes enter: Int = R.animator.slide_in_left, @IdRes exit: Int = R.animator.slide_out_right,
                         @IdRes popEnter: Int = R.animator.slide_in_right, @IdRes popExit: Int = R.animator.slide_out_left) {
        fragmentManager.beginTransaction().apply {
            setCustomAnimations(enter, exit, popEnter, popExit)
            replace(frameId, fragment)
            if (!tag.isNullOrBlank())
                addToBackStack(tag)
        }.commit()
    }

    fun checkAndRequestPermissions(requestCode: Int, permissions: Array<String>): Boolean {
        return getFragmentActivity()?.checkAndRequestPermissions(requestCode, permissions) ?: false
    }

    open fun onPermissionGranted(requestCode: Int) {
    }

    open fun onPermissionDenied() {
    }
}