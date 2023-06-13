package company.tap.checkout.internal.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import SupportedCurrencies
import company.tap.checkout.internal.api.responses.UserLocalCurrencyModel

const val UserLocalCurrencyModelKey = "userLocalCurrencyModel"
const val UserSupportedLocaleForTransactions = "UserSupportedLocaleForTransactions"

object SharedPrefManager {
    fun getSharedPref(mContext: Context): SharedPreferences {
        return mContext.getSharedPreferences("", Context.MODE_PRIVATE)
    }

    fun <T> saveModelLocally(context: Context, dataToBeSaved: T, keyValueToBeSaved: String) :Boolean {
        val preferences = getSharedPref(context)
        val prefsEditor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(dataToBeSaved)
        prefsEditor.putString(keyValueToBeSaved, json)
       return prefsEditor.commit()
    }


    /**
     * we use this model just to get more results from api :checkoutProfile api
     * by comparing the results
     */
    fun getUserLocalCurrency(context: Context): UserLocalCurrencyModel? {
        val preferences = getSharedPref(context)
        val gson = Gson()
        val json = preferences.getString(UserLocalCurrencyModelKey, "");
        return gson.fromJson(json, UserLocalCurrencyModel::class.java)
    }

    /**
     * we use this model in actuall UI implementation , cause it contains flags in light/dark mode .. etc
     */
    fun getUserSupportedLocaleForTransactions(context: Context): SupportedCurrencies? {
        val preferences = getSharedPref(context)
        val gson = Gson()
        val json = preferences.getString(UserSupportedLocaleForTransactions, "");
        return gson.fromJson(json, SupportedCurrencies::class.java)
    }



}