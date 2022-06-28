package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class LocationName(
    @SerializedName("locationName")
    val locationName: String,
    @SerializedName("merchantNumber")
    val merchantNumber: List<MerchantNumber>
)