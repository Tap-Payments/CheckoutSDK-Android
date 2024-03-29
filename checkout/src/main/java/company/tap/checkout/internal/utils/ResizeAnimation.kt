package company.tap.checkout.internal.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(view: View?, targetHeight: Int, startHeight: Int, expandHeightBool :Boolean) :Animation() {
    var targetHeight = 0
    var newtargetHeight = 0
    var newstartHeight = 0
    var view: View? = null
    var startHeight = 0
    var expandHeight:Boolean = false


      init {
          this.view = view
          this.targetHeight = targetHeight
          this.startHeight = startHeight
          this.expandHeight = expandHeightBool
      }


    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

        newtargetHeight = startHeight
        newstartHeight = targetHeight
        if(expandHeight){
            val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime)

            view?.layoutParams?.height = newHeight.toInt()

            view?.requestLayout()
        }else {
            val newHeight2 = (newtargetHeight + (newstartHeight - newtargetHeight) * interpolatedTime)
            view?.layoutParams?.height = newHeight2.toInt()
            view?.requestLayout()
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}