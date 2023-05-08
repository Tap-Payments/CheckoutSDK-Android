package company.tap.tapuilibrary.uikit.atoms

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.progressindicator.ProgressIndicator
import company.tap.tapuilibrary.uikit.interfaces.TapProgressIndicatorInterface

/**
 *
 * Created by Mario Gamal on 6/30/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapProgressIndicator(context: Context?, attrs: AttributeSet?) :
    ProgressIndicator(context, attrs) {

    private var progressIndicatorInterface: TapProgressIndicatorInterface? = null

    fun setTapProgressIndicatorInterface(progressIndicatorInterface: TapProgressIndicatorInterface) {
        this.progressIndicatorInterface = progressIndicatorInterface
    }

    override fun setProgressCompat(progress: Int, animated: Boolean) {
        super.setProgressCompat(progress, animated)
        if (progress == max) {
            if (!isIndeterminate){
                progressIndicatorInterface?.onProgressEnd()
            }
        }
    }
}