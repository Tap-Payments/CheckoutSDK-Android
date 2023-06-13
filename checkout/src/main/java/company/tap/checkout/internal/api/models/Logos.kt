//package company.tap.checkout.internal.api.models
//
//import com.google.gson.annotations.Expose
//import com.google.gson.annotations.SerializedName
//import java.io.Serializable
//
//data class Logos(
//    @SerializedName("dark") @Expose
//    var dark: Dark? = null,
//    @SerializedName("light")
//    @Expose
//    val light: Light? = null,
//
//    @SerializedName("light_mono")
//    @Expose
//    val light_mono: LightMono? = null,
//    @SerializedName("dark_colored")
//    @Expose
//    val dark_colored: DarkColored? = null
//
//) : Serializable
//
//data class Dark(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String,
//    @SerializedName("currency_widget") @Expose var currencyWidget: CurrencyWidgetModel,
//    @SerializedName("disabled") @Expose var disabled: DisabledModel,
//    @SerializedName("card_icon") @Expose var cardIcon: CardIconModel
//) : Serializable
//
//data class DarkColored(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String,
//    @SerializedName("currency_widget") @Expose var currencyWidget: CurrencyWidgetModel,
//    @SerializedName("disabled") @Expose var disabled: DisabledModel,
//    @SerializedName("card_icon") @Expose var cardIcon: CardIconModel
//) : Serializable
//
//data class Light(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String,
//    @SerializedName("currency_widget") @Expose var currencyWidget: CurrencyWidgetModel,
//    @SerializedName("disabled") @Expose var disabled: DisabledModel,
//    @SerializedName("card_icon") @Expose var cardIcon: CardIconModel
//) : Serializable
//
//data class LightMono(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String,
//    @SerializedName("currency_widget") @Expose var currencyWidget: CurrencyWidgetModel,
//    @SerializedName("disabled") @Expose var disabled: DisabledModel,
//    @SerializedName("card_icon") @Expose var cardIcon: CardIconModel
//) : Serializable
//
//
//data class DisabledModel(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String
//) : Serializable
//
//data class CurrencyWidgetModel(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String
//) : Serializable
//
//data class CardIconModel(
//    @SerializedName("svg") @Expose var svg: String,
//    @SerializedName("png") @Expose var png: String
//) : Serializable
