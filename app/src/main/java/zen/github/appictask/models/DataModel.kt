package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class DataModel(
    @SerializedName("errorCode")
    val errorCode: String,
    @SerializedName("filterData")
    val filterData: List<FilterData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)