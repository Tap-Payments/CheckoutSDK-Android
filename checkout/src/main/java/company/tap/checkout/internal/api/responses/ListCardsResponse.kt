package company.tap.checkout.internal.api.responses

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.interfaces.BaseResponse
import company.tap.checkout.open.data_managers.PaymentDataSource
import java.io.Serializable
import java.util.*

/**
 * Created by AhlaamK on 8/4/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
 class ListCardsResponse:BaseResponse {
    @SerializedName("object")
    @Expose
    private val `object`: String? = null


    @SerializedName("has_more")
    @Expose
    private val has_more: Boolean = false

    @SerializedName("data")
    @Expose
    private var data: ArrayList<SavedCard>? = null


    fun getObject(): String? {
        return `object`
    }

    fun isHas_more(): Boolean {
        return has_more
    }

    /**
     * Gets cards.
     *
     * @return the cards
     */
    fun getCards(): ArrayList<SavedCard> {
        if (data == null) {
            data = ArrayList()
        }
        for (i in data!!.indices) {
            if (data != null && PaymentDataSource.getCardType() != null && !PaymentDataSource.getCardType()
                    .toString()
                    .equals(
                        data!![i].funding
                    )
            ) {
                data!!.removeAt(i)
            }
        }
        return data as ArrayList<SavedCard>
    }
}



