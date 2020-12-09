package company.tap.checkout.internal.utils

import android.R
import android.content.Context
import android.widget.Toast
import com.gdacciaro.iOSDialog.iOSDialogBuilder


/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {

     fun showDialog(title: String, messageString: String, context: Context, btnType:String?=null) {
         val builder = iOSDialogBuilder(context)
         builder
             .setTitle(title)
             .setSubtitle(messageString)
             .setBoldPositiveLabel(true)
             .setCancelable(false)
                 if(btnType=="twobtns"){
                  builder.setPositiveListener("YES") { dialog ->
                         dialog.dismiss()
                     }
                     builder.setNegativeListener("No") { dialog ->

                     }
                 }else {
                    builder .setPositiveListener("OK") { dialog ->
                         dialog.dismiss()
                     }
                 }
             .build().show()


    }
}