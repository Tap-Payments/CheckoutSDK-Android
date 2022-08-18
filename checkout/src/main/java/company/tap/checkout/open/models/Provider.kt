package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Provider(
    @field:Expose @field:SerializedName("id") private val id: String,
    @field:Expose @field:SerializedName(
        "name"
    ) private val name: String
) :
    Serializable

