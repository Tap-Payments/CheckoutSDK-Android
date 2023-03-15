package company.tap.checkoutsdk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import company.tap.checkoutsdk.viewmodels.CustomerViewModel
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.utils.Validator
import company.tap.checkoutsdk.viewmodels.ShippingViewModel
import java.util.*

class ShippingCreateActivity : AppCompatActivity() {

    private var shippingName: AppCompatEditText? = null
    private var shippingDescription: AppCompatEditText? = null
    private var shippingAmount: AppCompatEditText? = null

    private var name_shipping_il: TextInputLayout? = null
    private var shipping_description_il: TextInputLayout? = null
    private var shipping_amount_il: TextInputLayout? = null

    private var shippingViewModel: ShippingViewModel? = null
    private var operation = ""
    @JvmField
    var OPERATION_ADD = "add"
    @JvmField
    var OPERATION_EDIT = "edit"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)
        bindViews()

        operation = intent.getStringExtra("operation")!!

        if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            shippingViewModel = intent.getSerializableExtra("shipping") as ShippingViewModel?
           // println("shippingViewModel : " + shippingViewModel?.getshippingName())
            populateShippingFields(shippingViewModel)
        }

    }

    private fun populateShippingFields(shippingViewModel: ShippingViewModel?) {
        shippingName?.setText(shippingViewModel?.getshippingName())
        shippingDescription?.setText(shippingViewModel?.getshippingDecsription())
        shippingAmount?.setText(shippingViewModel?.getshippingAmount())
    }

    fun save(view: View?) {
        if (Validator.isValidName(Objects.requireNonNull(shippingName!!.text).toString().trim { it <= ' ' })
        ) {
            if (operation.equals(
                    OPERATION_ADD,
                    ignoreCase = true
                )
            ) {
                println("inside: $operation")
                SettingsManager.saveShipping(
                    shippingName?.text.toString().trim { it <= ' ' },
                    shippingDescription?.text.toString().trim { it <= ' ' },
                    shippingAmount?.text.toString().trim { it <= ' ' },
                    this
                )
                back(null)
            } else if (operation.equals(
                    OPERATION_EDIT,
                    ignoreCase = true
                )
            ) {
                SettingsManager.editShipping(
                    shippingViewModel,
                    ShippingViewModel(
                        shippingName?.text.toString().trim { it <= ' ' },
                        shippingDescription?.text.toString().trim { it <= ' ' },
                        shippingAmount?.text.toString().trim { it <= ' ' }), this
                )
                back(null)
            }
        }
        /*} else {
           *//* if (!NAME_IS_VALID) name_l!!.error =
                "getString(R.string.name_invalid_msg)" else name_l?.error = null
            if (!EMAIL_IS_VALID) email_l!!.error =
                "getString(R.string.email_invalid_msg)" else email_l?.error = null
            if (!MOBILE_IS_VALID) mobile_l!!.error =
                "getString(R.string.mobile_invalid_msg)" else mobile_l?.error = null*//*
        }*/
    }

    private fun bindViews() {
        shippingName = findViewById(R.id.edit_shipping_name)
        shippingDescription = findViewById(R.id.edit_shipping_description)
        shippingAmount = findViewById(R.id.edit_shipping_amount)

        name_shipping_il = findViewById(R.id.name_shipping_il)
        shipping_description_il = findViewById(R.id.shipping_description_il)
        shipping_amount_il = findViewById(R.id.shipping_amount_il)
    }

    fun back(view: View?) {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ShippingsActivity::class.java))
        finish()
    }
}