package company.tap.checkout.open.enums

import com.google.gson.annotations.SerializedName

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