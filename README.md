# CheckoutSDK-Android

[Tap payments](https://www.tap.company "Tap payments") provides for you a one drop solution to enable a seamless checkout process in your android app flow.

[![Platform](https://img.shields.io/badge/platform-Android-inactive.svg?style=flat)](https://github.com/Tap-Payments/CheckoutSDK-Android/)
[![Documentation](https://img.shields.io/badge/documentation-100%25-bright%20green.svg)](https://github.com/Tap-Payments/CheckoutSDK-Android/)
[![SDK Version](https://img.shields.io/badge/minSdkVersion-21-blue.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)
[![SDK Version](https://img.shields.io/badge/targetSdkVersion-30-informational.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)

**Access all local, regional, & global payment methods**

Your customers can scale globally by offering all popular payment methods across MENA, whether that's mada, KNET, Fawry, Apple Pay, Visa, Mastercard, Amex, Tabby, and many more.
![](https://files.readme.io/4dc0d3e-payment-methods-tap-transparent.png)

**Look at it!**

# Table of Contents
---
1. [Requirements](#requirements)
2. [Installation with jitpack](#installation_with_jitpack)

3. [PRE SDK Setup](#setup)
    1. [Important Information](#setup_important_information)
   
4. [SDK SETUP](#sdk_setup)
    1. [Configure SDK Session](#configure_sdk_Session)
    2. [Configure SDK Transaction Mode](#configure_sdk_transaction_mode)
    3. [Use Tap PayButton](#init_pay_button)
    4. [List Saved Cards](#list_saved_cards)
    5. [Open SDK Interfaces](#sdk_open_interfaces)
    6. [Open SDK ENUMs](#sdk_open_enums)
    7. [Open SDK Models](#sdk_open_models)
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
# PRE SDK SETUP
---
> If you already have a Tap account, please skip this part.

In order to be able to use the SDK, you have to create a Tap account first. Once you finish your account with our [Integration team](https://www.tap.company "Integration team"), please make sure they provided you with the following:
1. Your sandbox public key.
   1. This will be used to perform testing transactions in our sandbox environment. Will be useful for you in your development phase.
2. Your production public key.
   1. This will be used to perform actual transactions in our production environment. Will be required for you before releasing your application.
3. Your tap merchant id.
   1. This will be used as an identifier for the app entity under your Tap account. As you can have multiple apps/websites integrated with Tap under your same Tap account.
   

<a name="setup_important_information"></a>
## Important Information

### Supported languages
1. English.
1. Arabic.

### Supported themes
> The sdk auto detects the device's display mode light or dark and displays itself accordingly.

1. Light mode.
   1. The `Checkout SDK` will be displayed in light mode and icons will be colored.
1. Light mode with mono color.
   1. The `Checkout SDK` will be displayed in light mode and icons will be monochromatic.
1. Dark mode.
   1. The `Checkout SDK` will be displayed in dark mode and icons will white.
1. Dark mode with colors.
   1. The `Checkout SDK` will be displayed in dark mode and icons will be colored.

### Supported SDK modes
1. Sandbox
   1. Where you can try all different payment methods with no limits and no charges will occur.
   2. Check this for our [Testing Cards](https://developers.tap.company/reference/testing-cards "Testing Cards")
1. Production
   1. Where you can try all different payment methods for real to memic your expected customers' experience. Please note, this will require using real data will incur charges.

### Supported transaction modes
1. Purchase
   1. To be used when you want to deduct the amount from your customer.
1. Authourize
   1. To be used when you want to hold the amount from your customer.

<a name="sdk_setup"></a>
# SDK SETUP
---
### Global variables setup
These variables to be set before starting the `Checkout SDK`. This will define important parameters for configuring and theming the `Checkout SDK` itself. Please note, missing to configure these will end up in using default values or the `Checkout SDK` will throw an error as it cannot start.

1. The required localisation.
   1. By this you define which language you want the `Checkout SDK` appears with.
   2. Now we do support: `en` & `ar`.
   3. Default value if not set is : `en`.
      ```kotlin
       LocalizationManager.setLocale(this, Locale("en"))
      ```
   4. If you wish to have your custom locale it can be added as below:
    ```kotlin
         LocalizationManager.loadTapLocale(resources, R.raw.defaulttaplocalisation)
      ```
2. The required Tap keys.
   1. By this, you define your Tap keys so the sdk can identify you as a merchant.
   1. These are required.
   1. How to set it:    
      To set it up, add the following line of code somewhere in your project and make sure it will be called before any usage of `checkOutSDK`, otherwise an exception will be thrown. **Required**.
      
```kotlin
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        private fun initializeSDK(){
       TapCheckOutSDK().init(this, "pk_kXXXXXXXXXXXXXXXXXXXXXXXX", "pk_XXXXXXXXXXXXXXXXXXXXXXXX", "app_id")  // to be replaced by merchant, you can contact tap support team to get you credentials
        }
```
1. **`authToken`** - to authorize your requests.// Secret key (format: "pk_XXXXXXXXXXXXXXXXXXXXXXXX")
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

3. Optional theme variables:
   1. Display mono variant when showing the Light mode theme
      1. If not passed default value is `false`
      2. How to set it : `TapCheckout.displayMonoLight = false`
   2. Display colorized variant when showing the Dark mode theme
      1. If not passed default value is `false`
      2. How to set it : `TapCheckout.displayColoredDark = false`
   3. If you wish to setup your custom theme , you can do it as below :
*Kotlin:*
```kotlin
ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
```
or
```kotlin
 ThemeManager.loadTapTheme(this, urlString)
```
   
      
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
         * Configure SDK Session with all required data.
         */
            configureSDKSession()
            
            
         /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
            initActionButton()
```
Below is the list of properties in checkOutSDK class you can manipulate. Make sure you do the setup before any usage of the SDK.



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
	<td> checkOutDelegate  </td>
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
	 <td> pass your activity that implements CheckOutDelegate interface . you have to override all methods available through this interface </td>
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
  PURCHASE , AUTHORIZE_CAPTURE
  
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

1. CheckOutDelegate

```kotlin

    fun checkoutChargeCaptured(charge: Charge)
    fun checkoutChargeFailed(charge: Charge?)

    fun checkoutAuthorizeCaptured(authorize: Authorize)
     fun checkoutAuthorizeFailed(authorize: Authorize?)


     fun cardSaved(charge: Charge)
     fun cardSavingFailed(charge: Charge)

     fun cardTokenizedSuccessfully(token: Token , saveCard:Boolean)

     fun savedCardsList(cardsList: CardsList)

     fun checkoutSdkError(goSellError: GoSellError?)

     fun sessionIsStarting()
     fun sessionHasStarted()
     fun sessionCancelled()
     fun sessionFailedToStart()

     fun invalidCardDetails()
     fun backendUnknownError(message: GoSellError?)
     fun invalidTransactionMode()
     fun invalidCustomerID()
     fun userEnabledSaveCardOption(saveCardEnabled: Boolean)
     fun  asyncPaymentStarted(charge:Charge)

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
1. TransactionMode 
```kotlin
enum class TransactionMode {
    PURCHASE,
    AUTHORIZE_CAPTURE

}
```

2.SdkMode
```kotlin
enum class SdkMode {
/**
* Sandbox is for testing purposes
*/
@SerializedName("Sandbox")
SAND_BOX,
/**
* Production is for live
*/
@SerializedName("Production")
PRODUCTION
}
```
3. SdkIdentifier
```kotlin
enum class SdkIdentifier {
   /* react-native
    */
    @SerializedName("react-native")
    ReactNative,
    /**
     * Flutter
     */
    @SerializedName("Flutter")
    Flutter,

    /**
     * Native
     */
    @SerializedName("Native")
    Native

}
```
4.CardType
```kotlin
/**
 * The Merchant can set different cardTypes CREDIT ,DEBIT , ALL to allow user his choice*/
enum class CardType {

    CREDIT,
    DEBIT,
    ALL
}
```
<a name="sdk_open_models"></a>
## SDK Open Models
SDK open Models available for implementation through Merchant Project: 
1. TapCustomer
```kotlin
class TapCustomer(
    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    @field:Expose @field:SerializedName("id") val identifier: String?,
    /**
     * Gets first name.
     *
     * @return the first name
     */
    @field:Expose @field:SerializedName("first_name") var firstName: String?,
    middleName: String?,
    lastName: String?,
    email: String?,
    phone: PhoneNumber?,
    metaData: String?
) :
    Serializable {

    /**
     * Gets middle name.
     *
     * @return the middle name
     */
    @SerializedName("middle_name")
    @Expose
    val middleName: String?

    /**
     * Gets last name.
     *
     * @return the last name
     */
    @SerializedName("last_name")
    @Expose
    val lastName: String?

    /**
     * Gets email.
     *
     * @return the email
     */
    @SerializedName("email")
    @Expose
    val email: String?

    @SerializedName("phone")
    @Expose
    private val phone: PhoneNumber?
    /**
     * Gets meta data.
     *
     * @return the meta data
     */
    /**
     * The Meta data.
     */
    @SerializedName("metadata")
    var metaData: String?

    /**
     * Gets phone.
     *
     * @return the phone
     */
    fun getPhone(): PhoneNumber? {
        return phone
    }

    override fun toString(): String {
        return """TapCustomer {
        id =  '$identifier'
        email =  '$email'
        first_name =  '$firstName'
        middle_name =  '$middleName'
        last_name =  '$lastName'
        phone  country code =  '${phone?.countryCode}'
        phone number =  '${phone?.number}'
        metadata =  '$metaData'
    }"""
    }
    ////////////////////////// ############################ Start of Builder Region ########################### ///////////////////////
    /**
     * The type TapCustomer builder.
     */
    class CustomerBuilder
    /**
     * Client app can create a customer object with only customer id
     *
     * @param innerId the inner id
     */(private val nestedIdentifier: String) {
        private var nestedFirstName: String? = null
        private var nestedMiddleName: String? = null
        private var nestedLastName: String? = null
        private var nestedEmail: String? = null
        private var nestedPhone: PhoneNumber? = null
        private var nestedMetaData: String? = null

        /**
         * First name customer builder.
         *
         * @param innerFirstName the inner first name
         * @return the customer builder
         */
        fun firstName(innerFirstName: String?): CustomerBuilder {
            nestedFirstName = innerFirstName
            return this
        }

        /**
         * Middle name customer builder.
         *
         * @param innerMiddle the inner middle
         * @return the customer builder
         */
        fun middleName(innerMiddle: String?): CustomerBuilder {
            nestedMiddleName = innerMiddle
            return this
        }

        /**
         * Last name customer builder.
         *
         * @param innerLastName the inner last name
         * @return the customer builder
         */
        fun lastName(innerLastName: String?): CustomerBuilder {
            nestedLastName = innerLastName
            return this
        }

        /**
         * Email customer builder.
         *
         * @param innerEmail the inner email
         * @return the customer builder
         */
        fun email(innerEmail: String?): CustomerBuilder {
            nestedEmail = innerEmail
            return this
        }

        /**
         * Phone customer builder.
         *
         * @param innerPhone the inner phone
         * @return the customer builder
         */
        fun phone(innerPhone: PhoneNumber?): CustomerBuilder {
            nestedPhone = innerPhone
            return this
        }

        /**
         * Metadata customer builder.
         *
         * @param innerMetadata the inner metadata
         * @return the customer builder
         */
        fun metadata(innerMetadata: String?): CustomerBuilder {
            nestedMetaData = innerMetadata
            return this
        }

        /**
         * Build customer.
         *
         * @return the customer
         */
        fun build(): TapCustomer {
            return TapCustomer(
                nestedIdentifier, nestedFirstName, nestedMiddleName, nestedLastName,
                nestedEmail, nestedPhone, nestedMetaData
            )
        }
    } ////////////////////////// ############################ End of Builder Region ########################### ///////////////////////

    //  Constructor is private to prevent access from client app, it must be through inner Builder class only
    init {
        firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.email = email
        this.phone = phone
        this.metaData = metaData
    }
}
```
2.TapCurrency
```kotlin
class TapCurrency(isoCode: String) {
    /**
     * Gets iso code.
     *
     * @return the iso code
     */
    val isoCode: String

    /**
     * Instantiates a new Tap currency.
     *
     * @param isoCode the iso code
     * @throws CurrencyException the currency exception
     */
    init {
        if (isoCode.isEmpty()) {
            this.isoCode = isoCode
        } else {
            val code = isoCode.toLowerCase()
            if (!LocaleCurrencies.checkUserCurrency(code)) {
                throw CurrencyException.getUnknown(code)
            }
            this.isoCode = code
        }
    }
}
```
3.AuthorizeAction
```kotlin
class AuthorizeAction(void: AuthorizeActionType, i: Int) {
    @SerializedName("type")
    @Expose
    private var type: AuthorizeActionType? = AuthorizeActionType.VOID

    @SerializedName("time")
    @Expose
    private var timeInHours = 168

    /**
     * Gets default.
     *
     * @return the default
     */
   open fun getDefault() {
        return AuthorizeAction(AuthorizeActionType.VOID, 168)
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    fun getType(): AuthorizeActionType? {
        return type
    }

    /**
     * Gets time in hours.
     *
     * @return the time in hours
     */
    fun getTimeInHours(): Int {
        return timeInHours
    }

    /**
     * Instantiates a new Authorize action.
     *
     * @param type        the type
     * @param timeInHours the time in hours
     */
    fun AuthorizeAction(type: AuthorizeActionType?, timeInHours: Int) {
        this.type = type
        this.timeInHours = timeInHours
    }
}
```
4. Destination
```kotlin
data class Destination(
    // Destination unique identifier (Required)
    @SerializedName("id") @Expose
    private var id: String? = null,

    // Amount to be transferred to the destination account (Required)
    @SerializedName("amount")
    @Expose
    private val amount: BigDecimal? = null,

    // Currency code (three digit ISO format) (Required)
    @SerializedName("currency")
    @Expose
    private val currency: String? = null,

    //Description about the transfer (Optional)
    @SerializedName("description")
    @Expose
    private val description: String? = null,

    //Merchant reference number to the destination (Optional)
    @SerializedName("reference")
    @Expose
    private val reference: String? = null
)
```
5.Destinations
```kotlin
data class Destinations(

    @SerializedName("amount") @Expose
    private var amount: BigDecimal? = null,

    @SerializedName("currency")
    @Expose
    private val currency: String? = null,

    @SerializedName("count")
    @Expose
    private val count: Int = 0,

    @SerializedName("destination")
    @Expose
    private val destination: ArrayList<Destination>? = null
)
```
6.PaymentItem
```kotlin
class PaymentItem(name: String,
                  @Nullable description: String,
                  quantity: Quantity,
                  amountPerUnit: BigDecimal,
                  @Nullable discount: AmountModificator?,
                  @Nullable taxes: ArrayList<Tax>?){
    @SerializedName("name")
    @Expose
    var name: String? = null


    @SerializedName("description")
    @Expose
     var description: String? = null

    @SerializedName("quantity")
    @Expose
     var quantity: Quantity? = null

    @SerializedName("amount_per_unit")
    @Expose
     var amountPerUnit: BigDecimal? = null

    @SerializedName("discount")
    @Expose
     var discount: AmountModificator? = null

    @SerializedName("taxes")
    @Expose
     var taxes: ArrayList<Tax>? = null

    @SerializedName("total_amount")
    @Expose
     var totalAmount: BigDecimal? = null


    init {
        this.name = name
        this.description = description
        this.quantity = quantity
        this.amountPerUnit = amountPerUnit
        this.discount = discount
        this.taxes = taxes
        totalAmount = AmountCalculator.calculateTotalAmountOf(listOf(this),null,null)

        println("calculated total amount : " + totalAmount)
    }

    /**
     * Gets amount per unit.
     *
     * @return the amount per unit
     */
    @JvmName("getAmountPerUnit1")
    fun getAmountPerUnit(): BigDecimal? {
        return amountPerUnit
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    @JvmName("getQuantity1")
    fun getQuantity(): Quantity? {
        return quantity
    }

    /**
     * Gets discount.
     *
     * @return the discount
     */
    @JvmName("getDiscount1")
    fun getDiscount(): AmountModificator? {
        return discount
    }

    /**
     * Gets plain amount.
     *
     * @return the plain amount
     */
    fun getPlainAmount(): BigDecimal {
        println("  #### getPlainAmount : " + getAmountPerUnit())
        System.out.println("  #### this.getQuantity().getValue() : " + getQuantity()?.value)
        println("  #### result : " + getAmountPerUnit()!!.multiply(getQuantity()?.value))
        return getAmountPerUnit()!!.multiply(getQuantity()?.value)
    }

    /**
     * Gets discount amount.
     *
     * @return the discount amount
     */
    fun getDiscountAmount(): BigDecimal? {
        return if (getDiscount() == null) {
            BigDecimal.ZERO
        } else when (getDiscount()!!.getType()) {
            AmountModificatorType.PERCENTAGE -> getPlainAmount().multiply(getDiscount()!!.getNormalizedValue())
            AmountModificatorType.FIXED -> getDiscount()?.getValue()
            else -> BigDecimal.ZERO
        }
    }

    /**
     * Gets taxes amount.
     *
     * @return the taxes amount
     */
    fun getTaxesAmount(): BigDecimal? {
        val taxationAmount = getPlainAmount().subtract(getDiscountAmount())
        return AmountCalculator.calculateTaxesOn(taxationAmount, taxes)
    }


    class PaymentItemBuilder
    /**
     * public constructor with only required data
     *
     * @param name          the name
     * @param quantity      the quantity
     * @param amountPerUnit the amount per unit
     */(private var nestedName: String,
        private var nestedQuantity: Quantity,
        private var nestedAmountPerUnit: BigDecimal) {
        private var nestedDescription: String? = null
        private var nestedDiscount: AmountModificator? = null
        private var nestedTaxes: ArrayList<Tax>? = null
        private var nestedTotalAmount: BigDecimal? = null

        /**
         * Description payment item builder.
         *
         * @param innerDescription the inner description
         * @return the payment item builder
         */
        fun description(innerDescription: String?): PaymentItemBuilder {
            nestedDescription = innerDescription
            return this
        }

        /**
         * Discount payment item builder.
         *
         * @param innerDiscount the inner discount
         * @return the payment item builder
         */
        fun discount(innerDiscount: AmountModificator?): PaymentItemBuilder {
            nestedDiscount = innerDiscount
            return this
        }

        /**
         * Taxes payment item builder.
         *
         * @param innerTaxes the inner taxes
         * @return the payment item builder
         */
        fun taxes(innerTaxes: ArrayList<Tax>?): PaymentItemBuilder {
            nestedTaxes = innerTaxes
            return this
        }

        /**
         * Total amount payment item builder.
         *
         * @param innerTotalAmount the inner total amount
         * @return the payment item builder
         */
        fun totalAmount(innerTotalAmount: BigDecimal?): PaymentItemBuilder {
            nestedTotalAmount = innerTotalAmount
            return this
        }

        /**
         * Build payment item.
         *
         * @return the payment item
         */
        fun build(): PaymentItem {
            return PaymentItem(nestedName, nestedDescription!!, nestedQuantity, nestedAmountPerUnit,
                    nestedDiscount, nestedTaxes)
        }
    }


}
```
7. Receipt
```kotlin
data class Receipt(
    @SerializedName("id") @Expose
    var id: Boolean,

    @SerializedName("email")
    @Expose
    val email: Boolean = false,

    @SerializedName("sms")
    @Expose
    private val sms: Boolean = false
) : Serializable
```
8.Reference
```kotlin
data class Reference(
    @SerializedName("acquirer") @Expose
    private var acquirer: String? = null,

    @SerializedName("gateway")
    @Expose
    private val gateway: String? = null,

    @SerializedName("payment")
    @Expose
    private val payment: String? = null,

    @SerializedName("track")
    @Expose
    private val track: String? = null,

    @SerializedName("transaction")
    @Expose
    private val transaction: String? = null,

    @SerializedName("order")
    @Expose
    private val order: String? = null,

    @SerializedName("gosell_id")
    @Expose
    private val gosell_id: String? = null
) : Serializable
```
8. Shipping
```kotlin
data class Shipping(
    @SerializedName("name") private var name: String,
    @SerializedName("description") @Nullable val description: String,
    @SerializedName("amount") val amount: BigDecimal
)
```
9.Tax
```kotlin
data class Tax(
    @SerializedName("name") var name: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: AmountModificator
)
```
10.TopUp
```kotlin
data class TopUp(
    @SerializedName("id") @Expose
    var Id: String? = null,
    @SerializedName("wallet_id")
    @Expose
    var walletId: String? = null,

    @SerializedName("created")
    @Expose
    var created: Long? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("amount")
    @Expose
    var amount: BigDecimal? = null,

    @SerializedName("currency")
    @Expose
    var currency: String? = null,

    @SerializedName("charge")
    @Expose
    var charge: TopchargeModel? = null,

    @SerializedName("customer")
    @Expose
    var customer: TopCustomerModel? = null,

    @SerializedName("reference")
    @Expose
    var topUpReference: TopUpReference? = null,

    @SerializedName("application")
    @Expose
    var application: TopUpApplication? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("post")
    @Expose
    var post: TopupPost? = null,

    @SerializedName("metadata")
    @Expose
    var metadata: MetaData? = null
) : Serializable

data class TopchargeModel(
    @SerializedName("id") @Expose
    private var id: String? = null
) : Serializable

data class TopCustomerModel(
    @SerializedName("id") @Expose
    private var id: String? = null
) : Serializable

data class TopUpApplication(
    @SerializedName("amount") @Expose
    var amount: BigDecimal? = null,
    @SerializedName("currency") @Expose
    var currency: String? = null
) : Serializable

data class TopUpReference(
    @SerializedName("order") @Expose
    var order: String? = null,
    @SerializedName("transaction")
    @Expose
    val transaction: String? = null
) : Serializable

data class TopupPost(
    @SerializedName("url") @Expose
    val url: String? = null
) : Serializable

data class MetaData(
    @SerializedName("udf1") @Expose
    private var udf1: String? = null,
    @SerializedName("udf2")
    @Expose
    private var udf2: String? = null
) : Serializable
```
11. CardsList
```kotlin
class CardsList(
    responseCode: Int,
    `object`: String,
    has_more: Boolean,
    data: ArrayList<SavedCard>?
) {
    private val responseCode: Int = responseCode
    private val `object`: String
    private val has_more: Boolean
    private var cards: ArrayList<SavedCard>?

    /**
     * Gets Response Code
     * @return responseCode
     */
    fun getResponseCode(): Int {
        return responseCode
    }

    /**
     * Gets Object type
     * @return object
     */
    fun getObject(): String {
        return `object`
    }

    /**
     * Check if customer has more cards
     * @return has_more
     */
    fun isHas_more(): Boolean {
        return has_more
    }

    /**
     * Gets cards.
     *
     * @return the cards
     */
    fun getCards(): ArrayList<SavedCard>? {
        if (cards == null) {
            cards = ArrayList<SavedCard>()
        }
        return cards
    }

    init {
        this.`object` = `object`
        this.has_more = has_more
        cards = data
    }
}
````
<a name="sdk_delegate"></a>
## SDKSession Delegate

**CheckOutDelegate** is an interface which you may want to implement to receive payment/authorization/card saving status updates and update your user interface accordingly when payment window closes.
Below are listed down all available callbacks:

<a name="payment_success_callback"></a>
### checkout Success Callback

Notifies the receiver that payment has succeed.

#### Declaration
*Kotlin:*
```kotlin
- fun checkoutChargeCaptured(charge: Charge)
```
#### Arguments

**charge**: Successful charge object.

<a name="payment_failure_callback"></a>
### checkout Failure Callback

Notifies the receiver that payment has failed.
#### Declaration

*Kotlin:*
```kotlin
- fun checkoutChargeFailed(charge: Charge?) 
```

#### Arguments

**charge**: Charge object that has failed (if reached the stage of charging).

<a name="authorization_success_callback"></a>
### Authorization Success Callback

Notifies the receiver that authorization has succeed.
#### Declaration

*Kotlin:*

```kotlin
-  fun checkoutAuthorizeCaptured(authorize: Authorize)
```

#### Arguments

**authorize**: Successful authorize object.

<a name="authorization_failure_callback"></a>
### Authorization Failure Callback

Notifies the receiver that authorization has failed.

#### Declaration

*Kotlin:*

```kotlin
- fun checkoutAuthorizeFailed(authorize: Authorize?)
```

#### Arguments

**authorize**: Authorize object that has failed (if reached the stage of authorization).

<a name="card_saving_success_callback"></a>
### Card Saving Success Callback

Notifies the receiver that the customer has successfully saved the card.

#### Declaration

*Kotlin:*

```kotlin
- fun cardSaved(charge: Charge) // you have to cast Charge object to SaveCard object first to get card info 
```

#### Arguments

**Charge**: Charge object with the details.

<a name="card_saving_failure_callback"></a>
### Card Saving Failure Callback

Notifies the receiver that the customer failed to save the card.

#### Declaration

*Kotlin:*

```kotlin
- fun cardSavingFailed(charge: Charge)
```

#### Arguments

**Charge**: Charge object with the details (if reached the stage of card saving).

<a name="card_tokenized_success_callback"></a>
### Card Tokenized Success Callback

Notifies the receiver that the card has successfully tokenized.

#### Declaration

*Kotlin:*

```kotlin
- fun cardTokenizedSuccessfully(token: Token)
```
#### Arguments

**token**: card token object.

<a name="saved_cards_list_callback"></a>
### Saved Cards List Callback

Notifies the receiver with list of saved cards for a customer. If customer has no cards then you will receive the same response but with empty cards array.

#### Declaration

*Kotlin:*

```kotlin
-  fun savedCardsList(cardsList: CardsList)
```

#### Arguments

**cardsList**: CardsList model that holds the response.

<a name="session_error_callback"></a>
### Session Other Failure Callback

Notifies the receiver if any other error occurred.

#### Declaration

*Kotlin:*

```kotlin
- fun checkoutSdkError(goSellError: GoSellError?)
```

#### Arguments

**GoSellError**: GoSellError object with details of error.

<a name="invalid_card_details"></a>
### Invalid Card details

Notifies the client that card data passed are invalid

#### Declaration

*Kotlin:*

```kotlin
- fun invalidCardDetails()
```
<a name="backecnd_unknow_error"></a>
### Backend Unknown Error

Notifies the client that an unknown error has occurred in the backend

#### Declaration

*Kotlin:*

```kotlin
-  fun backendUnknownError(message: GoSellError?)
```

<a name="invalid_transaction_mode"></a>
### Invalid Transaction Mode

Notifies the client that Transaction Mode not configured.

#### Declaration

*Kotlin:*

```kotlin
-  fun invalidTransactionMode()
```

<a name="user_enabled_save_card_option"></a>
### User Enabled Save Card call back

Notifies the receiver (Merchant Activity) that the user wants to save his card.

#### Declaration
*Kotlin:*

```kotlin
- fun userEnabledSaveCardOption(saveCardEnabled: Boolean)
```
<a name="session_is_starting_callback"></a>
### Session Is Starting Callback

Notifies the receiver that session is about to start, but hasn't yet shown the SDK UI. You might want to use this method if you are not using `PayButton` in your application and want to show a loader before SDK UI appears on the screen.

#### Declaration

*Kotlin:*

```kotlin
- fun sessionIsStarting()
```
<a name="session_has_started_callback"></a>
### Session Has Started Callback

Notifies the receiver that session has successfully started and shown the SDK UI on the screen. You might want to use this method if you are not using `PayButton` in your application and want to hide a loader after SDK UI has appeared on the screen.

#### Declaration

*Kotlin*:

```kotlin
-  fun sessionHasStarted()
```
<a name="session_failed_to_start_callback"></a>
### Session Failed To Start Callback

Notifies the receiver that session has failed to start.

#### Declaration

*Kotlin:*

```kotlin
-  fun sessionFailedToStart()
```
<a name="session_cancel_callback"></a>
### Session Cancel Callback

Notifies the receiver (Merchant Activity) that the user cancelled payment process, clicked on soft back button, clicked hard back button or clicked Header cancel button.

#### Declaration

*Kotlin:*

```kotlin
- fun sessionCancelled()
```
-----
<a name="docs"></a>
# Documentation
Documentation is available at [github-pages][2].<br>
Also documented sources are attached to the library.
[1]:https://www.tap.company/developers/