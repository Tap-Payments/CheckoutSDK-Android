package company.tap.checkoutsdk.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewmodels.PaymentItemViewModel
import kotlinx.android.synthetic.main.activity_customer_create.*
import java.util.*

open class ItemsObjectCreateActivity : AppCompatActivity() {
    private var product_id: AppCompatEditText? = null
    private var product_name: AppCompatEditText? = null
    private var tt_amount: AppCompatEditText? = null
    private var item_currency: AppCompatEditText? = null
    private var item_quantoty_unit: AppCompatEditText? = null
    private var vendor_name: AppCompatEditText? = null
    private var checkBoxFixed: CheckBox? = null
    private var checkBoxPercentage: CheckBox? = null
    private var checkBoxNone: CheckBox? = null


    private var totalamount_textView: TextView? = null
    private var mSeekbaBar: SeekBar? = null
    private var seekBarText: TextView? = null

    private var operation = ""
    private var paymentitemsViewModel: PaymentItemViewModel? = null

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
    var amountModificatorType: AmountModificatorType = AmountModificatorType.FIXED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_object_create)
        bindViews()

        operation = intent.getStringExtra("operation")!!

        if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            paymentitemsViewModel =
                intent.getSerializableExtra("paymentitems") as PaymentItemViewModel?
            println(" desc : " + paymentitemsViewModel?.getItemDescription())
            populateItemPaymentFields(paymentitemsViewModel)
        }

    }

    private fun bindViews() {
        product_id = findViewById(R.id.product_id)
        product_name = findViewById(R.id.product_name)
        tt_amount = findViewById(R.id.tt_amount)
        item_currency = findViewById(R.id.item_currency)
        item_quantoty_unit = findViewById(R.id.item_quantoty_unit)
        vendor_name = findViewById(R.id.vendor_name)
        checkBoxFixed = findViewById(R.id.checkBoxFixed)
        checkBoxPercentage = findViewById(R.id.checkBoxPercentage)
        checkBoxNone = findViewById(R.id.checkBoxNone)
        mSeekbaBar = findViewById(R.id.seekBar)
        seekBarText = findViewById(R.id.seekbar_value)



        checkBoxPercentage?.setOnClickListener() {
            checkBoxPercentage?.isChecked = true
            checkBoxFixed?.isChecked = false
            checkBoxNone?.isChecked = false
            amountModificatorType = AmountModificatorType.PERCENTAGE

        }
        checkBoxFixed?.setOnClickListener {
            checkBoxPercentage?.isChecked = false
            checkBoxFixed?.isChecked = true
            checkBoxNone?.isChecked = false
            amountModificatorType = AmountModificatorType.FIXED

        }
        checkBoxNone?.setOnClickListener {
            checkBoxPercentage?.isChecked = false
            checkBoxFixed?.isChecked = false
            checkBoxNone?.isChecked = true

        }
        mSeekbaBar?.post(Runnable {
            seekBarText?.text = mSeekbaBar?.progress.toString()
        })

    }

    private fun populateItemPaymentFields(paymentItems: PaymentItemViewModel?) {
        product_id?.setText(paymentItems?.getItemsName())
        product_name?.setText(paymentItems?.getItemDescription())
        tt_amount?.setText(paymentItems?.getitemQuantity().toString())
        item_currency?.setText(paymentItems?.getPricePUnit().toString())
        totalamount_textView?.text = "Total price is" + paymentItems?.getitemTotalPrice().toString()
        item_quantoty_unit?.setText(paymentItems?.getitemDiscount().toString())


    }


    fun save(view: View?) {

        if (operation.equals(
                OPERATION_ADD,
                ignoreCase = true
            )
        ) {
            /*     println("inside: $operation")
                amountModificatorType.let {
                    if(item_quantoty_unit?.text.toString().isNullOrEmpty()) item_discount_unit?.setText("0.0")

                    SettingsManager.saveItems(
                        product_id?.text.toString().trim { it <= ' ' },
                        product_name?.text.toString().trim { it <= ' ' },
                        tt_amount?.text.toString().toInt(),
                        item_currency?.text.toString().toDouble(),
                        item_quantoty_unit?.text.toString().toDouble().times(item_price_per_unit?.text.toString().toDouble()),
                        it,item_discount_unit?.text.toString().toDouble(),
                        this
                    )
                }*/
            //   back(null)
        } else if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            amountModificatorType.let {
                /* PaymentItemViewModel(
                        product_id?.text.toString().trim { it <= ' ' },
                        product_name?.text.toString().trim { it <= ' ' },
                        tt_amount?.text.toString().toDouble() ,
                        item_currency?.text.toString().toDouble().times( item_quantity?.text.toString().toInt()),
                        item_quantoty_unit?.text.toString().toInt(), it,item_discount_unit?.text.toString().toDouble()
                    )*/
                /*  }.let {
                    SettingsManager.editItems(
                        paymentitemsViewModel,
                        it, this
                    )
                }*/
                // back(null)
            }
            /* } else {
            if (!NAME_IS_VALID) itemname_il?.error =
                "getString(R.string.name_invalid_msg)" else name_il?.error = null
            if (!EMAIL_IS_VALID) description_il?.error =
                "getString(R.string.email_invalid_msg)" else description_il?.error = null
            if (!MOBILE_IS_VALID) quantity_il?.error =
                "getString(R.string.mobile_invalid_msg)" else quantity_il?.error = null
        }*/
        }

        fun back(view: View?) {
            onBackPressed()
        }

        /*  override fun onBackPressed() {
        startActivity(Intent(this, ItemActivity::class.java))
        finish()
    }*/
    }
}