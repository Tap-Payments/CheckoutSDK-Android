package company.tap.checkout.internal

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

class TouchyWebView : WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(computeVerticalScrollRange() > measuredHeight)
            requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event)
    }
}