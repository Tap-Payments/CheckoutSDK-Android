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
  // const val BASE_URL = "https://checkout-mw.dev.tap.company/api/"
    const val BASE_URL = "https://mw-sdk.dev.tap.company/v2/checkout/"
    const val INIT = "checkoutprofile"
    const val PAYMENT_TYPES = "checkout/payment/types"
    const val CHARGES ="charge"
    const val RETURN_URL ="gosellsdk://return_url"
    const val TAP_ID ="tap_id"
    const val BIN ="card/bin/"
    const val BILLING_ADDRESS ="billing_address/"
    //const val CONFIG ="checkout/config"
    const val CONFIG ="checkoutprofile"

    /**
     * The Authorize.
     */
    const val AUTHORIZE = "authorize"

    /**
     * The Tokens.
     */
    const val TOKENS = "checkout/tokens"

    /**
     * The Token.
     */
    const val TOKEN = "token/"
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
    const val CHARGE_ID = "charge/"
    const val AUTHORIZE_ID = "authorize/"
    const val SAVE_CARD_ID = "card/verify/"
    const val INIT_AR = "https://run.mocky.io/v3/491462af-994e-4d99-9218-041289a1ae5d"
    const val CURRENCY_API = "currency"
    const val CHECKOUT_PROFILE_API = "checkoutprofileapi"
    const val BASE_URL_IP = "https://geolocation-db.com"
    const val GET_IP_VAL = "/json/"
}