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

data class Dark( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String, @SerializedName("currency_widget") @Expose  var currencyWidget: CurrencyWidgetModel,  @SerializedName("disabled") @Expose  var disabled: DisabledModel ):Serializable

data class DisabledModel( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String ):Serializable
data class CurrencyWidgetModel( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String ):Serializable


data class Light( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String,  @SerializedName("currency_widget") @Expose  var currencyWidget: CurrencyWidgetModel,  @SerializedName("disabled") @Expose  var disabled: DisabledModel ):Serializable
data class LightColored( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String,  @SerializedName("currency_widget") @Expose  var currencyWidget: CurrencyWidgetModel,  @SerializedName("disabled") @Expose  var disabled: DisabledModel ):Serializable
