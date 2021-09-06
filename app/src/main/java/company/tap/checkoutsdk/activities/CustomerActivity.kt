package company.tap.checkoutsdk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkoutsdk.CustomerAdapter
import company.tap.checkoutsdk.CustomerViewModel
import company.tap.checkoutsdk.R

import company.tap.checkoutsdk.manager.SettingsManager

class CustomerActivity : AppCompatActivity(), CustomerAdapter.OnClickListenerInterface  {
    var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        recyclerView = findViewById(R.id.customers_settings_recycler_view)

        val adapter = CustomerAdapter(generateRegisteredCustomers(), this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView?.setLayoutManager(layoutManager)

        recyclerView?.setAdapter(adapter)

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        val divider =
            ContextCompat.getDrawable(this, R.drawable.recycler_divider)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }
        recyclerView?.addItemDecoration(dividerItemDecoration)

    }
    private fun generateRegisteredCustomers(): List<CustomerViewModel> {
        return SettingsManager.getRegisteredCustomers(this)
    }


    fun back(view: View?) {
        onBackPressed()
    }


    fun addCustomer(view: View?) {
        val intent = Intent(this, CustomerCreateActivity::class.java)
        intent.putExtra("operation", CustomerCreateActivity().OPERATION_ADD)
        startActivity(intent)
    }

    override fun onClick(viewModel: CustomerViewModel?) {
        val intent = Intent(this, CustomerCreateActivity::class.java)
        intent.putExtra("customer", viewModel)
        intent.putExtra("operation", CustomerCreateActivity().OPERATION_EDIT)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

}