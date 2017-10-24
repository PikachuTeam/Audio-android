package selft.yue.basekotlin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.FrameLayout

/**
 * Created by dongc on 8/29/2017.
 */
class FractionFrameLayout : FrameLayout {
    private var mOnPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    var xFraction: Float = 0.toFloat()
        get() = field
        set(value) {
            field = value
            if (width == 0) {
                if (mOnPreDrawListener == null) {
                    mOnPreDrawListener = ViewTreeObserver.OnPreDrawListener {
                        viewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener)
                        xFraction = value
                        true
                    }
                }
                return
            }

            translationX = width * field
        }

    var yFraction: Float = 0.toFloat()
        get() = field
        set(value) {
            field = value
            if (height == 0) {
                if (mOnPreDrawListener == null) {
                    mOnPreDrawListener = ViewTreeObserver.OnPreDrawListener {
                        viewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener)
                        yFraction = value
                        true
                    }
                }
                return
            }

            translationY = height * field
        }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
    }

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
    }
}