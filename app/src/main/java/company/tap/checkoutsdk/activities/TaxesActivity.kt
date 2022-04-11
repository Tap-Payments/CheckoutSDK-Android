package company.tap.checkoutsdk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.adapters.ShippingAdapter
import company.tap.checkoutsdk.adapters.TaxesAdapter
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewmodels.ShippingViewModel
import company.tap.checkoutsdk.viewmodels.TaxesViewModel

class TaxesActivity : AppCompatActivity(),  TaxesAdapter.OnClickListenerInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taxes)

        var recyclerView: RecyclerView? = null


        recyclerView = findViewById(R.id.taxes_settings_recycler_view)

        val adapter = TaxesAdapter(generateAddedShippings(), this)

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
    private fun generateAddedShippings(): List<TaxesViewModel> {
        return SettingsManager.getAddedTaxes(this)
    }


    fun back(view: View?) {
        onBackPressed()
    }


    fun addCustomer(view: View?) {
        val intent = Intent(this, TaxesCreateActivity::class.java)
        intent.putExtra("operation", TaxesCreateActivity().OPERATION_ADD)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

    override fun onClick(taxesViewModel: TaxesViewModel?) {
        val intent = Intent(this, TaxesCreateActivity::class.java)
        intent.putExtra("taxes", taxesViewModel)
        intent.putExtra("operation", TaxesCreateActivity().OPERATION_EDIT)
        startActivity(intent)

    }


}