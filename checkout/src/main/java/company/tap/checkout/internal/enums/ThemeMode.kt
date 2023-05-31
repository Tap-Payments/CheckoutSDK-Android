package company.tap.checkout.internal.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ThemeMode : Serializable {
    /**
     * light track
     */
    @SerializedName("light")
    light,
    /**
     * light_mono     light_mono.
     */
    @SerializedName("light_mono")
    light_mono,

    /**
     * dark     .
     */
    @SerializedName("dark")
    dark,

    /**
     * dark_colored     .
     */
    @SerializedName("dark_colored")
    dark_colored,


}