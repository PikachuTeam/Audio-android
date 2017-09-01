package selft.yue.basekotlin.util

/**
 * Created by dongc on 9/2/2017.
 */
interface HasNormalRecyclerView<E> {
    /**
     * Refresh data of recycler view
     */
    fun refreshData(data: MutableList<E>)
}