package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ButtonStyle(@SerializedName("background")
                       @Expose
                       var background: BackgroundModel? = null,
                       @SerializedName("title_assets")
                       @Expose
                       var titleAssets: TittleAssetModel? = null):Serializable

data class BackgroundModel(@SerializedName("light") @Expose  var lightModel: LightModel, @SerializedName("dark") @Expose  var darkModel: DarkModel ):Serializable
data class TittleAssetModel(@SerializedName("light") @Expose  var lightAssetModel: LightAssetModel, @SerializedName("dark") @Expose  var darkAssetModel: DarkAssetModel ):Serializable

data class LightModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable
data class DarkModel(@SerializedName("base_color") @Expose  var baseColor: String, @SerializedName("background_colors") @Expose  var backgroundColors: ArrayList<String> ):Serializable

data class LightAssetModel(@SerializedName("en") @Expose  var en: String, @SerializedName("ar") @Expose  var ar: String ):Serializable
data class DarkAssetModel(@SerializedName("en") @Expose  var en: String, @SerializedName("ar") @Expose  var ar: String ):Serializable
