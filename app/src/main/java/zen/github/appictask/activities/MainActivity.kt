package zen.github.appictask.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import zen.github.appictask.R
import zen.github.appictask.adapter.AccountAdapter
import zen.github.appictask.adapter.BrandAdapter
import zen.github.appictask.adapter.LocationAdapter
import zen.github.appictask.databinding.ActivityMainBinding
import zen.github.appictask.databinding.MainFilterLayoutBinding
import zen.github.appictask.databinding.SectionDialogLayoutBinding
import zen.github.appictask.helper.Utility
import zen.github.appictask.models.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogBinding: MainFilterLayoutBinding
    private lateinit var selectedAccountNo: MutableList<Hierarchy>
    private lateinit var selectedBrands: MutableList<BrandName>
    private lateinit var selectedLocations: MutableList<LocationName>
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var locationAdapter: LocationAdapter
    private var dataModel: DataModel? = null
    private var TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedAccountNo = ArrayList()
        selectedBrands = ArrayList()
        selectedLocations = ArrayList()

        readData()

        binding.apply {
            filter.setOnClickListener {
                dataModel?.let {
                    Log.d(TAG, dataModel.toString())
                    showFilterDialog()
                } ?: run {
                    Toast.makeText(this@MainActivity, "Error Loading Data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun readData() {
        dataModel = Gson().fromJson(
            Utility.readXMLinString(
                "data.json",
                applicationContext
            ), DataModel::class.java
        )
    }

    private fun showFilterDialog() {
        dialogBinding = MainFilterLayoutBinding.inflate(layoutInflater)
        val filterDialog = BottomSheetDialog(this, R.style.SheetDialog)
        dialogBinding.apply {
            company.text = dataModel!!.filterData[0].companyName

            if (getFilteredBrands().isNotEmpty()) {
                brandLayout.alpha = 1f
            } else {
                brandLayout.alpha = 0.5f
            }

            if (getFilteredLocations().isNotEmpty()) {
                locationLayout.alpha = 1f
            } else {
                locationLayout.alpha = 0.5f
            }

            val accounts =
                if (selectedAccountNo.size == 0) "" else selectedAccountNo.size.toString()
            val brands = if (selectedBrands.size == 0) "" else selectedBrands.size.toString()
            val locations =
                if (selectedLocations.size == 0) "" else selectedLocations.size.toString()

            accountNo.text = Utility.boldText(
                "Acc No. : ",
                accounts
            )

            brand.text = Utility.boldText(
                "Brand : ",
                brands
            )

            location.text = Utility.boldText(
                "Location : ",
                locations
            )

            accountCount.text = accounts
            brandCount.text = brands
            locationCount.text = locations

            accountLayout.setOnClickListener {
                filterDialog.dismiss()
                showFilters(
                    BottomSheetDialog(this@MainActivity, R.style.SheetDialog),
                    SectionDialogLayoutBinding.inflate(layoutInflater),
                    FilterType.ACCOUNT_NO
                )
            }

            viewAccounts.setOnClickListener {
                accountLayout.callOnClick()
            }

            brandLayout.setOnClickListener {
                if (getFilteredBrands().isNotEmpty()) {
                    filterDialog.dismiss()
                    showFilters(
                        BottomSheetDialog(this@MainActivity, R.style.SheetDialog),
                        SectionDialogLayoutBinding.inflate(layoutInflater),
                        FilterType.BRANDS
                    )
                }
            }

            viewBrands.setOnClickListener {
                brandLayout.callOnClick()
            }

            locationLayout.setOnClickListener {
                if (getFilteredLocations().isNotEmpty()) {
                    filterDialog.dismiss()
                    showFilters(
                        BottomSheetDialog(this@MainActivity),
                        SectionDialogLayoutBinding.inflate(layoutInflater),
                        FilterType.LOCATIONS
                    )
                }
            }

            viewLocations.setOnClickListener {
                locationLayout.callOnClick()
            }

           /* clear.setOnClickListener {
                selectedAccountNo.clear()
                selectedBrands.clear()
                selectedLocations.clear()
            }*/

            close.setOnClickListener {
                if (filterDialog.isShowing)
                    filterDialog.dismiss()
            }
        }

        filterDialog.apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(true)
            show()
        }

    }

    private fun getFilteredBrands(): MutableList<BrandName> {
        val list: MutableList<BrandName> = ArrayList()
        dataModel?.let {
            for (child in it.filterData[0].hierarchy) {
                if (selectedAccountNo.contains(child)) {
                    list.addAll(child.brandNameList)
                }
            }
        }
        return list
    }

    private fun getFilteredLocations(): MutableList<LocationName> {
        val list: MutableList<LocationName> = ArrayList()
        dataModel?.let {
            for (child in it.filterData[0].hierarchy) {
                if (selectedAccountNo.contains(child)) {
                    for (brand in child.brandNameList) {
                        if (selectedBrands.contains(brand)) {
                            list.addAll(brand.locationNameList)
                        }
                    }
                }
            }
        }
        return list
    }

    private fun showFilters(
        bottomSheetDialog: BottomSheetDialog,
        binding: SectionDialogLayoutBinding,
        type: FilterType
    ) {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

            header.text = "Select ${Utility.toCamelCase(type.name)}"

            close.setOnClickListener {
                if (bottomSheetDialog.isShowing)
                    bottomSheetDialog.dismiss()
            }

            clear.setOnClickListener {
                selectAll.isChecked = false
            }

            search.hint = "Search for ${Utility.toCamelCase(type.name)}"

            selectAll.isChecked = when (type) {
                FilterType.ACCOUNT_NO -> {
                    val hierarchy = dataModel!!.filterData[0].hierarchy
                    selectedAccountNo == hierarchy
                }
                FilterType.BRANDS -> {
                    selectedBrands == getFilteredBrands()
                }
                FilterType.LOCATIONS -> {
                    selectedLocations == getFilteredLocations()
                }
            }

            selectAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    when (type) {
                        FilterType.ACCOUNT_NO -> {
                            selectedAccountNo.clear()
                            accountAdapter.setSelectedItems(dataModel!!.filterData[0].hierarchy)
                        }
                        FilterType.BRANDS -> {
                            selectedBrands.clear()
                            brandAdapter.setSelectedItems(getFilteredBrands())
                        }
                        FilterType.LOCATIONS -> {
                            selectedLocations.clear()
                            locationAdapter.setSelectedItems(getFilteredLocations())
                        }
                    }
                } else {
                    when (type) {
                        FilterType.ACCOUNT_NO -> {
                            selectedAccountNo.clear()
                            accountAdapter.setSelectedItems(selectedAccountNo)
                        }
                        FilterType.BRANDS -> {
                            selectedBrands.clear()
                            brandAdapter.setSelectedItems(selectedBrands)
                        }
                        FilterType.LOCATIONS -> {
                            selectedLocations.clear()
                            locationAdapter.setSelectedItems(selectedLocations)
                        }
                    }
                }
            }

            when (type) {
                FilterType.ACCOUNT_NO -> {
                    accountAdapter = AccountAdapter(selectedAccountNo)
                    recyclerView.adapter = accountAdapter
                    accountAdapter.differ.submitList(dataModel!!.filterData[0].hierarchy)

                    add.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Accounts - ${selectedAccountNo.size}")
                        showFilterDialog()
                    }
                }
                FilterType.BRANDS -> {
                    brandAdapter = BrandAdapter(selectedBrands)
                    recyclerView.adapter = brandAdapter
                    brandAdapter.differ.submitList(getFilteredBrands())

                    add.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Brands - ${selectedBrands.size}")
                        showFilterDialog()
                    }
                }
                FilterType.LOCATIONS -> {
                    locationAdapter = LocationAdapter(selectedLocations)
                    recyclerView.adapter = locationAdapter
                    locationAdapter.differ.submitList(getFilteredLocations())

                    add.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Locations - ${selectedLocations.size}")
                        showFilterDialog()
                    }
                }
            }
        }

        bottomSheetDialog.apply {
            setContentView(binding.root)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(true)
            show()
        }
    }
}