package company.tap.checkoutsdk.viewmodels

import java.io.Serializable

/**
 * Created by AhlaamK on 08/02/22

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class PaymentItemViewModel(
    itemsName: String?,
    itemDescrp: String, itemPriceUnit: Double,
    itemTotalPrice: Double, itemQuantity: Int,

    ) : Serializable {
    private var itemsName: String? = null
    private var itemDescrp: String? = null
    private var itemPriceUnit: Double? = null
    private var itemTotalPrice: Double? = null
    private var itemQuantity: Int? = null

    fun getItemsName(): String? {
        return itemsName
    }

    fun getItemDescription(): String? {
        return itemDescrp
    }

    fun getPricePUnit(): Double? {
        return itemPriceUnit
    }

    fun getitemTotalPrice(): Double? {
        return itemTotalPrice
    }

    fun getitemQuantity(): Int? {
        return itemQuantity
    }


    private fun setSimpleText(
        itemsName: String?, itemDescrp: String, itemQuantity: Int,
        itemPriceUnit: Double, itemTotalPrice: Double
    ) {
        this.itemsName = itemsName
        this.itemDescrp = itemDescrp
        this.itemQuantity = itemQuantity
        this.itemPriceUnit = itemPriceUnit
        this.itemTotalPrice = itemTotalPrice

    }


    init {
        setSimpleText(itemsName, itemDescrp, itemQuantity, itemPriceUnit, itemTotalPrice)
    }

}