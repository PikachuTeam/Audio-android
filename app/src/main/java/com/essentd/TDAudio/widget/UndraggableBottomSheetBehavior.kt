package com.essentd.TDAudio.widget

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by dongc on 9/1/2017.
 */
class UndraggableBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: V, event: MotionEvent?): Boolean {
        return false
    }
}