package company.tap.checkout.internal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.models.ItemsModel
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.ItemViewDataSource
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.views.TapItemListView


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class ItemAdapter(private var checkoutViewModel: CheckoutViewModel,private var bottomSheetLayout: FrameLayout) :
    RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private var previousExpandedPosition = -1
    private var mExpandedPosition = -1
    private lateinit var itemViewAdapter: TapItemListView
    private lateinit var context: Context
    private var arrayModifiedItem : ArrayList<Any> = ArrayList()
    private var adapterContentItems: List<ItemsModel> = java.util.ArrayList()
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ItemHolder {


        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_view_adapter,
            parent,
            false
        )
        context = parent.context
        itemViewAdapter = v.findViewById(R.id.amount_item_view)
        return ItemHolder(v)
    }

    fun updateAdapterData(adapterContentItems: List<ItemsModel>) {
        println("adapterContentItems val"+adapterContentItems)
        this.adapterContentItems = adapterContentItems
        notifyDataSetChanged()

    }
    override fun getItemCount() = adapterContentItems.size


    class ItemHolder(v: View) : RecyclerView.ViewHolder(v)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        initView(holder, position)
    }

    @SuppressLint("SetTextI18n")
    private fun initView(holder: ItemHolder, position: Int) {
        val descriptionTextView = holder.itemView.findViewById<TapTextView>(R.id.description_textView)
        val nameText = holder.itemView.findViewById<TapTextView>(R.id.brief_description)

        val itemSeparator = holder.itemView.findViewById<TapSeparatorView>(R.id.itemseparator)
        val totalQuantity = holder.itemView.findViewById<TapTextView>(R.id.total_quantity)
      //  val discount = holder.itemView.findViewById<TapTextView>(R.id.discount_text)
      //  val quantityRelative = holder.itemView.findViewById<RelativeLayout>(R.id.quantityRelative)
        val totalAmount = holder.itemView.findViewById<TapTextView>(R.id.total_amount)
        val mainViewLinear = holder.itemView.findViewById<LinearLayout>(R.id.mainViewLinear)
        val itemName = holder.itemView.findViewById<TapTextView>(R.id.item_title)
        itemName.filters = arrayOf<InputFilter>(AllCaps())
        val isExpanded = position == mExpandedPosition
        if(adapterContentItems.isNotEmpty()){
            for (i in adapterContentItems.indices) {
                descriptionTextView.text = adapterContentItems[position].description
                descriptionTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
                holder.itemView.isActivated = isExpanded
               // totalQuantity.text = adapterContentItems[position].quantity?.toString()
               /* itemViewAdapter.setItemViewDataSource(
                    getItemViewDataSource(null, CurrencyFormatter.currencyFormat(adapterContentItems[position].totalAmount.toString()),adapterContentItems[position].currency ,  CurrencyFormatter.currencyFormat(adapterContentItems[position].totalAmount.toString()), adapterContentItems[position].currency, adapterContentItems[position].quantity.toString())
                )*/

//replaced PaymentDataSource.getSelectedCurrency with PaymentDataSource.getSelectedCurrencySymbol
              //  println("PaymentDataSource.getSelectedCurrencySymbol()"+PaymentDataSource.getSelectedCurrencySymbol())
                PaymentDataSource.getSelectedCurrencySymbol()?.let {
                    PaymentDataSource.getSelectedCurrencySymbol()?.let { it1 ->
                        adapterContentItems[position].quantity?.toString()?.let { it2 ->
                            getItemViewDataSource(adapterContentItems[position]?.name.toString().toUpperCase(), CurrencyFormatter.currencyFormat(adapterContentItems[position].getPlainAmount().toString() ),
                                it,CurrencyFormatter.currencyFormat(adapterContentItems[position].amount.toString() ),
                                it1,
                                it2
                            )
                        }
                    }
                }?.let {
                    itemViewAdapter.setItemViewDataSource(
                        it
                    )
                }
            }
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F);
            nameText?.text = adapterContentItems[position].name
            // This to be handled in ui kit
            totalAmount.text = PaymentDataSource.getSelectedCurrencySymbol()+ " " + CurrencyFormatter.currencyFormat(adapterContentItems[position].amount.toString())+ " x " +adapterContentItems[position].quantity?.toString()

        }else{
           // descriptionTextView.text = adapterContentItems[0].description
            descriptionTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.itemView.isActivated = isExpanded
        }
        holder.itemView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))

        onItemClickAction(holder, position, isExpanded)
        showHideDescText(isExpanded, position, nameText)
        setTheme(descriptionTextView, nameText, totalQuantity, totalAmount, itemName, itemSeparator, mainViewLinear,null)
      //  nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F);

        if(LocalizationManager.getLocale(context).language=="en"){
          setFontsEnglish(itemName, totalAmount, nameText, descriptionTextView, totalQuantity)
      }else setFontsArabic(itemName, totalAmount, nameText, descriptionTextView, totalQuantity)

        checkItemListPosition(position, totalAmount, itemName)
    }

    private fun showHideDescText(isExpanded: Boolean, position: Int, descText: TapTextView?) {
        if (isExpanded) {
            previousExpandedPosition = position
           // descText?.text = LocalizationManager.getValue("hideDesc", "ItemList")
            itemViewAdapter.collapseImageView?.visibility= View.VISIBLE
            itemViewAdapter.expandImageView?.visibility= View.GONE
            checkoutViewModel.translateViewToNewHeight(bottomSheetLayout.measuredHeight,true)


        } else {
           // descText?.text = LocalizationManager.getValue("showDesc", "ItemList")
            itemViewAdapter.expandImageView?.visibility= View.VISIBLE
            itemViewAdapter.collapseImageView?.visibility= View.GONE
        }
    }

    private fun onItemClickAction(holder: ItemHolder, position: Int, isExpanded: Boolean) {
        holder.itemView.setOnClickListener {
            itemViewAdapter.visibility = View.VISIBLE
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun checkItemListPosition(
        position: Int,
       // discount: TapTextView?,
        totalAmount: TapTextView?,
        itemName: TapTextView?
    ) {
        if(adapterContentItems.isEmpty()) {
            if (position % 2 == 0) {
                //discount?.visibility = View.VISIBLE
               // discount?.text = LocalizationManager.getValue("Discount", "ItemList")
                totalAmount?.paintFlags = totalAmount?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
                itemName?.text = "ITEM TITLE " + adapterContentItems[position]
            } else {
                //discount?.visibility = View.INVISIBLE
                totalAmount?.paintFlags = totalAmount?.paintFlags?.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())!!
                itemName?.text =
                    "VERY LOOOONNGGGG ITEM TITLE ITEM TITLE TITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLETITLE ITEM TITLE " + adapterContentItems[position]
            }
        }else
            for (i in adapterContentItems.indices) {
                itemName?.text = adapterContentItems[position].name
                if(adapterContentItems[position].discount?.amnttype?.name == AmountModificatorType.PERCENTAGE.name){
                 //   discount?.visibility = View.VISIBLE
                 //  discount?.text = adapterContentItems[i].discount.toString()
                //    discount?.text = LocalizationManager.getValue("Discount", "ItemList")
                    totalAmount?.paintFlags = totalAmount?.paintFlags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!
                    totalAmount.text = adapterContentItems[position].getPlainAmount().toString()

                }else{
                   // discount?.visibility = View.INVISIBLE
                    //totalAmount?.paintFlags = totalAmount?.paintFlags?.and(Paint.STRIKE_THRU_TEXT_FLAG.inv())!!
                  //  totalAmount?.text = "KWD 4 x 3"
                }
            }

    }


    private fun setTheme(
        descriptionTextView: TapTextView?,
       // discount: TapTextView?,
        descText: TapTextView?,
        totalQuantity: TapTextView?,
        totalAmount: TapTextView?,
        itemName: TapTextView?,
        itemSeparator: TapSeparatorView?,
        mainViewLinear: LinearLayout?,
        quantityRelative: RelativeLayout?
    ) {

        itemViewAdapter.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))
        val descriptionTextViewTheme = TextViewTheme()
        descriptionTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("itemsList.item.descLabelColor"))
        descriptionTextViewTheme.backgroundColor = Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor"))
        descriptionTextViewTheme.textSize = ThemeManager.getFontSize("itemsList.item.descLabelFont")
        descriptionTextViewTheme.font = ThemeManager.getFontName("itemsList.item.descLabelFont")
        descriptionTextView?.setTheme(descriptionTextViewTheme)
       // discount?.setTheme(descriptionTextViewTheme)
        descText?.setTheme(descriptionTextViewTheme)

        mainViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.item.backgroundColor")))

        val totalQuantityTextViewTheme = TextViewTheme()
        totalQuantityTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.count.countLabelColor"))
        totalQuantityTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.count.countLabelFont")
        totalQuantityTextViewTheme.font =
            ThemeManager.getFontName("itemsList.item.count.countLabelFont")
        totalQuantity?.setTheme(totalQuantityTextViewTheme)


        val totalAmountTextViewTheme = TextViewTheme()
        totalAmountTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.calculatedPriceLabelColor"))
        totalAmountTextViewTheme.textSize =
            ThemeManager.getFontSize("itemsList.item.calculatedPriceLabelFont")
        totalAmountTextViewTheme.font =
            ThemeManager.getFontName("itemsList.item.calculatedPriceLabelFont")
        totalAmount?.setTheme(totalAmountTextViewTheme)

        quantityRelative?.let {
            setBorderedView(
                it,
                50f,
                0.0f,
                Color.parseColor(ThemeManager.getValue("itemsList.item.count.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("itemsList.item.count.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("itemsList.item.count.backgroundColor"))
            )
        }


        val itemTitleTextViewTheme = TextViewTheme()
        itemTitleTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("itemsList.item.titleLabelColor"))
        itemTitleTextViewTheme.textSize = ThemeManager.getFontSize("itemsList.item.titleLabelFont")
        itemTitleTextViewTheme.font = ThemeManager.getFontName("itemsList.item.titleLabelFont")
        itemName?.setTheme(itemTitleTextViewTheme)


        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor = Color.parseColor(ThemeManager.getValue("itemsList.separatorColor"))
        itemSeparator?.setTheme(separatorViewTheme)

    }


    private fun setFontsEnglish(
        itemName: TapTextView?, totalAmount: TapTextView?,
       // discount: TapTextView?,
        descText: TapTextView?,
        descriptionTextView: TapTextView?, totalQuantity: TapTextView?
    ) {
        itemName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        totalAmount?.typeface = Typeface.createFromAsset(
            context.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        /*discount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )*/
        descText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        descriptionTextView?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        // itemViewAdapter.setItemViewDataSource(getItemViewDataSource())
        totalQuantity?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    private fun setFontsArabic(
        itemName: TapTextView?, totalAmount: TapTextView?,
       // discount: TapTextView?,
        descText: TapTextView?,
        descriptionTextView: TapTextView?, totalQuantity: TapTextView?
    ) {
        itemName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        totalAmount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
       /* discount?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )*/
        descText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        descriptionTextView?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        // itemViewAdapter.setItemViewDataSource(getItemViewDataSource())
        totalQuantity?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
    }

    private fun getItemViewDataSource(
        itemTitle: String?,
        itemAmount: String,
        itemAmountCurr:String, totalAmount: String,
        totalAmountCurr:String, totalQuantity: String): ItemViewDataSource {
        return ItemViewDataSource(
            itemTitle = itemTitle,
            itemAmount = itemAmount,
            itemAmountCurr = itemAmountCurr,
            totalAmount =totalAmount,
            totalAmountCurr = totalAmountCurr,
            totalQuantity = totalQuantity
        )
    }




}

