package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class Hierarchy(
    @SerializedName("accountNumber")
    val accountNumber: String,
    @SerializedName("brandNameList")
    val brandNameList: List<BrandName>
)