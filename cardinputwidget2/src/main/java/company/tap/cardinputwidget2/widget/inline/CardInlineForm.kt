package company.tap.cardinputwidget2.widget.inline

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import company.tap.cardinputwidget2.databinding.CardInlineFormBinding
import company.tap.cardinputwidget2.widget.BaseCardInput
import kotlinx.android.synthetic.main.card_input_widget.view.*
import kotlin.properties.Delegates

class CardInlineForm @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr)
{

    private val viewBinding = CardInlineFormBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    @JvmSynthetic
    internal val holderNameEditText1 = viewBinding.holderNameEditText1
    private val containerLayout = viewBinding.inlineCardInput.container
    internal val holderNameTextInputLayout1 = viewBinding.holderNameTextInputLayout1
    private val frameStart: Int
        get() {
            val isLtr = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
            return if (isLtr) {
                containerLayout.left
            } else {
                containerLayout.right
            }
        }
    private val holderNameValue: String?
        get() {
            return if (holderNameEnabled) {
                holderNameEditText1.holderName
            } else {
                null
            }
        }
    /**
     * The postal code field is enabled by default. Disabling the postal code field may impact
     * auth success rates, so it is discouraged to disable it unless you are collecting the postal
     * code outside of this form.
     */
    var holderNameEnabled: Boolean by Delegates.observable(
        BaseCardInput.DEFAULT_HOLDER_NAME_ENABLED
    ) { _, _, isEnabled ->
        if (isEnabled) {
            holderNameEditText1.isEnabled = true
            holderNameTextInputLayout1.visibility = View.VISIBLE

            //cvcNumberEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        } else {
            holderNameEditText1.isEnabled = false
            holderNameTextInputLayout1.visibility = View.GONE

           // cvcNumberEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }

}