package company.tap.cardinputwidget.views

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import company.tap.cardinputwidget.R
import company.tap.tapuilibrary.uikit.atoms.TapTextInput
import company.tap.tapuilibrary.uikit.utils.TapTextWatcher

class HolderNameEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TapTextInput(context, attrs, defStyleAttr) {


    internal val holderName: String?
        get() {
            return fieldText
        }

    init {
        setErrorMessage(resources.getString(R.string.invalid_holder_name))
        maxLines = 1
        inputType=InputType.TYPE_CLASS_TEXT
        addTextChangedListener(object : TapTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                shouldShowError = false
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_POSTAL_CODE)
        }
     /*   filters = arrayOf(
            InputFilter { cs, start, end, spanned, dStart, dEnd -> // TODO Auto-generated method stub
                if (cs == "") { // for backspace
                    return@InputFilter cs
                }
                val pattern = Regex("[ABCDEFGHIJKLMNOPQRSTUVWXYZ .]")
                (if (cs.toString().matches(pattern)){

                } else "").toString()
            }
        )*/

        val letterFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                var filtered: String? = ""
                filtered =source.replace(Regex("[^a-zA-Z ]*"), "")
               return@InputFilter filtered.toString().toUpperCase()
            }
        filters = arrayOf(letterFilter)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        configureField()
    }

    /**
     * Configure the field
     */
    private fun configureField() {
        hint = resources.getString(R.string.holder_name_hint)
       val filtersL = InputFilter.LengthFilter(MAX_LENGTH)


       // keyListener = TextKeyListener.getInstance()
        inputType = InputType.TYPE_CLASS_TEXT
       /* val letterFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                var filtered: String? = ""
                for (i in start until end) {
                    val character = source[i]
                    if ( Character.isLetter(character)) {
                        filtered += character
                    }
                }
                filtered.toString().toUpperCase()
            }
        filters = arrayOf(letterFilter)*/
        addTextChangedListener(object : TapTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                shouldShowError = false
            }
        })
        val letterFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                var filtered: String? = ""
                filtered =source.replace(Regex("[^a-zA-Z ]*"), "")
                return@InputFilter filtered.toString().toUpperCase()
            }
        filters = arrayOf(letterFilter,filtersL)
    }



    /**
     * If a `TextInputLayout` is an ancestor of this view, set the hint on it. Otherwise, set
     * the hint on this view.
     */
    private fun updateHint(@StringRes hintRes: Int) {
        getTextInputLayout()?.let {
            if (it.isHintEnabled) {
                it.hint = resources.getString(hintRes)
            } else {
                setHint(hintRes)
            }
        }
    }

    /**
     * Copied from `TextInputEditText`
     */
    private fun getTextInputLayout(): TextInputLayout? {
        var parent = parent
        while (parent is View) {
            if (parent is TextInputLayout) {
                return parent
            }
            parent = parent.getParent()
        }
        return null
    }


    private companion object {
        private const val MAX_LENGTH = 22
    }
}
