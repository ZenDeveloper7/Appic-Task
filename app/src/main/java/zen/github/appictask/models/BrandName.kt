package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class BrandName(
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("locationNameList")
    val locationNameList: List<LocationName>
)