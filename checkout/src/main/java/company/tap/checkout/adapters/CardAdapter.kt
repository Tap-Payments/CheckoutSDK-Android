package company.tap.checkout.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import company.tap.cardbusinesskit.testmodels.Payment_methods
import company.tap.checkout.R
import company.tap.checkout.interfaces.OnCardSelectedActionListener

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardAdapter(private val arrayList: List<Payment_methods>, private val onCardSelectedActionListener: OnCardSelectedActionListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            TYPE_SAVED_CARD -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_saved_card, parent, false)
                SavedViewHolder(
                    view
                )
            }
            TYPE_SINGLE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_knet, parent, false)
                SingleViewHolder(
                    view
                )
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_gopay, parent, false)
                GoPayViewHolder(
                    view
                )
            }
        }
    }

    override fun getItemCount() = arrayList.size


    override fun getItemViewType(position: Int): Int {


        return if (arrayList[position].equals(1)|| arrayList[position].equals(3) || arrayList[position].equals(5)) {
            TYPE_SINGLE
        } else if (arrayList[position].equals(2)) {
            TYPE_GO_PAY
        } else {
            TYPE_SAVED_CARD
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_SAVED_CARD) {
            if (selectedPosition == position) {
                holder.itemView.setBackgroundResource(R.drawable.border_shadow)
            } else
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)
            (holder as SavedViewHolder)
            holder.itemView.setOnClickListener {
                onCardSelectedActionListener?.onCardSelectedAction(false)
                selectedPosition = position
                notifyDataSetChanged()
            }
        } else if (getItemViewType(position) == TYPE_SINGLE) {

            if (selectedPosition == position)
                holder.itemView.setBackgroundResource(R.drawable.border_shadow)
            else
                holder.itemView.setBackgroundResource(R.drawable.border_unclick)
            (holder as SingleViewHolder)
            holder.itemView.setOnClickListener {
                onCardSelectedActionListener?.onCardSelectedAction(false)
                selectedPosition = position
                notifyDataSetChanged()
            }

        } else {
            if (selectedPosition == position)
                holder.itemView.setBackgroundResource(R.drawable.border_gopay)
            else
                holder.itemView.setBackgroundResource(R.drawable.border_gopay_unclick)
            (holder as GoPayViewHolder)
            holder.itemView.setOnClickListener {
                onCardSelectedActionListener?.onCardSelectedAction(true)
                selectedPosition = position
                notifyDataSetChanged()
            }

        }
    }

    internal class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    internal class GoPayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val TYPE_SAVED_CARD = 1
        const val TYPE_SINGLE = 2
        const val TYPE_GO_PAY = 3
    }
}