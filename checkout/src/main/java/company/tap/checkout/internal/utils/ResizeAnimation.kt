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
        println("targetHeight>>"+targetHeight)
        println("startHeight>>"+startHeight)
        println("expandHeight>>"+expandHeight)
        newtargetHeight = startHeight
        newstartHeight = targetHeight
        if(expandHeight){
            val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime)

            view?.layoutParams?.height = newHeight.toInt()

            view?.requestLayout()
        }else {
            println("newtargetHeight>>"+newtargetHeight)
            println("newstartHeight>>"+newstartHeight)
          //  val newHeight2 = (startHeight + targetHeight * interpolatedTime).toInt()
            val newHeight2 = (newtargetHeight + (newstartHeight - newtargetHeight) * interpolatedTime)
           // val newHeight2 = (newtargetHeight - newstartHeight * interpolatedTime).toInt()
            view?.layoutParams?.height = newHeight2.toInt()
            view?.requestLayout()
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        println("initialize width>>"+width)
        println("initialize height>>"+height)
        println("initialize parentWidth>>"+parentWidth)
        println("initialize parentHeight>>"+parentHeight)

    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}