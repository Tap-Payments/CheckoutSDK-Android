# CheckoutSDK-Android
CheckoutSDK Android [goSell API][1].

[![Platform](https://img.shields.io/badge/platform-Android-inactive.svg?style=flat)](https://github.com/Tap-Payments/CheckoutSDK-Android/)
[![Documentation](https://img.shields.io/badge/documentation-100%25-bright%20green.svg)](https://github.com/Tap-Payments/CheckoutSDK-Android/)
[![SDK Version](https://img.shields.io/badge/minSdkVersion-21-blue.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)
[![SDK Version](https://img.shields.io/badge/targetSdkVersion-30-informational.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)

CheckoutSDK Android compatible version of CheckoutSDK library that fully covers payment/authorization/card saving process inside your Android application.
# Table of Contents
---
1. [Requirements](#requirements)
2. [Installation](#installation)
   1. [Include CheckoutSDK library as a dependency module in your project](#include_library_to_code_locally)
   2. [Installation with jitpack](#installation_with_jitpack)

3. [Setup](#setup)
    1. [CheckoutSDK Class Properties](#setup_checkoutsdk_class_properties)
4. [Usage](#usage)
    1. [Configure SDK with Required Data](#configure_sdk_with_required_data)
    2. [Configure SDK Look and Feel](#configure_sdk_look_and_feel)
    3. [Configure SDK Session](#configure_sdk_Session)
    4. [Configure SDK Transaction Mode](#configure_sdk_transaction_mode)
    5. [Use Tap PayButton](#init_pay_button)
    6. [List Saved Cards](#list_saved_cards)
    6. [Open SDK Interfaces](#sdk_open_interfaces)
    7. [Open SDK ENUMs](#sdk_open_enums)
    8. [Open SDK Models](#sdk_open_models)
5. [SDKSession Delegate](#sdk_delegate)
    1. [Payment Success Callback](#payment_success_callback)
    2. [Payment Failure Callback](#payment_failure_callback)
    3. [Authorization Success Callback](#authorization_success_callback)
    4. [Authorization Failure Callback](#authorization_failure_callback)
    5. [Card Saving Success Callback](#card_saving_success_callback)
    6. [Card Saving Failure Callback](#card_saving_failure_callback)
    7. [Card Tokenized Success Callback](#card_tokenized_success_callback)
    8. [Saved Cards List Callback](#saved_cards_list_callback)
    9. [Session Other Failure Callback](#session_error_callback)
    10. [Invalid Card Details](#invalid_card_details)
    11. [Backend Un-known Error](#backecnd_unknow_error)
    12. [Invalid Transaction Mode](#invalid_transaction_mode)
    13. [Session Is Starting Callback](#session_is_starting_callback)
    14. [Session Has Started Callback](#session_has_started_callback)
    15. [Session Failed To Start Callback](#session_failed_to_start_callback)
    16. [Session Cancel Callback](#session_cancel_callback)
    17. [User Enabled Save CARD](#user_enabled_save_card_option)
6. [Documentation](#docs)

<a name="requirements"></a>
# Requirements
---
To use the SDK the following requirements must be met:

1. **Android Studio 3.4** or newer
2. **Android SDK Tools 29** or newer
3. **Android Platform Version: API 29: Android 10  revision 6** or later
4. **Android targetSdkVersion: 29

<a name="installation"></a>
<a name="include_library_to_code_locally"></a>
### Include goSellSDK library as a dependency module in your project
---
1. Clone checkoutSDK library from Tap repository
   ```
       github.com/Tap-Payments/CheckoutSDK-Android.git
    ```
2. Add goSellSDK library to your project settings.gradle file as following
    ```java
        include ':library', ':YourAppName'
    ```
3. Setup your project to include checkout as a dependency Module.
   1. File -> Project Structure -> Modules -> << your project name >>
   2. Dependencies -> click on **+** icon in the screen bottom -> add Module Dependency
   3. select checkout library

<a name="installation_with_jitpack"></a>
### Installation with JitPack
---
[JitPack](https://jitpack.io/) is a novel package repository for JVM and Android projects. It builds Git projects on demand and provides you with ready-to-use artifacts (jar, aar).

To integrate goSellSDK into your project add it in your **root** `build.gradle` at the end of repositories:
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```java
	dependencies {
	        implementation 'com.github.Tap-Payments:CheckoutSDK-Android:Tag'
	}
```
<a name="setup"></a>
# Setup
---
First of all, `checkOutSDK` should be set up. In this section secret key and application ID are required.

<a name="setup_gosellsdk_class_properties"></a>
## checkOutSDK Class Properties
First of all, `checkOutSDK` should be set up. To set it up, add the following lines of code somewhere in your project and make sure they will be called before any usage of `checkOutSDK`.

Below is the list of properties in checkOutSDK class you can manipulate. Make sure you do the setup before any usage of the SDK.

<a name="setup_checkoutsdk_class_properties_secret_key"></a>
### Secret Key and Application ID

To set it up, add the following line of code somewhere in your project and make sure it will be called before any usage of `checkOutSDK`, otherwise an exception will be thrown. **Required**.

*Java:*
```java
 TapCheckOutSDK().init(this,"sk_test_kXXXXXXXXXXXXXXXXXXXXXXXX","sk_live_XXXXXXXXXXXXXXXXXXXXXXXX","app_id");
```
*Kotlin:*
```kotlin
    TapCheckOutSDK().init(this, "sk_test_kXXXXXXXXXXXXXXXXXXXXXXXX", "sk_live_XXXXXXXXXXXXXXXXXXXXXXXX", "app_id")
 ```    
1. **`authToken`** - to authorize your requests.// Secret key (format: "sk_XXXXXXXXXXXXXXXXXXXXXXXX")
2. **`app_id`** - replace it using your application ID "Application main package".

Don't forget to import the class at the beginning of the file:

*Java:*

```java
import company.tap.checkout.TapCheckOutSDK;
```
*Kotlin:*

```kotlin
import company.tap.checkout.TapCheckOutSDK
```
<a name="setup_checkoutsdk_class_properties_mode"></a>
### Mode

SDK mode defines which mode SDK is operating in, either **sandbox** or **production**.

SDK Mode is automatically identified in the backend based on the secrete key you defined earlier in setup process.
<a name="setup_checkoutsdk_class_properties_language"></a>

Localization language of the UI part of the SDK. This is locale identifier.

Make sure it consists only from 2 lowercased letters and is presented in the list of **availableLanguages** property of *checkout* class.
This gives you the choice to select/customize your locales
*Kotlin:*

```kotlin
      LocalizationManager.loadTapLocale(this, urlLocalisation)  //Load from URL / Dashboard
                      OR
        LocalizationManager.loadTapLocale(resources, R.raw.lang) //Load from resources local
```

**Notice:** SDK user interface layout direction is behave similar to your App. There is no effect come form the SDK back to your application locale.
<a name="setup_checkoutsdk_class_properties_theme"></a>

Theming of the UI is part of the SDK. 

Make sure to add atleast one by default dark or light theme to initialize the sdk.
Choose between *darktheme* and *lighttheme*
This gives you the choice to select/customize your themes
*Kotlin:*

```kotlin
      ThemeManager.loadTapTheme(this, urlStrDark)  //Load from URL / Dashboard
                      OR
    ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "darktheme") //Load from resources local
```
<a name="usage"></a>
#Usage
---
<a name="configure_sdk_with_required_data"></a>
### Configure SDK With Required Data

`checkOutSDK` should be set up. To set it up, add the following lines of code somewhere in your project and make sure they will be called before any usage of `checkOutSDK`.
*Kotlin:*
```kotlin
         /**
         * Integrating the SDK
         */
         
         
          /**
         * Required step.
         * Configure SDK with your choice of language preferences from the given list.
         */
         initializeLanguage()
        
         /**
         * Required step.
         * Configure SDK with your choice of theme preferences from the given list -dark or light.
         */
        initializeTheme()

        /**
         * Required step.
         * Configure SDK with your choice of language preferences from the given list.
         */   
            initializeSDK()
            
         /**
         * Required step.
         * Configure SDK Session with all required data.
         */
            configureSDKSession()
            
            
         /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
            initActionButton()
```
Below is the list of properties in checkOutSDK class you can manipulate. Make sure you do the setup before any usage of the SDK.

<a name="setup_checkoutsdk_class_properties_secret_key"></a>
### Configure SDK Secret Key and Application ID and SDK Language

To set it up, add the following line of code somewhere in your project and make sure it will be called before any usage of `checkOutSDK`, otherwise an exception will be thrown. **Required**.

*Kotlin:*
```kotlin
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        private fun initializeSDK(){
   TapCheckOutSDK().init(this, "sk_test_kXXXXXXXXXXXXXXXXXXXXXXXX", "sk_live_XXXXXXXXXXXXXXXXXXXXXXXX", "app_id")  // to be replaced by merchant, you can contact tap support team to get you credentials
        }
```
1. **`authToken`** - to authorize your requests.// Secret key (format: "sk_XXXXXXXXXXXXXXXXXXXXXXXX")
2. **`app_id`** - replace it using your application ID "Application main package".

Don't forget to import the class at the beginning of the file:

*Java:*

```java
import company.tap.checkout.TapCheckOutSDK;
```
*Kotlin:*

```koltin
import company.tap.checkout.TapCheckOutSDK
```
<a name="configure_sdk_look_and_feel"></a>
### Configure SDK Look and Feel
To customize the SDK look and feel you must use **ThemeManager**  as following:

*Kotlin:*
```kotlin
ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
```
or
```kotlin
 ThemeManager.loadTapTheme(this, urlString)
```
<a name="configure_sdk_Session"></a>
## Configure SDK Session
**SDKSession** is the main interface for checkOutSDK library from you application, so you can use it to start SDK with pay button or without pay button.
### Properties

<table style="text-align:center">
    <th colspan=1>Property</th>
    <th colspan=1>Type</th>
    <th rowspan=1>Description</th>

   <tr>
	<td> payButton </td>
	<td> PayButton </td>
	<td> Pay Button can be used to start SDK </td>
   <tr>

   <tr>
	<td> paymentDataSource  </td>
	<td> PaymentDataSource </td>
	<td> Payment data source. All input payment information is passed through this protocol. Required. </td>
   <tr>

   <tr>
	<td> activityListener  </td>
	<td> Activity </td>
	<td> Activity. used as a context to setup sdk.</td>
   <tr>

   <tr>
	<td> sessionDelegate  </td>
	<td> Activity </td>
	<td> Activity. it is used to notify Merchant application with all SDK Events </td>
   <tr>


</table>

### Methods

<table style="text-align:center">
    <th colspan=1>Property</th>
    <th colspan=1>Type</th>
    <tr>
	 <td> addSessionDelegate  </td>
	 <td> pass your activity that implements SessionDelegate interface . you have to override all methods available through this interface </td>
    </tr>
    <tr>
	 <td> instantiatePaymentDataSource  </td>
	 <td> Payment Data Source Object is the main object that is responsible of holding all data required from our backend to return all payment options [ Debit Cards - Credit Cards ] available for this merchant . </td>
    </tr>
    <tr>
   	 <td> setTransactionCurrency  </td>
   	 <td> Set the transaction currency associated to your account. Transaction currency must be of type TapCurrency("currency_iso_code"). i.e new TapCurrency("KWD") </td>
    </tr>
    <tr>
	 <td> setTransactionMode  </td>
	 <td> SDK offers different transaction modes such as [ TransactionMode.PURCHASE - TransactionMode.AUTHORIZE_CAPTURE - TransactionMode.SAVE_CARD - TransactionMode.TOKENIZE_CARD]   </td>
    </tr>
    <tr>
	 <td> setCustomer </td>
	 <td> Pass your customer data. Customer must be of type Tap Customer. You can create Tap Customer as following
	  TapCustomer(
                "cust_id", "cust_firstname", "cust_middlename",
                "cust_lastname", "cust_email",
                PhoneNumber("country_code", "MobileNo"), "metdata",
            )</td>
    </tr>
    <tr>
	 <td> setAmount </td>
	 <td> Set Total Amount. Amount value must be of type BigDecimal i.e new BigDecimal(40) </td>
    </tr>
    <tr>
	 <td> setPaymentItems </td>
	 <td> ArrayList that contains payment items. each item of this array must be of type PaymentItem. in case of SAVE_CARD or TOKENIZE_CARD you can pass it null</td>
    </tr>
    <tr>
  	 <td> setTaxes </td>
  	 <td> ArrayList that contains Tax items. each item of this array must be of type Tax. in case of SAVE_CARD or TOKENIZE_CARD you can pass it null</td>
  	</tr>
  	<tr>
  	 <td> setShipping </td>
  	 <td> ArrayList that contains Shipping items. each item of this array must be of type Shipping. in case of SAVE_CARD or TOKENIZE_CARD you can pass it null</td>
  	</tr>
  	<tr>
  	 <td> setPostURL </td>
  	 <td> POST URL. </td>
  	</tr>
  	<tr>
  	 <td> setPaymentDescription </td>
  	 <td> Payment description. </td>
  	</tr>
  	<tr>
  	 <td> setPaymentMetadata </td>
  	 <td> HashMap that contains any other payment related data. </td>
  	</tr>
  	<tr>
  	 <td> setPaymentReference </td>
  	 <td> Payment reference. it must be of type Reference object or null </td>
  	</tr>
  	<tr>
  	 <td> setPaymentStatementDescriptor </td>
  	 <td> Payment Statement Description </td>
  	</tr>
  	<tr>
  	 <td> isRequires3DSecure </td>
  	 <td> Enable or Disable 3D Secure </td>
  	</tr>
  	<tr>
  	 <td> setReceiptSettings </td>
  	 <td> Identify Receipt Settings. You must pass  Receipt object or null </td>
  	</tr>
  	<tr>
  	 <td> setAuthorizeAction </td>
  	 <td> Identify AuthorizeAction. You must pass AuthorizeAction object or null </td>
  	</tr>
  	<tr>
  	 <td> setDestination </td>
  	 <td> Identify Array of destination. You must pass Destinations object or null </td>
  	</tr>
  	<tr>
  	 <td> start </td>
  	 <td> Start SDK Without using Tap Pay button. You must call this method where ever you want to show TAP Payment screen. Also, you must pass your activity as a context   </td>
  	</tr>
  	<tr>
  	 <td> setButtonView </td>
  	 <td> If you included TAP PayButton in your activity then you need to configure it and then pass it to SDKSession through this method.</td>
    </tr>
    <tr>
      <td> setDefaultCardHolderName </td>
      <td> Sets default CardHoldername in the field, without the user need to re-type.(OPTIONAL) </td>
     </tr>
      <tr>
           <td>isUserAllowedToEnableCardHolderName </td>
           <td> Lets default CardHoldername in the field,can be editable or not based on user configuration .(OPTIONAL) </td>
          </tr>
      <tr>
                <td>sdkSession.cancelSession(this); </td>
                <td> Merchant can now cancel the session and stop all process initiating the SDK. .(OPTIONAL) </td>
               </tr>
               <tr>
                               <td>setTopUp </td>
                               <td> Merchant can now send a topUp object while initiating the SDK. .(OPTIONAL) </td>
                              </tr>
</table>
**Configure SDK Session Example** 

*Kotlin:*
```kotlin
 // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this) //** Required **
        
        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource() //** Required **


        // set transaction currency associated to your account

        sdkSession.setTransactionCurrency(TapCurrency("KWD")) //** Required **


        // Using static CustomerBuilder method available inside TAP TapCustomer Class you can populate TAP TapCustomer object and pass it to SDK
        sdkSession.setCustomer(setCustomer()) //** Required **


        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping

        settingsManager?.getString("key_amount_name", "1")?.let { BigDecimal(it) }?.let {
            sdkSession.setAmount(
                    it
            )
        }//** Required **


        // Set Payment Items array list
        sdkSession.setPaymentItems(getPaymentItems()) // ** Optional ** you can pass empty array list


        sdkSession.setPaymentType("ALL")  //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list


        // Set Shipping array list
          sdkSession.setShipping(settingsManager?.getShippingList()) // ** Optional ** you can pass empty array list

        // Post URL
        sdkSession.setPostURL("") // ** Optional **


        // Payment Description
        sdkSession.setPaymentDescription("") //** Optional **

        // Payment Extra Info
        sdkSession.setPaymentMetadata(HashMap()) // ** Optional ** you can pass empty array hash map

        // Payment Reference
        sdkSession.setPaymentReference(null) // ** Optional ** you can pass null

        // Payment Statement Descriptor
        sdkSession.setPaymentStatementDescriptor("") // ** Optional **


        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean


        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(
                Receipt(
                        false,
                        false
                )
        ) // ** Optional ** you can pass Receipt object or null


        // Set Authorize Action
        sdkSession.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null


      //  sdkSession.setDestination(settingsManager?.getDestination()) // ** Optional ** you can pass Destinations object or null
       sdkSession.setDestination(null) // ** Optional ** you can pass Destinations object or null


        sdkSession.setMerchantID("xxxxx") // ** Optional ** you can pass merchant id or null


        sdkSession.setCardType(CardType.ALL) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

         settingsManager?.getTransactionsMode("key_sdk_transaction_mode")?.let {
            sdkSession.setTransactionMode(
                    it
            )
        }

        sdkSession.setDefaultCardHolderName("TEST TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
        sdkSession.isUserAllowedToEnableCardHolderName(false) // ** Optional ** you can enable/ disable  default CardHolderName .
        sdkSession.setSdkMode(SdkMode.SAND_BOX) //** Pass your SDK MODE
 ```
 <a name="configure_sdk_transaction_mode"></a>
  **Configure SDK Transaction Mode**
  
  You have to choose only one Mode of the following modes:
  
   **Note:-**
     - In case of using PayButton, then don't call  sdkSession.startSDK(fragmentManager,this,this) because the SDK will start when user clicks the tap pay button.

<a name="init_pay_button"></a>
If you included Tap Pay Button then configure it first, if not then ignore this step.

**Use Tap PayButton**

*Kotlin:*
  ```kotlin
        /**
          * Include pay button in merchant page
          */
            private fun initActionButton() {
        payButton.setButtonDataSource(
                true,
                this.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )

        sdkSession.setButtonView(payButton, this, supportFragmentManager, this)


    }
```
**To populate TAP Customer object**

*Kotlin*:
```kotlin
   fun setCustomer(): TapCustomer { 
        val tapCustomer: TapCustomer? = null
        return TapCustomer(
                "cus_TSxxxxxxxxxxxxx", "firstname", "middlename",
                "lastname", "abcd@gmail.com",
                PhoneNumber("00965", "xxxxxxxx"), "description-metadata",
        )

    }
 ```	
<a name="sdkSession_data_source"></a>
### SDKSession Payment Data Source

**PaymentDataSource** is an interface which you should implement somewhere in your code to pass payment information in order to be able to access payment flow within the SDK.

<a name="session_data_source_structure"></a>
### Structure

The following table describes its structure and specifies which fields are required for each of the modes.

<table style="text-align:center">
    <th rowspan=2>Member</th>
    <th colspan=1>Type</th>
    <th colspan=3>Required</th>
    <th rowspan=2>Description</th>
    <tr>
        <th><sub>Android</sub></th>
        <th><sub>Purchase</sub></th>
        <th><sub>Authorize</sub></th>
        <th><sub>Card Saving</sub></th>
    </tr>
    <tr>
        <td><sub><i>mode</i></sub></td>
        <td colspan=1><sub><b>TransactionMode</b></sub></td>
        <td colspan=3><sub><i>false</i></sub></td>
        <td align="left"><sub>Mode of the transactions (purchase or authorize). If this    property is not implemented, <i>purchase</i> mode is used.</sub></td>
    </tr>
     <tr>
        <td><sub><i>customer</i></sub></td>
        <td colspan=1><sub><b>Customer</b></sub></td>
        <td colspan=3><sub><i>true</i></sub></td>
        <td align="left"><sub>Customer information. For more details on how to create the customer, please refer to <i>Customer</i> class reference.</sub></td>
    </tr>
     <tr>
        <td><sub><i>currency</i></sub></td>
        <td colspan=1><sub><b>Currency</b></sub></td>
        <td colspan=2><sub><i>true</i></sub></td><td><sub><i>false</i></sub></td>
        <td align="left"><sub>Currency of the transaction.</sub></td>
    </tr>
    <tr>
        <td><sub><i>amount</i></sub></td>
        <td colspan=1><sub><b><nobr>BigDecimal</nobr></b></sub></td>
        <td colspan=3><sub><i>false</i></sub></td>
        <td align="left"><sub>Payment/Authorization amount.<br><b>Note:</b> In order to have payment amount either <i>amount</i> or <i>items</i> should be implemented. If both are implemented, <i>items</i> is preferred.</sub></td>
    </tr>
    <tr>
    <td><sub><i>items</i></sub></td>
    <td colspan=1><sub><b>ArrayList <nobr>[PaymentItem]</PaymentItem> </nobr></b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>List of items to pay for.<br><b>Note:</b> In order to have payment amount either <i>amount</i> or <i>items</i> should be implemented. If both are implemented, <i>items</i> is preferred.</sub></td>
</tr>
<tr>
    <td><sub><i>taxes</i></sub></td>
    <td colspan=1><sub><b>ArrayList <nobr>[Tax]</PaymentItem> </nobr></b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>You can specify taxation details here. By default, there are no taxes.<br> <b>Note:</b> Specifying taxes will affect total payment/authorization amount.</sub></td>
</tr>
<tr>
    <td><sub><i>shipping</i></sub></td>
    <td colspan=1><sub><b>ArrayList <nobr>[Shipping]</PaymentItem> </nobr></b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>You can specify shipping details here. By default, there are no shipping details.<br> <b>Note:</b> Specifying shipping will affect total payment/authorization amount.</sub></td>
</tr>
<tr>
    <td><sub><i>postURL</i></sub></td>
    <td colspan=1><sub><b>String </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>The URL which will be called by Tap system notifying that payment has either succeed or failed</sub></td>
</tr>
<tr>
    <td><sub><i>paymentDescription</i></sub></td>
    <td colspan=1><sub><b>String </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>Description of the payment.</sub></td>
</tr>
<tr>
    <td><sub><i>paymentMetadata</i></sub></td>
    <td colspan=1><sub><b>String </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>Additional information you would like to pass along with the transaction.</sub></td>
</tr>
<tr>
    <td><sub><i>paymentReference</i></sub></td>
    <td colspan=1><sub><b>Reference </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>You can keep a reference to the transaction using this property</sub></td>
</tr>
<tr>
    <td><sub><i>paymentStatementDescriptor</i></sub></td>
    <td colspan=1><sub><b>String </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>Statement descriptor.</sub></td>
</tr>
<tr>
    <td><sub><i>require3DSecure</i></sub></td>
    <td colspan=1><sub><b>Boolean </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>Defines if 3D secure check is required. If not implemented, treated as <i>true</i>.<br><b>Note:</b> If you disable 3D secure check, it still may occure. Final decision is taken by Tap</sub></td>
</tr>
<tr>
    <td><sub><i>receiptSettings</i></sub></td>
    <td colspan=1><sub><b>Receipt </b></sub></td>
    <td colspan=3><sub><i>false</i></sub></td>
    <td align="left"><sub>Receipt recipient details.</sub></td>
</tr>
<tr>
    <td><sub><i>authorizeAction</i></sub></td>
    <td colspan=1><sub><b>AuthorizeAction </b></sub></td>
    <td><sub><i>false</i></sub></td><td><sub><i>true</i></sub></td><td><sub><i>false</i></sub></td>
    <td align="left"><sub>Action to perform after authorization succeeds.</sub></td>
</tr>

</table>

<a name="sdk_open_interfaces"></a>
## SDK Open Interfaces
 SDK open Interfaces available for implementation through Merchant Project:

1. SessionDelegate
```kotlin
fun paymentSucceed(charge: Charge)
    fun paymentFailed(charge: Charge?)

    fun authorizationSucceed(authorize: Authorize)
    fun authorizationFailed(authorize: Authorize?)


    fun cardSaved(charge: Charge)
    fun cardSavingFailed(charge: Charge)

    fun cardTokenizedSuccessfully(token: Token)

    fun savedCardsList(cardsList: CardsList)

    fun sdkError(goSellError: GoSellError?)

    fun sessionIsStarting()
    fun sessionHasStarted()
    fun sessionCancelled()
    fun sessionFailedToStart()

    fun invalidCardDetails()

    fun backendUnknownError(message: GoSellError?)

    fun invalidTransactionMode()

    fun invalidCustomerID()

    fun userEnabledSaveCardOption(saveCardEnabled: Boolean)

    fun getStatusSDK(response :String ? ,charge: Charge?)
```
2. PaymentDataSource
```kotlin
interface PaymentDataSource {
    /**
     * Transaction currency. @return the currency
     */
    fun getCurrency(): TapCurrency?

    /**
     * TapCustomer. @return the customer
     */
    fun getCustomer(): TapCustomer?

    /**
     * Amount. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the amount
     */
    fun getAmount(): BigDecimal?

    /**
     * List of items to pay for. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the items
     */
    fun getItems(): ArrayList<PaymentItem>?

    /**
     * Transaction mode. If you return null in this method, it will be treated as PURCHASE. @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode?

    /**
     * List of taxes. Optional. Note: specifying taxes will affect total payment amount. @return the taxes
     */
    fun getTaxes(): ArrayList<Tax>?

    /**
     * Shipping list. Optional. Note: specifying shipping will affect total payment amount. @return the shipping
     */
    fun getShipping(): ArrayList<Shipping>?

    /**
     * Tap will post to this URL after transaction finishes. Optional. @return the post url
     */
    fun getPostURL(): String?

    /**
     * Description of the payment. @return the payment description
     */
    fun getPaymentDescription(): String?

    /**
     * If you would like to pass additional information with the payment, pass it here. @return the payment metadata
     */
    fun getPaymentMetadata(): HashMap<String, String>?

    /**
     * Payment reference. Implement this property to keep a reference to the transaction on your backend. @return the payment reference
     */
    fun getPaymentReference(): Reference?

    /**
     * Payment statement descriptor. @return the payment statement descriptor
     */
    fun getPaymentStatementDescriptor(): String?

    /**
     * Defines if user allowed to save card. @return the allowUserToSaveCard
     * @return
     */
    fun getAllowedToSaveCard(): Boolean

    /**
     * Defines if 3D secure check is required. @return the requires 3 d secure
     */
    fun getRequires3DSecure(): Boolean

    /**
     * Receipt dispatch settings. @return the receipt settings
     */
    fun getReceiptSettings(): Receipt?

    /**
     * Action to perform after authorization succeeds. Used only if transaction mode is AUTHORIZE_CAPTURE. @return the authorize action
     */
    fun getAuthorizeAction(): AuthorizeAction?

    /**
     * The Destination array contains list of Merchant desired destinations accounts to receive money from payment transactions
     */
    fun getDestination(): Destinations?

    fun getMerchant(): Merchant?
    fun getPaymentDataType(): String?

    /**
     * Defines if user wants all cards or specific card types.
     */
    fun getCardType(): CardType?

    /**
     * Defines the default cardHolderName. Optional. @return the default CardHolderName
     */
    fun getDefaultCardHolderName(): String?

    /**
     * Defines if user allowed to edit the cardHolderName. @return the enableEditCardHolderName
     * @return
     */
    fun getEnableEditCardHolderName(): Boolean

    /**
     * Defines the cardIssuer details. Optional. @return the default CardIssuer
     */
    fun getCardIssuer(): CardIssuer?

    fun getTopup(): TopUp?

    fun getSelectedCurrency():String?

    fun getSelectedAmount():BigDecimal?

    fun getPaymentOptionsResponse():PaymentOptionsResponse?

    fun getMerchantData(): MerchantData?

    fun getBinLookupResponse() : BINLookupResponse?

    /**
     * Defines the SDK mode . Optional. @return the default Sandbox
     */
    fun getSDKMode(): SdkMode?
     /**
     * Defines the Payment options. Optional.
     */
    fun getAvailablePaymentOptionsCardBrands(): ArrayList<PaymentOption>?
    /**
     * Defines the TokenConfig for header
     */
    fun getTokenConfig(): String?

    /**
     * Defines the AuthKeys.
     */
    fun getAuthKeys(): String?

    fun getInitOptionsResponse():InitResponseModel?
}
```
<a name="sdk_open_enums"></a>
## SDK Open ENUMs
SDK open Enums available for implementation through Merchant Project:
