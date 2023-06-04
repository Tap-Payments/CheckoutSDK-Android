package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import company.tap.tapuilibraryy.R


class TapCurrencyControlWidget : FrameLayout {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        inflate(getContext(), R.layout.tap_currency_control_widget, this)
    }
}