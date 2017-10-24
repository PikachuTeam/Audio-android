package selft.yue.basekotlin.decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by dongc on 8/28/2017.
 */
class LinearItemDecoration(private val left: Int, private val top: Int, private val right: Int, private val bottom: Int) : RecyclerView.ItemDecoration() {

    constructor(verticalSpace: Int, horizontalSpace: Int) : this(horizontalSpace, verticalSpace, horizontalSpace, verticalSpace)

    constructor(space: Int) : this(space, space)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.also {
            it.left = left
            it.right = right
            it.bottom = bottom
            it.top = 0
            if (parent.getChildAdapterPosition(view) == 0)
                it.top = top
        }
    }
}