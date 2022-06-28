package zen.github.appictask.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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
                    showMainFilterDialog()
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

    private fun showMainFilterDialog() {
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
            val brands =
                if (selectedAccountNo.size == 0) "" else if (selectedBrands.size == 0) "" else selectedBrands.size.toString()
            val locations =
                if (selectedAccountNo.size == 0) "" else if (selectedLocations.size == 0) "" else selectedLocations.size.toString()

            accountNo.text = Utility.toBoldText(
                "Acc No. : ",
                accounts
            )

            brand.text = Utility.toBoldText(
                "Brand : ",
                brands
            )

            location.text = Utility.toBoldText(
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
                        BottomSheetDialog(this@MainActivity, R.style.SheetDialog),
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

    private fun showFilters(
        bottomSheetDialog: BottomSheetDialog,
        binding: SectionDialogLayoutBinding,
        type: FilterType
    ) {
        binding.apply {

            header.text = "Select ${Utility.toCamelCase(type.name)}"

            search.hint = "Search for ${Utility.toCamelCase(type.name)}"

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

            when (type) {
                FilterType.ACCOUNT_NO -> {
                    accountAdapter = AccountAdapter(selectedAccountNo)
                    recyclerView.adapter = accountAdapter
                    accountAdapter.differ.submitList(getAccountNos())
                }
                FilterType.BRANDS -> {
                    brandAdapter = BrandAdapter(selectedBrands)
                    recyclerView.adapter = brandAdapter
                    brandAdapter.differ.submitList(getFilteredBrands())
                }
                FilterType.LOCATIONS -> {
                    locationAdapter = LocationAdapter(selectedLocations)
                    recyclerView.adapter = locationAdapter
                    locationAdapter.differ.submitList(getFilteredLocations())
                }
            }

            selectAll.isChecked = when (type) {
                FilterType.ACCOUNT_NO -> {
                    selectedAccountNo == getAccountNos()
                }
                FilterType.BRANDS -> {
                    selectedBrands == getFilteredBrands()
                }
                FilterType.LOCATIONS -> {
                    selectedLocations == getFilteredLocations()
                }
            }

            search.addTextChangedListener {
                it?.let {
                    when (type) {
                        FilterType.ACCOUNT_NO -> {
                            val searchedList: MutableList<Hierarchy> = ArrayList()
                            for (account in getAccountNos()) {
                                if (account.accountNumber.contains(it.toString()))
                                    searchedList.add(account)
                            }
                            accountAdapter.differ.submitList(searchedList)
                        }
                        FilterType.BRANDS -> {
                            val searchedList: MutableList<BrandName> = ArrayList()
                            for (account in getFilteredBrands()) {
                                if (account.brandName.lowercase()
                                        .contains(it.toString().lowercase())
                                )
                                    searchedList.add(account)
                            }
                            brandAdapter.differ.submitList(searchedList)
                        }
                        FilterType.LOCATIONS -> {
                            val searchedList: MutableList<LocationName> = ArrayList()
                            for (account in getFilteredLocations()) {
                                if (account.locationName.lowercase()
                                        .contains(it.toString().lowercase())
                                )
                                    searchedList.add(account)
                            }
                            locationAdapter.differ.submitList(searchedList)
                        }
                    }
                }
            }

            selectAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    when (type) {
                        FilterType.ACCOUNT_NO -> {
                            selectedAccountNo.clear()
                            accountAdapter.setSelectedItems(getAccountNos())
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

            add.setOnClickListener {
                when (type) {
                    FilterType.ACCOUNT_NO -> {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Accounts - ${selectedAccountNo.size}")
                        showMainFilterDialog()
                    }
                    FilterType.BRANDS -> {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Brands - ${selectedBrands.size}")
                        showMainFilterDialog()
                    }
                    FilterType.LOCATIONS -> {
                        bottomSheetDialog.dismiss()
                        Log.d(TAG, "Selected Locations - ${selectedLocations.size}")
                        showMainFilterDialog()
                    }
                }
            }

            close.setOnClickListener {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                    showMainFilterDialog()
                }
            }

            clear.setOnClickListener {
                if (selectAll.isChecked) selectAll.isChecked = false
                else {
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
        }

        bottomSheetDialog.apply {
            setContentView(binding.root)
            setCanceledOnTouchOutside(true)
            show()
        }
    }

    private fun getAccountNos(): List<Hierarchy> {
        return dataModel!!.filterData[0].hierarchy
    }

    private fun getFilteredBrands(): List<BrandName> {
        val list: MutableList<BrandName> = ArrayList()
        for (child in getAccountNos()) {
            if (selectedAccountNo.contains(child)) {
                list.addAll(child.brandNameList)
            }
        }
        return list
    }

    private fun getFilteredLocations(): List<LocationName> {
        val list: MutableList<LocationName> = ArrayList()
        for (child in getAccountNos()) {
            if (selectedAccountNo.contains(child)) {
                for (brand in child.brandNameList) {
                    if (selectedBrands.contains(brand)) {
                        list.addAll(brand.locationNameList)
                    }
                }
            }
        }
        return list
    }
}