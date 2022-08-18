package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data  class Description(@SerializedName("en")
                        @Expose
                        private val en: String? = null) : Serializable