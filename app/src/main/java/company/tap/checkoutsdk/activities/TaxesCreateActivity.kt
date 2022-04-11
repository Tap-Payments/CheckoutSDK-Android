package company.tap.checkoutsdk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.utils.Validator
import company.tap.checkoutsdk.viewmodels.ShippingViewModel
import company.tap.checkoutsdk.viewmodels.TaxesViewModel
import java.util.*

class TaxesCreateActivity : AppCompatActivity() {
    private var taxesName: AppCompatEditText? = null
    private var taxesDescription: AppCompatEditText? = null
    private var taxesAmount: AppCompatEditText? = null

    private var name_taxes_il: TextInputLayout? = null
    private var taxes_description_il: TextInputLayout? = null
    private var taxes_amount_il: TextInputLayout? = null

    private var taxesViewModel: TaxesViewModel? = null
    private var operation = ""
    @JvmField
    var OPERATION_ADD = "add"
    @JvmField
    var OPERATION_EDIT = "edit"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxes_create)
        bindViews()

        operation = intent.getStringExtra("operation")!!

        if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            taxesViewModel = intent.getSerializableExtra("taxes") as TaxesViewModel?
            println("taxesViewModel : " + taxesViewModel?.getTaxesName())
            populateShippingFields(taxesViewModel)
        }
    }
    private fun populateShippingFields(taxesViewModel: TaxesViewModel?) {
        taxesName?.setText(taxesViewModel?.getTaxesName())
        taxesDescription?.setText(taxesViewModel?.getTaxesDecsription())
        taxesAmount?.setText(taxesViewModel?.getTaxesAmount())
    }

    fun save(view: View?) {
        if (Validator.isValidName(Objects.requireNonNull(taxesName!!.text).toString().trim { it <= ' ' })
        ) {
            if (operation.equals(
                    OPERATION_ADD,
                    ignoreCase = true
                )
            ) {
                println("inside: $operation")
                SettingsManager.saveShipping(
                    taxesName?.text.toString().trim { it <= ' ' },
                    taxesDescription?.text.toString().trim { it <= ' ' },
                    taxesAmount?.text.toString().trim { it <= ' ' },
                    this
                )
                back(null)
            } else if (operation.equals(
                    OPERATION_EDIT,
                    ignoreCase = true
                )
            ) {
                SettingsManager.editTaxes(
                    taxesViewModel,
                    TaxesViewModel(
                        taxesName?.text.toString().trim { it <= ' ' },
                        taxesDescription?.text.toString().trim { it <= ' ' },
                        taxesAmount?.text.toString().trim { it <= ' ' }), this
                )
                back(null)
            }
        }

    }

    private fun bindViews() {
        taxesName = findViewById(R.id.edit_taxes_name)
        taxesDescription = findViewById(R.id.edit_taxes_description)
        taxesAmount = findViewById(R.id.edit_taxes_amount)

        name_taxes_il = findViewById(R.id.name_taxes_il)
        taxes_description_il = findViewById(R.id.taxes_description_il)
        taxes_amount_il = findViewById(R.id.taxes_amount_il)
    }

    fun back(view: View?) {
        onBackPressed()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, TaxesActivity::class.java))
        finish()
    }

}