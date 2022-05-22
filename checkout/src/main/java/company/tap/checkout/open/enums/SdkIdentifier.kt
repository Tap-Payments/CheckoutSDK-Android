package company.tap.checkout.open.enums

import com.google.gson.annotations.SerializedName

enum class SdkIdentifier {
   /* react-native
    */
    @SerializedName("react-native")
    REACT_NATIVE,
    /**
     * Flutter
     */
    @SerializedName("Flutter")
    FLUTTER,

    /**
     * Native
     */
    @SerializedName("Native")
    NATIVE

}