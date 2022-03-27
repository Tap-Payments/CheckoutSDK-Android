package company.tap.checkout.internal.apiresponse

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object ApiService {


    // const val INIT = "https://run.mocky.io/v3/e4718d85-554b-4d15-a883-3043d961c8e5"
    //const val INIT = "e4718d85-554b-4d15-a883-3043d961c8e5"
   // const val BASE_URL = "https://api.tap.company/v2/"
    const val BASE_URL = "https://checkout-mw.dev.tap.company/api/"
    const val INIT = "checkout/init"
    const val PAYMENT_TYPES = "checkout/payment/types"
    const val CHARGES ="charges"
    const val RETURN_URL ="gosellsdk://return_url"
    const val TAP_ID ="tap_id"
    const val BIN ="card/bin"
    const val CONFIG ="checkout/config"

    /**
     * The Authorize.
     */
    const val AUTHORIZE = "checkout/authorize"

    /**
     * The Tokens.
     */
    const val TOKENS = "checkout/tokens"

    /**
     * The Token.
     */
    const val TOKEN = "/api/token"
    /**
     * The Save card.
     */
    const val SAVE_CARD = "card/verify"

    /**
     * The Authenticate.
     */
    const val AUTHENTICATE = "authenticate"

    /**
     *
     */
    const val LIST_CARD = "card"

    /**
     * The Charge id.
     */
  //  const val CHARGE_ID = "charge_id/"
    /**
     * Delete Card API
     */
    const val DELETE_CARD = "card"
    const val CHARGE_ID = "charges/"
    const val AUTHORIZE_ID = "authorize/"
    const val SAVE_CARD_ID = "card/verify/"
    const val INIT_AR = "https://run.mocky.io/v3/491462af-994e-4d99-9218-041289a1ae5d"
}