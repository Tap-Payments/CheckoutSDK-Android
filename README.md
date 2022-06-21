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