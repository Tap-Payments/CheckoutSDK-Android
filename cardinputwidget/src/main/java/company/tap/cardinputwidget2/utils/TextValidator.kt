package company.tap.cardinputwidget.utils

import android.text.Editable
import android.text.TextWatcher
import company.tap.cardinputwidget.views.CardNumberEditText


/**
 * Created by AhlaamK on 8/5/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
abstract class TextValidator( cardNumberEditText: CardNumberEditText) : TextWatcher {
    private val cardNumberEditText: CardNumberEditText = cardNumberEditText
    abstract fun validate(cardNumberEditText: CardNumberEditText?, text: String?)
    override fun afterTextChanged(s: Editable?) {
        val text = cardNumberEditText.text.toString()
        validate(cardNumberEditText, text)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* Don't care */
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* Don't care */
    }

}