package company.tap.checkoutsdk.viewmodels

import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.Category
import company.tap.checkout.open.models.ItemDimensions
import company.tap.checkout.open.models.MetaData
import company.tap.checkout.open.models.ReferenceItem
import company.tap.checkout.open.models.Vendor
import java.io.Serializable

/**
 * Created by AhlaamK on 08/02/22

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class PaymentItemViewModel(
    productId:String?,
    itemsName: String?,
    itemDescrp: String, itemPriceUnit: Double,
    itemTotalPrice: Double, itemQuantity: Int, itemsdiscount: AmountModificator,itemsCurrency :String?,
    itemsCategory: Category? ,itemsVendor: Vendor?,fulfillmentService:String?,isRequireShipping:Boolean?=false,
    itemCode :String?,accountCode :String?, itemImage :String?, itemReference : ReferenceItem?,
    itemsDimensions : ItemDimensions?,itemsTags :String?,itemMetaData: MetaData?


    ) : Serializable {
    private var productId: String? = null
    private var itemsName: String? = null
    private var itemDescrp: String? = null
    private var itemPriceUnit: Double? = null
    private var itemTotalPrice: Double? = null
    private var itemQuantity: Int? = null
    private var itemsdiscount: AmountModificator? = null

    private var itemsCurrency: String? = null
    private var itemsCategory: Category? = null
    private var itemsVendor: Vendor? = null
    private var fulfillmentService: String? = null
    private var isRequireShipping:Boolean?=false
    private var itemCode:String?=null
    private var accountCode:String?=null
    private var itemImage:String?=null
    private var itemReference:ReferenceItem?=null
    private var  itemsDimensions : ItemDimensions?=null
    private var itemsTags :String?=null
    private var itemMetaData: MetaData?=null


    fun getProductId(): String? {
        return productId
    }
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
    fun getAmountType(): AmountModificator? {
        return itemsdiscount
    }
    fun getItemCurrency(): String? {
        return itemsCurrency
    }

    fun getItemCategory(): Category? {
        return itemsCategory
    }

    fun getItemVendor(): Vendor? {
        return itemsVendor
    }

    fun getItemFullfillmentService(): String? {
        return fulfillmentService
    }


    fun getItemIsRequireShip(): Boolean? {
        return isRequireShipping
    }


    fun getItemCode(): String? {
        return itemCode
    }

    fun getAccountCode(): String? {
        return accountCode
    }
    fun getItemImage(): String? {
        return itemImage
    }
    fun getItemReference(): ReferenceItem? {
        return itemReference
    }

    fun getItemDimens(): ItemDimensions? {
        return itemsDimensions
    }

    fun getItemTags(): String? {
        return itemsTags
    }
    fun getItemMetaData(): MetaData? {
        return itemMetaData
    }

    private fun setSimpleText(
        productId:String?,
        itemsName: String?, itemDescrp: String, itemQuantity: Int,
        itemPriceUnit: Double, itemTotalPrice: Double , itemsdiscount: AmountModificator?,itemsCurrency :String?,
        itemsCategory: Category? ,itemsVendor: Vendor?,fulfillmentService:String?,isRequireShipping:Boolean?=false,
        itemCode :String?,accountCode :String?, itemImage :String?, itemReference : ReferenceItem?,
        itemsDimensions : ItemDimensions?,itemsTags :String?,itemMetaData: MetaData?
    ) {
        this.productId = productId
        this.itemsName = itemsName
        this.itemDescrp = itemDescrp
        this.itemQuantity = itemQuantity
        this.itemPriceUnit = itemPriceUnit
        this.itemTotalPrice = itemTotalPrice
        this.itemsdiscount = itemsdiscount
        this.itemsCurrency = itemsCurrency
        this.itemsCategory = itemsCategory
        this.itemsVendor= itemsVendor
        this.fulfillmentService= fulfillmentService
        this.isRequireShipping = isRequireShipping
        this.itemCode= itemCode
        this.accountCode= accountCode
        this.itemImage= itemImage
        this.itemReference=itemReference
        this. itemsDimensions=itemsDimensions
        this.itemsTags= itemsTags
        this.itemMetaData=itemMetaData
    }



    init {
        setSimpleText(productId,itemsName, itemDescrp, itemQuantity, itemPriceUnit, itemTotalPrice,itemsdiscount,itemsCurrency, itemsCategory,itemsVendor,fulfillmentService,isRequireShipping,
        itemCode ,accountCode , itemImage , itemReference,
        itemsDimensions,itemsTags ,itemMetaData
        )
    }

}