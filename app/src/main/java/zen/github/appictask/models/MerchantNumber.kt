package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class MerchantNumber(
    @SerializedName("mid")
    val mid: String,
    @SerializedName("outletNumber")
    val outletNumber: List<String>
)