package company.tap.checkout.internal.cache

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.SyncStateContract
import com.google.gson.Gson
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.internal.apiresponse.UserLocalCurrencyModel

const val UserLocalCurrencyModelKey = "userLocalCurrencyModel"
const val UserSupportedLocaleForTransactions = "UserSupportedLocaleForTransactions"

object SharedPrefManager {
    fun getSharedPref(mContext: Context): SharedPreferences {
        return mContext.getSharedPreferences("", Context.MODE_PRIVATE)
    }

    fun setPrefVal(mContext: Context, key: String?, value: String?) {
        if (key != null) {
            val edit: SharedPreferences.Editor = getSharedPref(mContext).edit()
            edit.putString(key, value)
            edit.commit()
        }
    }


    fun <T> saveModelLocally(context: Context, dataToBeSaved: T, keyValueToBeSaved: String) {
        val preferences = getSharedPref(context)
        val prefsEditor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(dataToBeSaved)
        prefsEditor.putString(keyValueToBeSaved, json)
        prefsEditor.apply()
    }

    fun getUserLocalCurrency(context: Context): UserLocalCurrencyModel {
        val preferences = getSharedPref(context)
        val gson = Gson()
        val json = preferences.getString(UserLocalCurrencyModelKey, "");
        val data = gson.fromJson(json, UserLocalCurrencyModel::class.java)
        return data
    }

    fun getUserSupportedLocaleForTransactions(context: Context): SupportedCurrencies? {
        val preferences = getSharedPref(context)
        val gson = Gson()
        val json = preferences.getString(UserSupportedLocaleForTransactions, "");
        val data = gson.fromJson(json, SupportedCurrencies::class.java)
        return data
    }

}