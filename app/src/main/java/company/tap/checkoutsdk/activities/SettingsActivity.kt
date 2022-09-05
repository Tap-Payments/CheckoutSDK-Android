package company.tap.checkoutsdk.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import company.tap.checkoutsdk.R


/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)


            bindPreferenceSummaryToValue(findPreference("sdk_language"))
            bindPreferenceSummaryToValue(findPreference("key_sdkmode"))
            bindPreferenceSummaryToValue(findPreference("key_sdk_transaction_mode"))
            bindPreferenceSummaryToValue(findPreference("key_sdk_transaction_currency"))
            bindPreferenceSummaryToValue(findPreference("key_amount_name"))
            bindPreferenceSummaryToValue(findPreference("key_package_name"))
             bindPreferenceSummaryToValue(findPreference("key_test_name"))
             bindPreferenceSummaryToValue(findPreference("key_live_name"))
             bindPreferenceSummaryToValue(findPreference("key_merchant_id"))
            bindPreferenceSummaryToValue(findPreference("key_sdk_payment_type"))
            bindPreferenceSummaryToValue(findPreference("key_sdk_card_type"))
            bindPreferenceSummaryToValue(findPreference("showImageKey"))
            bindPreferenceSummaryToValue(findPreference("useOrderObjectKey"))

        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->

            val stringValue = value.toString()
           // println("stringValue>>" + stringValue)

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

            } else if (preference is ListPreference) {
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

            }else if (preference is EditTextPreference) {


                if (preference.getKey().equals("key_amount_name")) {
                    preference.setOnBindEditTextListener { editText->
                        editText.inputType = InputType.TYPE_CLASS_NUMBER

                    }
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue)
                }
               else if (preference.getKey().equals("key_package_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue)
                } else if (preference.getKey().equals("key_test_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue)
                } else if (preference.getKey().equals("key_live_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue)
                } else if (preference.getKey().equals("key_merchant_id")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue)
                }
            } else if(preference is CheckBoxPreference){
                if (preference.getKey().equals("showImageKey")) {
                    // update the changed gallery name to summary filed

                } else  if (preference.getKey().equals("useOrderObjectKey")){

                }
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference?) {
            // Set the listener to watch for value changes.
            preference?.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            if (preference != null && preference is CheckBoxPreference) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, false)
                )
            }else{
                if(preference!=null)
                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference?.context)
                        .getString(preference?.key, "")
                )
            }

            }
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        finish()
        startActivity(intent)


    }

}