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
import java.util.*

open class CustomerCreateActivity : AppCompatActivity() {
    private var name: AppCompatEditText? = null
    private var middleName: AppCompatEditText? = null
    private var lastName: AppCompatEditText? = null
    private var email: AppCompatEditText? = null
    private var sdn: AppCompatEditText? = null
    private var mobile: AppCompatEditText? = null


    private var name_l: TextInputLayout? = null
    private var middlename_l: TextInputLayout? = null
    private var lastname_l: TextInputLayout? = null
    private var email_l: TextInputLayout? = null
    private var mobile_l: TextInputLayout? = null
    private var sdn_l: TextInputLayout? = null

    private var operation = ""
    private var customer: CustomerViewModel? = null

    @JvmField
    var OPERATION_ADD = "add"
    @JvmField
    var OPERATION_EDIT = "edit"
    @JvmField
    var NAME_IS_VALID = false
    @JvmField
    var EMAIL_IS_VALID = false
    @JvmField
    var MOBILE_IS_VALID = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_create)
        bindViews()

        operation = intent.getStringExtra("operation")!!

        if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            customer = intent.getSerializableExtra("customer") as CustomerViewModel?
            println(" customer : " + customer?.getEmail())
            populateCustomerFields(customer)
        }

    }

    private fun bindViews() {
        name = findViewById(R.id.customer_name)
        middleName = findViewById(R.id.middlename)
        lastName = findViewById(R.id.lastname)
        email = findViewById(R.id.customer_email)
        sdn = findViewById(R.id.sdn)
        mobile = findViewById(R.id.customer_mobile)

        name_l = findViewById(R.id.name_il)
        middlename_l = findViewById(R.id.middlename_il)
        lastname_l = findViewById(R.id.lastname_il)
        email_l = findViewById(R.id.email_il)
        mobile_l = findViewById(R.id.mobile_il)
        sdn_l = findViewById(R.id.sdn_il)
    }

    private fun populateCustomerFields(customer: CustomerViewModel?) {
        name?.setText(customer?.getName())
        middleName?.setText(customer?.getMiddleName())
        lastName?.setText(customer?.getLastName())
        email?.setText(customer?.getEmail())
        sdn?.setText(customer?.getSdn())
        mobile?.setText(customer?.getMobile())
    }


    fun save(view: View?) {
        if (Validator.isValidName(Objects.requireNonNull(name!!.text).toString().trim { it <= ' ' }) &&
            Validator
                .isValidEmail(Objects.requireNonNull(email!!.text).toString().trim { it <= ' ' }) &&
            Validator.isValidPhoneNumber(
                Objects.requireNonNull(mobile!!.text).toString().trim { it <= ' ' })
        ) {
            if (operation.equals(
                    OPERATION_ADD,
                    ignoreCase = true
                )
            ) {
                println("inside: $operation")
                SettingsManager.saveCustomer(
                    name!!.text.toString().trim { it <= ' ' },
                    middleName!!.text.toString().trim { it <= ' ' },
                    lastName!!.text.toString().trim { it <= ' ' },
                    email!!.text.toString().trim { it <= ' ' },
                    sdn!!.text.toString().trim { it <= ' ' },
                    mobile!!.text.toString().trim { it <= ' ' },
                    this
                )
                back(null)
            } else if (operation.equals(
                    OPERATION_EDIT,
                    ignoreCase = true
                )
            ) {
                SettingsManager.editCustomer(
                    customer,
                    CustomerViewModel(
                        customer!!.getRef(),
                        name!!.text.toString().trim { it <= ' ' },
                        middleName!!.text.toString().trim { it <= ' ' },
                        lastName!!.text.toString().trim { it <= ' ' },
                        email!!.text.toString().trim { it <= ' ' },
                        sdn!!.text.toString().trim { it <= ' ' },
                        mobile!!.text.toString().trim { it <= ' ' }), this
                )
                back(null)
            }
        } else {
            if (!NAME_IS_VALID) name_l!!.error =
                "getString(R.string.name_invalid_msg)" else name_l?.error = null
            if (!EMAIL_IS_VALID) email_l!!.error =
                "getString(R.string.email_invalid_msg)" else email_l?.error = null
            if (!MOBILE_IS_VALID) mobile_l!!.error =
                "getString(R.string.mobile_invalid_msg)" else mobile_l?.error = null
        }
    }

    fun back(view: View?) {
        onBackPressed()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CustomerActivity::class.java))
        finish()
    }
}