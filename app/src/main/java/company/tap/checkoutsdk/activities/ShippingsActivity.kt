package company.tap.checkoutsdk.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.ShippingAdapter
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewmodels.ShippingViewModel
import company.tap.checkoutsdk.viewmodels.TaxesViewModel

class ShippingsActivity : AppCompatActivity(), ShippingAdapter.OnClickListenerInterface  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shippings)

    var recyclerView: RecyclerView? = null


        recyclerView = findViewById(R.id.shippings_settings_recycler_view)

        val adapter = ShippingAdapter(generateAddedShippings(), this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager

        recyclerView?.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        val divider =
            ContextCompat.getDrawable(this, R.drawable.recycler_divider)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }
        recyclerView?.addItemDecoration(dividerItemDecoration)

    }
    private fun generateAddedShippings(): List<ShippingViewModel> {
        return SettingsManager.getAddedShippings(this)
    }


    fun back(view: View?) {
        onBackPressed()
    }


    fun addCustomer(view: View?) {
        val intent = Intent(this, ShippingCreateActivity::class.java)
        intent.putExtra("operation", ShippingCreateActivity().OPERATION_ADD)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

 

    override fun onClick(shippingViewModel: ShippingViewModel?) {
        val intent = Intent(this, ShippingCreateActivity::class.java)
        intent.putExtra("shipping", shippingViewModel)
        intent.putExtra("operation", ShippingCreateActivity().OPERATION_EDIT)
        startActivity(intent)
    }
}