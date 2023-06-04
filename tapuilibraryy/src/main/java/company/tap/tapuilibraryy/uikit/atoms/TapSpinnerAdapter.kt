package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import company.tap.tapuilibraryy.R



class TapSpinnerAdapter(context: Context, resouceId: Int, textviewId: Int, list: List<currncyData>) :
    ArrayAdapter<currncyData?>(context, resouceId, textviewId, list) {
    var flater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowview(convertView, position)!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return rowview(convertView, position)
    }

    private fun rowview(convertView: View?, position: Int): View? {
        val rowItem: currncyData? = getItem(position)
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
        holder.imageView?.setImageDrawable(rowItem?.image)
        holder.txtTitle?.text = rowItem?.price
        return rowview
    }

    private inner class viewHolder {
        var txtTitle: TextView? = null
        var imageView: ImageView? = null
    }
}
data class currncyData(var image: Drawable, var price:String)
