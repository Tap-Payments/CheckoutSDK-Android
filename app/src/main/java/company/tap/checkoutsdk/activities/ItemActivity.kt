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
import company.tap.checkoutsdk.adapters.CustomerAdapter
import company.tap.checkoutsdk.adapters.ItemsAdapter
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.checkoutsdk.viewmodels.CustomerViewModel
import company.tap.checkoutsdk.viewmodels.PaymentItemViewModel

class ItemActivity : AppCompatActivity() ,ItemsAdapter.OnClickListenerInterface {
    var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        recyclerView = findViewById(R.id.items_settings_recycler_view)

        val adapter = ItemsAdapter(generateRegisteredItems(), this)

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
   /* private fun generateRegisteredItems(): List<PaymentItemViewModel> {
        return SettingsManager.getAddedItems(this)
    }*/


    fun back(view: View?) {
        onBackPressed()
    }
    private fun generateRegisteredItems(): List<PaymentItemViewModel> {
        return SettingsManager.getRegisteredItems(this)
    }

    fun addCustomer(view: View?) {
        val intent = Intent(this, ItemsCreateActivity::class.java)
        intent.putExtra("operation", ItemsCreateActivity().OPERATION_ADD)
        startActivity(intent)
    }

    override fun onClick(viewModel: PaymentItemViewModel?) {
        val intent = Intent(this, ItemsCreateActivity::class.java)
        intent.putExtra("paymentitems", viewModel)
        intent.putExtra("operation", ItemsCreateActivity().OPERATION_EDIT)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

}