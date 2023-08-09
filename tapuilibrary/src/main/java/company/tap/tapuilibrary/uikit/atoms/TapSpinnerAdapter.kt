package company.tap.tapuilibrary.uikit.atoms

import SupportedCurrencies
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.AppColorTheme
import company.tap.tapuilibrary.uikit.formatTo2DecimalPoints
import company.tap.tapuilibrary.uikit.getColorWithoutOpacity
import java.security.AccessController.getContext


enum class SPINNER_VIEW_TYPE(var type: Int) {
    VIEW(0),
    DROPDOWNVIEW(1)
}

class TapSpinnerAdapter(
    context: Context,
    resouceId: Int,
    textviewId: Int,
    list: MutableList<SupportedCurrencies>
) :
    ArrayAdapter<SupportedCurrencies?>(
        context, resouceId, textviewId,
        list as List<SupportedCurrencies?>
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return headerView(convertView, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return rowview(convertView, position)
    }


    @SuppressLint("SetTextI18n")
    private fun rowview(convertView: View?, position: Int): View {
        val rowItem: SupportedCurrencies? = getItem(position)
        val holder: viewHolder
        var rowview = convertView
        holder = viewHolder()
        val flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rowview = flater.inflate(R.layout.custom_spinner, null, false)
        holder.txtTitle = (rowview.findViewById<View>(R.id.tv_spinnervalue) as TextView).apply {
            val colorToBeParsed =
                (ThemeManager.getValue<String>(AppColorTheme.ControlCurrencyWidgetMessageColor)).getColorWithoutOpacity()
            setTextColor(Color.parseColor(colorToBeParsed))
            typeface = Typeface.createFromAsset(
                context.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
            text = rowItem?.currency.toString() + " " + rowItem?.amount?.toString()
        }
        rowview.tag = holder
        holder.imageView = rowview.findViewById<View>(R.id.iv_flag) as ImageView
        Glide.with(context).load(rowItem?.flag).into(holder.imageView!!)



        return rowview
    }

    @SuppressLint("SetTextI18n")
    private fun headerView(convertView: View?, position: Int): View {
        val rowItem: SupportedCurrencies? = getItem(position)
        val holder = viewHolder()
        var headerView = convertView
        if (headerView == null) {
            val flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            headerView = flater.inflate(R.layout.spinner_header, null, false)
            holder.txtTitle =
                (headerView?.findViewById<TextView>(R.id.tv_spinnervalue) as TextView).apply {
                    val colorToBeParsed =
                        (ThemeManager.getValue<String>(AppColorTheme.ControlCurrencyWidgetMessageColor)).getColorWithoutOpacity()
                    setTextColor(Color.parseColor(colorToBeParsed))
                    typeface = Typeface.createFromAsset(
                        context.assets, TapFont.tapFontType(
                            TapFont.RobotoRegular
                        )
                    )
                    text = rowItem?.currency.toString() + " " + rowItem?.amount?.toString()
                }
        }
        return headerView
    }

    private inner class viewHolder {
        var txtTitle: TextView? = null
        var imageView: ImageView? = null
    }
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}


class CustomDropDownAdapter(
    val context: Context,
    var dataSource: List<SupportedCurrencies>,
) : BaseAdapter() {
    private var hidingItemIndex: Int=0

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        with(vh.label) {
            val colorToBeParsed =
                (ThemeManager.getValue<String>(AppColorTheme.ControlCurrencyWidgetMessageColor)).getColorWithoutOpacity()
            setTextColor(Color.parseColor(colorToBeParsed))
            typeface = Typeface.createFromAsset(
                context.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
            text =
                dataSource.get(position).currency.toString() + " " + dataSource[position].amount.toString()
                    .formatTo2DecimalPoints()
        }

        Glide.with(context).load(dataSource[position].flag).into(vh.img)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var v: View? = null

        if (position == hidingItemIndex) {
            val tv = TextView(context)
            tv.visibility = View.GONE
            tv.height = 0
            v = tv
        } else {
            v = super.getDropDownView(position, null, parent);

        }
        return v
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    fun hideItemPosition(position: Int) {
        this.hidingItemIndex = position
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val label: TextView
        val img: ImageView

        init {
            label = row?.findViewById(R.id.tv_spinnervalue) as TextView
            img = row.findViewById(R.id.iv_flag) as ImageView
        }
    }

}