package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Logos(@SerializedName("dark") @Expose
                 var dark: Dark? = null,
                 @SerializedName("light")
      @Expose
                 val light: Light? = null,

                 @SerializedName("light_colored")
      @Expose
                 val light_colored: LightColored? = null
) : Serializable

data class Dark( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String ):Serializable
data class Light( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String ):Serializable
data class LightColored( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String ):Serializable
