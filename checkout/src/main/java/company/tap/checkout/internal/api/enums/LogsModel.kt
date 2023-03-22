package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class LogsModel : Serializable {
    /**
     * UI track
     */
    @SerializedName("UI")
    UI,
 /**
     * API     track.
     */
    @SerializedName("API")
        API,
    /**
     * EVENT track.
     */
    @SerializedName("EVENT")
    EVENT,

}