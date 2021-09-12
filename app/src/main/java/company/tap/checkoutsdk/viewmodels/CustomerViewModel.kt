package company.tap.checkoutsdk.viewmodels

import java.io.Serializable

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class CustomerViewModel(
    ref: String?,
    name: String, middle: String,
    last: String, email: String,
    sdn: String, mobile: String
) : Serializable,
    Comparable<CustomerViewModel> {
    private var ref: String? = null
    private var name: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var mobile: String? = null
    private var sdn: String? = null
    fun getName(): String {
        return name!!
    }

    fun getMiddleName(): String? {
        return middleName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun getEmail(): String {
        return email!!
    }

    fun getSdn(): String? {
        return sdn
    }

    fun getRef(): String? {
        return ref
    }

    fun getMobile(): String {
        return mobile!!
    }

    private fun setSimpleText(
        ref: String?, name: String, middle: String,
        last: String, email: String,
        sdn: String, mobile: String
    ) {
        this.ref = ref
        this.name = name
        middleName = middle
        lastName = last
        this.email = email
        this.sdn = sdn
        this.mobile = mobile
    }



    init {
        setSimpleText(ref, name, middle, last, email, sdn, mobile)
    }

    override fun compareTo(other: CustomerViewModel): Int {
        val thisMobile = getMobile()
        val otherMobile = other.getMobile()
        return otherMobile.let { thisMobile.compareTo(it, ignoreCase = true) }
    }
}