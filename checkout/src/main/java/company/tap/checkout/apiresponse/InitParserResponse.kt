package company.tap.checkout.apiresponse

import android.content.Context
import java.io.IOException

/**
 * Created by AhlaamK on 8/31/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        println("file name in data from assets $fileName")
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString


}