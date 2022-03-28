package company.tap.checkout.internal.api.responses

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.interfaces.BaseResponse

/**
 * Created by AhlaamK on 7/29/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
class DeleteCardResponse(
                          @SerializedName("deleted") @Expose
                          var deleted:Boolean,
                          @SerializedName("id") @Expose
                          var id: String? = null,
                          @SerializedName("delete") @Expose
                          var delete: Boolean) : BaseResponse
