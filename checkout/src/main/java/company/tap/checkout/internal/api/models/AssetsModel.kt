package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AssetsModel( @SerializedName("localisation") @Expose
                        var localisation: LocalisationAssetModel,
                        @SerializedName("theme") @Expose
                        var theme: ThemeAssetModel) :Serializable


data class LocalisationAssetModel(@SerializedName("url") @Expose
                                   var urlLocale: String) :Serializable

data class ThemeAssetModel(@SerializedName("light") @Expose
                           var lightUrl: String, @SerializedName("dark") @Expose
var darkUrl: String) :Serializable
