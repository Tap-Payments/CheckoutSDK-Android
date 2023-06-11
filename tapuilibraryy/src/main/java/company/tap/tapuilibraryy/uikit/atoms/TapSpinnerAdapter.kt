package company.tap.tapuilibraryy.uikit.atoms

import SupportedCurrencies
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath


class TapSpinnerAdapter(
    context: Context,
    resouceId: Int,
    textviewId: Int,
    list: MutableList<SupportedCurrencies>
) :
    ArrayAdapter<SupportedCurrencies?>(context, resouceId, textviewId,
        list as List<SupportedCurrencies?>
    ) {
    var flater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowview(convertView, position)!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return rowview(convertView, position)
    }

    @SuppressLint("SetTextI18n")
    private fun rowview(convertView: View?, position: Int): View? {
        val rowItem: SupportedCurrencies? = getItem(position)
        val holder: viewHolder
        var rowview = convertView

        if (rowview == null) {
            holder = viewHolder()
            flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowview = flater?.inflate(R.layout.custom_spinner, null, false)
            holder.txtTitle = rowview?.findViewById<View>(R.id.tv_spinnervalue) as TextView
            holder.imageView = rowview.findViewById<View>(R.id.iv_flag) as ImageView
            rowview.tag = holder
        } else {
            holder = rowview.tag as viewHolder
        }
        rowview.setPadding(0,convertView?.paddingTop?: 0, 0,convertView?.paddingBottom ?: 0)
        Glide.with(context).load(rowItem?.flag).into(holder.imageView!!)
        holder.txtTitle?.text =  rowItem?.currency.toString() + " " +rowItem?.amount.toString()
        holder.txtTitle?.setTextColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetCurrencyDropDownLabelColor))
        holder.txtTitle?.typeface = Typeface.createFromAsset(
            context.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        return rowview
    }

    private inner class viewHolder {
        var txtTitle: TextView? = null
        var imageView: ImageView? = null
    }
}

data class currncyData(var image: Drawable, var price: String)
