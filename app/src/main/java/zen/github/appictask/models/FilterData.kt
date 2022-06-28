package zen.github.appictask.models


import com.google.gson.annotations.SerializedName

data class FilterData(
    @SerializedName("Cif")
    val cif: String,
    @SerializedName("companyName")
    val companyName: String,
    @SerializedName("hierarchy")
    val hierarchy: List<Hierarchy>
)