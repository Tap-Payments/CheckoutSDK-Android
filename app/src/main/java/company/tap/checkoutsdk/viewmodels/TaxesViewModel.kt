package company.tap.checkoutsdk.viewmodels
import java.io.Serializable

/**
 * Created by AhlaamK on 4/11/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class TaxesViewModel (
    taxesName: String,
    taxesDescription: String, taxesAmount: String
    ) : Serializable {

        private var taxesName: String? = null
        private var taxesDecsription: String? = null
        private var taxesAmount: String? = null


        fun getTaxesName(): String {
            return taxesName!!
        }

        fun getTaxesDecsription(): String? {
            return taxesDecsription
        }

        fun getTaxesAmount(): String? {
            return taxesAmount
        }


        private fun setSimpleText(
            _taxesName: String, _taxesDescription: String,
            _taxesAmount: String
        ) {

            this.taxesName = _taxesName
            this.taxesDecsription = _taxesDescription
            this.taxesAmount = _taxesAmount

        }



        init {
            setSimpleText(
                taxesName,taxesDescription,taxesAmount)
        }


    }