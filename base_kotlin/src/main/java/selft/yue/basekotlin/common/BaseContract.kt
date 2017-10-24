package selft.yue.basekotlin.common

import android.support.annotation.IdRes

/**
 * Created by dongc on 8/26/2017.
 */
interface BaseContract {
    interface View {
        fun getLayoutResId(): Int

        fun showLoadingDialog()

        fun dismissLoadingDialog()

        fun showToast(message: String)

        fun showToast(@IdRes messageId: Int)

    }

    interface Presenter<V : BaseContract.View> {
        fun dispose()
    }
}