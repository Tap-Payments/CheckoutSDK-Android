package company.tap.tapuilibraryy.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.*
import androidx.cardview.widget.CardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import kotlinx.android.synthetic.main.tap_loyality_view.view.*

class CustomEditText : FrameLayout {


    val editTextAmount by lazy { findViewById<EditText>(R.id.ed_amount_) }


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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        inflate(context, R.layout.tap_custom_edit_text, this)
    }


}
