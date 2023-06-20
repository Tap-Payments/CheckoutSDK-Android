package company.tap.checkout.internal.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
    }
}
fun RecyclerView.getCenterSmoothController(): RecyclerView.SmoothScroller {
    val smoothScroller: RecyclerView.SmoothScroller = CenterSmoothScroller(this.context)
    return smoothScroller
}
