package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ButtonStyle(@SerializedName("background")
                       @Expose
                       var background: BackgroundModel? = null,
                       @SerializedName("title_asset")
                       @Expose
                       var titleAssets: String? = null):Serializable

data class BackgroundModel(@SerializedName("light") @Expose  var lightModel: LightModel, @SerializedName("dark") @Expose  var darkModel: DarkModel,  @SerializedName("light_mono") @Expose  var lightMonoModel: LightMonoModel,  @SerializedName("dark_colored") @Expose  var darkColorModel: DarkColorModel ):Serializable
data class LightModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable
data class DarkModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable
data class LightMonoModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable
data class DarkColorModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable


