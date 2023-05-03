package company.tap.checkoutsdk.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputLayout
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.Category
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewmodels.PaymentItemViewModel
import kotlinx.android.synthetic.main.activity_customer_create.*
import java.math.BigDecimal
import java.util.*

open class ItemsCreateActivity : AppCompatActivity() {
    private var item_name: AppCompatEditText? = null
    private var item_description: AppCompatEditText? = null
    private var item_quantity: AppCompatEditText? = null
    private var item_price_per_unit: AppCompatEditText? = null
    private var item_discount_unit: AppCompatEditText? = null
    private var checkBoxFixed: CheckBox? = null
    private var checkBoxPercentage: CheckBox? = null
    private var checkBoxNone: CheckBox? = null



    private var itemname_il: TextInputLayout? = null
    private var description_il: TextInputLayout? = null
    private var quantity_il: TextInputLayout? = null
    private var price_il: TextInputLayout? = null
    private var itemDisc_il: TextInputLayout? = null
    private var totalamount_textView: TextView? = null
    private var mSeekbaBar: SeekBar? = null
    private var seekBarText: TextView? = null
    private var itemsSwitch: SwitchCompat? = null

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
    var categoryShipping: Category = Category.DIGITAL_GOODS

    private var itemsShippable: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_create)
        bindViews()

        operation = intent.getStringExtra("operation")!!

        if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            paymentitemsViewModel =
                intent.getSerializableExtra("paymentitems") as PaymentItemViewModel?
            println(" desc : " + paymentitemsViewModel?.getItemIsRequireShip())
            populateItemPaymentFields(paymentitemsViewModel)
        }

    }

    private fun bindViews() {
        item_name = findViewById(R.id.item_name)
        item_description = findViewById(R.id.item_description)
        item_quantity = findViewById(R.id.item_quantity)
        item_price_per_unit = findViewById(R.id.item_price_per_unit)
        item_discount_unit = findViewById(R.id.item_discount_unit)
        checkBoxFixed = findViewById(R.id.checkBoxFixed)
        checkBoxPercentage = findViewById(R.id.checkBoxPercentage)
        checkBoxNone = findViewById(R.id.checkBoxNone)
        mSeekbaBar = findViewById(R.id.seekBar)
        seekBarText = findViewById(R.id.seekbar_value)

        itemname_il = findViewById(R.id.itemname_il)
        description_il = findViewById(R.id.description_il)
        quantity_il = findViewById(R.id.quantity_il)
        price_il = findViewById(R.id.price_il)
        itemDisc_il = findViewById(R.id.itemDisc_il)
        totalamount_textView = findViewById(R.id.totalamount_textView)
        itemsSwitch = findViewById(R.id.itemsSwitch)



        item_discount_unit?.setText("1.0")
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
        itemsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            // do whatever you need to do when the switch is toggled here

            itemsShippable = isChecked
            if (isChecked) {
                categoryShipping = Category.PHYSICAL_GOODS
            } else {
                categoryShipping = Category.DIGITAL_GOODS
            }
            println("itemsShippable>>" + itemsShippable)

        }

    }

    private fun populateItemPaymentFields(paymentItems: PaymentItemViewModel?) {
        item_name?.setText(paymentItems?.getItemsName())
        item_description?.setText(paymentItems?.getItemDescription())
        item_quantity?.setText(paymentItems?.getitemQuantity().toString())
        item_price_per_unit?.setText(paymentItems?.getPricePUnit().toString())
        totalamount_textView?.text = "Total price is" + paymentItems?.getitemTotalPrice().toString()
        item_discount_unit?.setText(paymentItems?.getAmountType()?.getNormalizedValue().toString())
    }

    fun save(view: View?) {

        if (operation.equals(
                OPERATION_ADD,
                ignoreCase = true
            )
        ) {
            println("inside: $operation")

            if (item_discount_unit?.text.toString()
                    .isNullOrEmpty()
            ) item_discount_unit?.setText("0.0")

            SettingsManager.saveItems(
                "",
                item_name?.text.toString().trim { it <= ' ' },
                item_description?.text.toString().trim { it <= ' ' },
                item_quantity?.text.toString().toInt(),
                item_price_per_unit?.text.toString().toDouble(),
                item_quantity?.text.toString().toDouble()
                    .times(item_price_per_unit?.text.toString().toDouble()),
                AmountModificator(amountModificatorType,
                    item_discount_unit?.text?.toString()?.toDouble()
                        ?.let { it1 -> BigDecimal.valueOf(it1) }),
                SettingsManager.getString("key_sdk_transaction_currency", "KWD"),
                categoryShipping,
                null,
                null,
                itemsShippable,
                null,
                null,
                null,
                null,
                null,
                "",
                null,
                this
            )

            back(null)
        } else if (operation.equals(
                OPERATION_EDIT,
                ignoreCase = true
            )
        ) {
            amountModificatorType.let {
                PaymentItemViewModel(
                    null,
                    item_name?.text.toString().trim { it <= ' ' },
                    item_description?.text.toString().trim { it <= ' ' },
                    item_price_per_unit?.text.toString().toDouble(),
                    item_price_per_unit?.text.toString().toDouble().times(item_quantity?.text.toString().toInt()),
                    item_quantity?.text.toString().toInt(),
                    AmountModificator(amountModificatorType, BigDecimal.ZERO),
                    SettingsManager.getString("key_sdk_transaction_currency", "KWD"),
                    categoryShipping,
                    null,
                    null,
                    itemsShippable,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                )
            }.let {
                SettingsManager.editItems(
                    paymentitemsViewModel,
                    it, this
                )
            }
            back(null)
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

    override fun onBackPressed() {
        startActivity(Intent(this, ItemActivity::class.java))
        finish()
    }
}