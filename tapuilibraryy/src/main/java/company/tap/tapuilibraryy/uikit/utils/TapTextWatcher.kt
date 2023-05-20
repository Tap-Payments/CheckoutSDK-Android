package company.tap.tapuilibraryy.uikit.utils

import android.text.Editable
import android.text.TextWatcher

abstract class TapTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }
}
