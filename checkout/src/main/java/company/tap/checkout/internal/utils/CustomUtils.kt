package company.tap.checkout.internal.utils

import android.content.Context
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import company.tap.checkout.internal.interfaces.BaseLayouttManager


/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CustomUtils {



     fun showDialog(title: String, messageString: String, context: Context, btnType: String? = null,baseLayouttManager: BaseLayouttManager?) {
         val builder = iOSDialogBuilder(context)
         builder
             .setTitle(title)
             .setSubtitle(messageString)
             .setBoldPositiveLabel(true)
             .setCancelable(false)
                 if(btnType=="twobtns"){
                  builder.setPositiveListener("YES") { dialog ->
                         dialog.dismiss()
                      baseLayouttManager?.didDialogueExecute("YES")

                     }
                     builder.setNegativeListener("NO") { dialog ->
                         dialog.dismiss()

                         baseLayouttManager?.didDialogueExecute("NO")
                     }
                 }else {
                    builder .setPositiveListener("OK") { dialog ->
                         dialog.dismiss()
                        baseLayouttManager?.didDialogueExecute("OK")
                     }
                 }
             .build().show()


    }
}