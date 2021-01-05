# CheckoutSDK-Android
CheckoutSDK Android


[![Platform](https://img.shields.io/badge/platform-Android-inactive.svg?style=flat)](https://github.com/Tap-Payments/TapQRCode-Android.git)
[![SDK Version](https://img.shields.io/badge/minSdkVersion-21-blue.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)
[![SDK Version](https://img.shields.io/badge/targetSdkVersion-29-informational.svg)](https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html)

# Table of Contents
---

1. [Requirements](#requirements)
2. [Installation](#installation)
   1. [Include TapUIKit-Android library as a dependency module in your project](#include_library_to_code_locally)
   2. [Installation with jitpack](#installation_with_jitpack)
3. [Features](#features)
   1. [Atoms](#atoms)
   2. [Molecules](#molecules)
4. [Usage](#usage)
   1. [TapTextView](#tap_textview)
   2. [TapSwitch](#tap_switch)
   3. [TapImageView](#tap_imageview)
   4. [TapButton](#tap_button)
   5. [TapSeparatorView](#tap_separator)
   6. [TapDragIndicator](#tap_dragindicator)
   7. [TapChip](#tap_chip)
   8. [TapChipGroup](#tap_chip_group)
   9. [TapHeaderSectionView](#tap_header_section)
   10. [TapListItemView](#tap_item_listview)
   11. [TapAmountSectionView](#tap_amount_section_view)
   12. [TapSelectionTabLayout](#tap_selection_tablayout)
   13. [TapBottomSheetDialog](#tap_bottom_sheetdialog)
   14. [TapCardSwitch](#tap_card_switch)
   15. [TapNFCView](#tap_nfc_view)
   16. [TabAnimatedActionButton](#tap_animated_action_button)
5. [DataSources](#datasources)
   1. [HeaderDataSource](#header_data_source)
   2. [AmountDataSource](#amount_data_source)
   3. [ItemViewDataSource](#itemview_data_source)
   4. [ActionButtonDataSource](#actionbutton_data_source)
   5. [AnimationDataSource](#animation_data_source)


<a name="requirements"></a>
## Requirements

To use the SDK the following requirements must be met:

1. **Android Studio 3.6** or newer
2. **Android SDK Tools 29.0.0 ** or newer
3. **Android Platform Version: API 29: Android 10.0 (Q)
4. **Android targetSdkVersion: 29

<a name="installation"></a>
# Installation
---
<a name="include_library_to_code_locally"></a>
### Include TapUIKit-Android library as a dependency module in your project
---
1. Clone TapUIKit-Android library from Tap repository
   ```
       git@github.com:Tap-Payments/TapUIKit-Android.git
    ```
2. Add TapUIKit-Android library to your project settings.gradle file as following
    ```java
        include ':tapuilibrary', ':YourAppName'
    ```
3. Setup your project to include TapUIKit-Android as a dependency Module.
   1. File -> Project Structure -> Modules -> << your project name >>
   2. Dependencies -> click on **+** icon in the screen bottom -> add Module Dependency
   3. select tapuilibrary library

<a name="installation_with_jitpack"></a>
### Installation with JitPack
---
[JitPack](https://jitpack.io/) is a novel package repository for JVM and Android projects. It builds Git projects on demand and provides you with ready-to-use artifacts (jar, aar).

To integrate TapUIKit-Android into your project add it in your **root** `build.gradle` at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```groovy
	dependencies {
      implementation 'com.github.Tap-Payments:TapUIKit-Android:0.0.6.8'
	}
```

<a name="features"></a>
## Features

------

`TapUIKit-Android` provides extensive ways for using the Tap UI views as atoms , molecules etc and are easy to
integrate with.

<a name="atoms"></a>
### Atoms
If atoms are the basic building blocks of matter, then the atoms of our interfaces serve as the foundational building blocks that comprise all our user interfaces. These atoms include basic elements like textviews, inputlayouts, buttons, and others that can’t be broken down any further without ceasing to be functional.


 ![Atoms](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/atom.png)

 Atom Name    |                  Usage                       |
------------- | -------------------------------------------- |
TapTextView   |  <company.tap.tapuilibrary.atoms.TapTextView/> |
TapSwitch     | <company.tap.tapuilibrary.atoms.TapSwitch />   |
TapImageView   | <company.tap.tapuilibrary.atoms.TapImageView />|
TapButton     | <company.tap.tapuilibrary.atoms.TapButton/> |
TapEditText   | <company.tap.tapuilibrary.atoms.TapEditText />|     |
TapSeparatorView   | <company.tap.tapuilibrary.atoms.TapSeparatorView />|
TapDragIndicator   | <company.tap.tapuilibrary.atoms.TapDragIndicator/> |
TapChip            |<company.tap.tapuilibrary.atoms.TapChip/>|
TapChipGroup|<company.tap.tapuilibrary.atoms.TapChipGroup />|

<a name="molecules"></a>
### Molecules
Molecules are relatively simple groups of UI elements functioning together as a unit. For example, a form label, search  input, and button can join together to create a search form molecule.


 ![Molecules](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/molecules.png)
Molecule Name    |                  Usage                       |                configuration                         |
------------- | ------------------------------------------------|------------------------------------------------------|
TapHeader   |  <company.tap.tapuilibrary.views.TapHeaderSectionView |  businessName, businessIcon, businessPlaceHolder |
TapAmountSectionView |  <company.tap.tapuilibrary.views.TapAmountSectionView | selectedCurrency, currentCurrency, itemCount |
TapListItemView     | <company.tap.tapuilibrary.views.TapListItemView    | itemTitle,itemAmount, totalAmount, totalQuantity |
TapSelectionTabLayout| <company.tap.tapuilibrary.views.TapSelectionTabLayout|                                               |
TapBottomSheetDialog| <company.tap.tapuilibrary.views.TapBottomSheetDialog|  header, amountsection, listview, selectionTab  |
TapCardSwitch| <company.tap.tapuilibrary.views.TapCardSwitch|  mobileSave, merchantCheckoutSave, goPayCheckoutSave  |
TapNFCView|<company.tap.tapuilibrary.views.TapNFCView| not required   |
TabAnimatedActionButton| <company.tap.tapuilibrary.views.TabAnimatedActionButton|                                           |

<a name="usage"></a>
## Usage
<a name="tap_textview"></a>
1. TapTextView

 1.a. Enable any View extending TextView in code:
 ```kotlin
 AnyTextView : TapTextView()
 ```
 2.b. Enable any View extending TextView in XML:
 ```xml
  <company.tap.tapuilibrary.atoms.TapTextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:text="Welcome to Tap UI Kit"
        android:gravity="center"
        android:textSize="30sp"/>
```
<a name="tap_switch"></a>
2. TapSwitch
 Enable any View extending TapSwitch in XML:
 ```xml
  <company.tap.tapuilibrary.atoms.TapSwitch
        android:id="@+id/switch_discount"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:text="Apply Discount"
        android:layout_marginEnd="7dp"
        android:layout_marginStart="7dp"/>
```
<a name="tap_imageview"></a>
3. TapImageView

Enable any View extending TapImageView in XML:
```xml
<company.tap.tapuilibrary.atoms.TapImageView
        android:id="@+id/imageView_master"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/mastercard"
        android:scaleType="centerCrop"
        android:visibility="gone"
        android:layout_gravity="center"
        android:layout_marginStart="@dimen/margin_basic_8dp"
        android:layout_marginEnd="@dimen/margin_basic_8dp"/>
```
<a name="tap_button"></a>
4.TapButton

4.a. Enable any View extending TapButton in code:
```kotlin
AnyButton:TapButton()
```
 4.b.Enable any View extending TapButton in XML:
 ```xml
 <company.tap.tapuilibrary.atoms.TapButton
        android:id="@+id/textView_itemcount"
        android:layout_width="70dp"
        android:layout_height="@dimen/margin_basic_20dp"
        android:fontFamily="sans-serif-light"
        android:gravity="center"
        android:textColor="#4b4847"
        android:textSize="9sp"
        android:textStyle="normal"
        tools:text="10 ITEMS"
        android:background="@drawable/rounded_rectangle"
        android:layout_marginTop="@dimen/margin_basic_19dp"
        android:layout_marginBottom="@dimen/margin_basic_19dp"
        android:layout_marginEnd="21dp"
        android:layout_marginStart="283dp"
        android:elevation="@dimen/margin_basic_0dp"
       />
 ```
<a name="tap_separator"></a>
 5. TapSeparatorView

Enable any View extending TapSeparatorView in XML:
```xml
<company.tap.tapuilibrary.atoms.TapSeparatorView
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_below="@+id/textView_itemcount"
        android:layout_marginTop="@dimen/margin_basic_9dp"
        android:background="#dfdfdf"
        />
```
<a name="tap_dragindicator"></a>
6. TapDragIndicator

Enable any View extending TapDragIndicator in XML:
```xml
 <company.tap.tapuilibrary.atoms.TapDragIndicator
        android:layout_width="75dp"
        android:layout_height="4dp"
        android:layout_marginEnd="150dp"
        android:layout_marginStart="150dp"
        app:cardCornerRadius="@dimen/margin_basic_8dp"
        android:layout_marginTop="@dimen/margin_basic_10dp"
        app:cardElevation="@dimen/margin_basic_0dp"
        />
```
<a name="tap_chip"></a>
7. TapChip

Enable any View extending TapChip in XML:
```xml
 <company.tap.tapuilibrary.atoms.TapChip
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:layout_marginStart="@dimen/margin_basic_16dp"
            android:layout_marginBottom="@dimen/margin_basic_13dp"
            app:cardCornerRadius="8dp"
            app:cardElevation="@dimen/margin_basic_0dp"
          />
 ```
<a name="tap_chip_group"></a>
 8.TapChipGroup

 Enable any View extending TapChipGroup in XML:
 ```xml
 <company.tap.tapuilibrary.atoms.TapChipGroup
        android:id="@+id/currencyLayout1"
        android:layout_width="match_parent"
        android:layout_height="75dp"
        android:background="@color/chip_background"
        android:orientation="horizontal">

    </company.tap.tapuilibrary.atoms.TapChipGroup>
```
<a name="tap_header_section"></a>
9. TapHeaderSectionView Molecule

 Enable any View extending TapHeader in XML:
 ```xml
   <company.tap.tapuilibrary.views.TapHeader
        android:layout_marginTop="@dimen/margin_basic_9dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />
```

![HeaderMolecule](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/headermolecule.png)

<a name="tap_amount_section_view"></a>
10. TapAmountSectionView Molecule

Enable any View extending TapAmountSectionView in XML:
```xml
  <company.tap.tapuilibrary.views.TapAmountSectionView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

![AmountSectionMolecule](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/amountsection.png)

<a name="tap_item_listview"></a>
11. TapListItemView Molecule

 Enable any View extending TapItemsView in XML:
 ```xml
 <company.tap.tapuilibrary.views.TapItemsView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

![TapListItemViewMolecule](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/tapitemlistview.png)

<a name="tap_selection_tablayout"></a>
12. TapSelectionTabLayout Molecule

Enable any View extending TapSelectionTabLayout in XML:
```xml
  <company.tap.tapuilibrary.views.TapSelectionTabLayout
        android:id="@+id/sections_tablayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
<a name="tap_bottom_sheetdialog"></a>
13.TapBottomSheetDialog Molecule

 13.a. Enable any View extending TapBottomSheetDialog in code:
 ```kotlin
open class BottomSheetDialog : TapBottomSheetDialog()
 ```
 13.b. Enable any View extending TextView in XML:
 ```xml
 <company.tap.tapuilibrary.views.TapBottomSheetDialog
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
       />
```
<a name="tap_card_switch"></a>
14.TapCardSwitch

Enable any View extending TextView in XML:
 ```xml
<company.tap.tapuilibrary.views.TapCardSwitch
        android:id="@+id/switch_pay_demo"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_margin="20dp" />
 ```
![TapCardSwitchMolecule](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/switchmolecule.png)

<a name="tap_nfc_view"></a>
15.TapNFCView

Enable any View extending TapNFCView in XML:
 ```xml
 <company.tap.tapuilibrary.views.TapNFCView
        android:id="@+id/custom_nfc"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />
 ```
![TapNFCView](https://github.com/Tap-Payments/TapUIKit-Android/blob/master/images/nfcmolecule.png)

<a name="tap_animated_action_button"></a>
16.TabAnimatedActionButton

Enable any View extending TextView in XML:
 ```xml
<company.tap.tapuilibrary.views.TabAnimatedActionButton
        android:id="@+id/action_button"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_margin="20dp" />
 ```
 <a name="datasources"></a>
## DataSources

<a name="header_data_source"></a>
### 1. HeaderDataSource
  Set data for businessName, businessIcon and businessPlaceHolder from the consumer app as below:
  ```kotlin
  private fun getHeaderDataSource(): HeaderDataSource {
          return HeaderDataSource(
              businessName = businessName,
              businessFor = LocalizationManager.getValue("paymentFor", "TapMerchantSection"),
              businessImageResources = imageUrl,
              businessPlaceHolder = businessName?.get(0).toString()
          )
      }

  ```
<a name="amount_data_source"></a>
### 2. AmountViewDataSource
  Set data for selectedCurrency, currentCurrency and itemCount as shown:
  ```kotlin
  private fun getAmountViewDataSOurce(): AmountViewDataSource {
          return AmountViewDataSource(
              selectedCurr = "SR1000,000.000" ,
              currentCurr = "KD1000,000.000",
              itemCount = "22 ITEMS"
          )
      }
 ```
<a name="itemview_data_source"></a>
### 3. ItemViewDataSource
   Set data for itemTitle,  itemAmount , totalAmount and totalQuantity as below:
   ```kotlin
   private fun getItemViewdataSource(): ItemViewDataSource {
           return ItemViewDataSource(
               itemTitle = "ITEM TITLE",
               itemAmount = "KD000,000.000",
               totalQuantity = "1",
               totalAmount = "KD000,000.000"
           )

       }
   ```
<a name="actionbutton_data_source"></a>
### 4. ActionButtonDataSource
  Set ActionButton data as shown:
  ```kotlin
  private fun getSuccessDataSource(): ActionButtonDataSource {
          return ActionButtonDataSource(
              text = "PAY!",
              textSize = 20f,
              textColor = Color.WHITE,
              cornerRadius = 10f,
              successImageResources = R.drawable.ic_check,
              backgroundColor = resources.getColor(R.color.button_green)
          )
      }
   ```
<a name="animation_data_source"></a>
### 5. AnimationDataSource
  Set Animation data as shown:
  ```kotlin
     val animationDataSource = AnimationDataSource(
                 fromHeight = height,
                 toHeight = MAX_RADIUS,
                 fromWidth = width,
                 toWidth = MAX_RADIUS,
                 fromCorners = dataSource?.cornerRadius,
                 toCorners = MAX_CORNERS,
                 fromColor = dataSource?.backgroundColor,
                 toColor = dataSource?.errorColor,
                 duration = MAX_DURATION,
                 background = backgroundDrawable
             )
  ```
