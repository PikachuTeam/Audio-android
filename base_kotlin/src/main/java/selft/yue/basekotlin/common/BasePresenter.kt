package selft.yue.basekotlin.common

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BasePresenter<V : BaseContract.View>(var view: V?) : BaseContract.Presenter<V> {
    override fun dispose() {
        this.view = null
    }
}