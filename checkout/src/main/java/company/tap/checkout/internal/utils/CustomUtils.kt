package company.tap.checkout.internal.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {

     fun showDialog(title: String, messageString :String,context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(messageString)
    //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(context,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(context,
                    android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(context,
                    "Maybe", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}