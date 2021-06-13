package company.tap.checkout.internal.api.enums

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class AmountModificatorType (val amountModificatorType:String){
    /**
     * Percentage amount modificator type.
     */
    PERCENTAGE("P"),
    /**
     * Fixed amount modificator type.
     */
    FIXED("F")
}