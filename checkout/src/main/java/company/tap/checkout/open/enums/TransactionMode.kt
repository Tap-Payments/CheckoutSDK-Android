package company.tap.checkout.open.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * There are different Transaction modes like Purchase,Authorize capture,Save card, Tokenize card which can be set by the merchant
 * **/
enum class TransactionMode {
    PURCHASE,
    AUTHORIZE_CAPTURE,
    SAVE_CARD,
    TOKENIZE_CARD

}