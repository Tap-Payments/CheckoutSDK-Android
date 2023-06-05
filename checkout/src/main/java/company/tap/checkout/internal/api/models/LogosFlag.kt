//package company.tap.checkout.internal.api.models
//
//import com.google.gson.annotations.Expose
//import com.google.gson.annotations.SerializedName
//import java.io.Serializable
//
//data class LogosFlag(@SerializedName("dark") @Expose
//                 var dark: DarkFlag? = null,
//                 @SerializedName("light")
//                  @Expose
//                 val light: LightFlag? = null,
//
//                 @SerializedName("light_mono")
//                 @Expose
//                 val light_mono: LightMonoFlag? = null,
//                 @SerializedName("dark_colored")
//                 @Expose
//                 val dark_colored: DarkColoredFlag? = null
//
//) : Serializable
//
//data class DarkFlag( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String):Serializable
//data class DarkColoredFlag( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String):Serializable
//
//data class LightFlag( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String):Serializable
//data class LightMonoFlag( @SerializedName("svg") @Expose  var svg: String ,@SerializedName("png") @Expose  var png: String):Serializable
//
//
