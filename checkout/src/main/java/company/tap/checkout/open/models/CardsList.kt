package company.tap.checkout.open.models

import company.tap.checkout.internal.api.models.SavedCard
import java.util.*

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class CardsList(
    responseCode: Int,
    `object`: String,
    has_more: Boolean,
    data: ArrayList<SavedCard>?
) {
    private val responseCode: Int = responseCode
    private val `object`: String
    private val has_more: Boolean
    private var cards: ArrayList<SavedCard>?

    /**
     * Gets Response Code
     * @return responseCode
     */
    fun getResponseCode(): Int {
        return responseCode
    }

    /**
     * Gets Object type
     * @return object
     */
    fun getObject(): String {
        return `object`
    }

    /**
     * Check if customer has more cards
     * @return has_more
     */
    fun isHas_more(): Boolean {
        return has_more
    }

    /**
     * Gets cards.
     *
     * @return the cards
     */
    fun getCards(): ArrayList<SavedCard>? {
        if (cards == null) {
            cards = ArrayList<SavedCard>()
        }
        return cards
    }

    init {
        this.`object` = `object`
        this.has_more = has_more
        cards = data
    }
}