package com.ecommerce.letgoecommerce.pages

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion._categoryList
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion._randomListCity
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion._randomListProduct
import com.ecommerce.letgoecommerce.adapter.Category.CategoryListAdapter
import com.ecommerce.letgoecommerce.adapter.HeaderAdapter
import com.ecommerce.letgoecommerce.adapter.RandomProduct.RandomProductListAdapter
import com.ecommerce.letgoecommerce.adapter.ShowCase.ShowCaseListAdapter
import com.ecommerce.letgoecommerce.databinding.FilterScreenDesignBinding
import com.ecommerce.letgoecommerce.databinding.FragmentHomeScreenBinding
import com.ecommerce.letgoecommerce.extension.Constant
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carColorList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carEngineList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carFuelList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carGearList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carTractionList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carTypeList
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.model.CategoryModel
import com.ecommerce.letgoecommerce.model.Filter
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.User
import com.ecommerce.letgoecommerce.sql.ConSQL
import kotlinx.coroutines.*

class HomeScreen : Fragment() {


    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var showCaseAdapter: ShowCaseListAdapter
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var randomProductAdapter: RandomProductListAdapter

    private lateinit var showCaseHeaderAdapter: HeaderAdapter
    private var user: User? = null
    private lateinit var conSQL: ConSQL

    private lateinit var randomListProduct: List<Product>
    private lateinit var randomListCity: List<Product>
    private lateinit var categoryList: List<CategoryModel>

    private val isUpdate = MutableLiveData<Boolean>()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conSQL = ConSQL()

        user = conSQL.getUserInfo()

        if (user?.photo_url != null)
            binding.topBar.profileImage.setImageBitmap(convertImagetoBitmap(user?.photo_url))

        showCaseAdapter = ShowCaseListAdapter(::itemClickShowCase)
        categoryAdapter = CategoryListAdapter(::itemClickCategory)
        randomProductAdapter = RandomProductListAdapter(::itemClickRandomProduct)

        showCaseHeaderAdapter = HeaderAdapter(
            user?.city.toString(),
            "Hepsini gör",
            ::itemClickShowCaseAll
        )

        initializeClick()



        setAdapter()

        isUpdate.observe(viewLifecycleOwner) {
            if (it)
                concatAdapter?.notifyItemRangeChanged(0, 5)

            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false
        }

        if (_randomListCity == null || _randomListProduct == null || _categoryList == null)
            refreshUI()
        else
            initializeConcatAdapter()


    }


    private fun initializeClick() = binding.apply {

        topBar.profileImage.setOnClickListener {
            findNavController().navigate(
                HomeScreenDirections.actionHomeScreenToUserScreen()
            )
        }


        topBar.searchButton.setOnClickListener { findNavController().navigate(HomeScreenDirections.actionHomeScreenToSearchScreen()) }
        topBar.filterText.setOnClickListener { openFilterDialog() }

        swipeRefresh.setOnRefreshListener {
            refreshUI()
        }

    }

    private fun refreshUI() = GlobalScope.launch {

        if (!binding.swipeRefresh.isRefreshing)
            binding.swipeRefresh.isRefreshing = true

        categoryList = getCategory()
        randomListCity = getProductByCity()
        randomListProduct = getProductByRandom()

        randomProductAdapter.updateList(randomListProduct)
        showCaseAdapter.updateList(randomListCity)
        categoryAdapter.updateList(categoryList)

        isUpdate.postValue(true)
    }

    private var filterCategoryId = -1
    private var filterCityId: Int? = null

    private fun openFilterDialog() {

        val view = FilterScreenDesignBinding.inflate(layoutInflater)

        val filterCategoryList = categoryList.map { it.name }
        val cityList = getCity()
        val filterCityList = cityList.map { it.name }

        (filterCityList as ArrayList).add(0, "Şehir seçiniz")

        val categoryAdapter = getArrayAdapter(filterCategoryList)

        val cityAdapter = getArrayAdapter(filterCityList)

        view.category.adapter = categoryAdapter
        view.category.setSelection(1)
        view.city.adapter = cityAdapter

        view.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterCategoryId = categoryList[p2].id!!
                view.carExtension.visibility =
                    if (filterCategoryId == 13) View.VISIBLE else View.GONE

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        view.city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterCityId = if (p2 == 0) null else cityList[p2 - 1].id
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        initializeDialogCarExtension(view)

        val dialog = AlertDialog.Builder(requireContext()).setView(view.root).show()


        view.applyFilter.setOnClickListener {

            if (filterCategoryId != -1) {
                if (filterCategoryId == 13)
                    if (filterChooseCarColor.isNotEmpty() && filterChooseCarEngine.isNotEmpty()
                        && filterChooseCarFuel.isNotEmpty() && filterChooseCarGear.isNotEmpty()
                        && filterChooseCarType.isNotEmpty() && filterChooseCarTraction.isNotEmpty()
                    ) {

                        val minKm = view.minCarKm.text.toString()
                        val maxKm = view.maxCarKm.text.toString()

                        val maxYear = view.maxCarModel.text.toString()
                        val minYear = view.minCarModel.text.toString()

                        val filterModel = Filter(
                            categoryId = filterCategoryId,
                            cityId = filterCityId,
                            maxPrice = if (view.maxPrice.getNumericValue()
                                    .toInt() == 0
                            ) null else view.maxPrice.getNumericValue().toInt(),
                            minPrice = if (view.minPrice.getNumericValue()
                                    .toInt() == 0
                            ) null else view.minPrice.getNumericValue().toInt(),
                            minYear = if (minYear.isEmpty()) null else minYear.toInt(),
                            maxYear = if (maxYear.isEmpty()) null else maxYear.toInt(),
                            minKm = if (minKm.trim().isEmpty()) null else minKm.toInt(),
                            maxKm = if (maxKm.trim().isEmpty()) null else maxKm.toInt(),
                            carType = filterChooseCarType,
                            carColor = filterChooseCarColor,
                            carEngine = filterChooseCarEngine,
                            carFuel = filterChooseCarFuel,
                            carGear = filterChooseCarGear,
                            carTraction = filterChooseCarTraction
                        )
                        findNavController().navigate(
                            HomeScreenDirections.actionHomeScreenToSearchScreen(filterModel)
                        )
                        dialog.dismiss()

                    } else
                        showToast("Doldurulması gerekli alanları doldurunuz")
                else {
                    val filterModel = Filter(
                        categoryId = filterCategoryId,
                        cityId = filterCityId,
                        maxPrice = if (view.maxPrice.getNumericValue()
                                .toInt() == 0
                        ) null else view.maxPrice.getNumericValue().toInt(),
                        minPrice = if (view.minPrice.getNumericValue()
                                .toInt() == 0
                        ) null else view.minPrice.getNumericValue().toInt()
                    )
                    findNavController().navigate(
                        HomeScreenDirections.actionHomeScreenToSearchScreen(filterModel)
                    )
                    dialog.dismiss()
                }


            } else
                showToast("Tüm alanların doldurulması gerekir")

        }

    }

    private var filterChooseCarType = ""
    private var filterChooseCarEngine = ""
    private var filterChooseCarGear = ""
    private var filterChooseCarColor = ""
    private var filterChooseCarFuel = ""
    private var filterChooseCarTraction = ""

    private fun initializeDialogCarExtension(view: FilterScreenDesignBinding) {

        val carTypeAdapter = getArrayAdapter(Constant.carTypeList)
        val carEngineAdapter = getArrayAdapter(Constant.carEngineList)
        val carGearAdapter = getArrayAdapter(Constant.carGearList)
        val carColorAdapter = getArrayAdapter(Constant.carColorList)
        val carFuelAdapter = getArrayAdapter(Constant.carFuelList)
        val carTractionAdapter = getArrayAdapter(Constant.carTractionList)

        view.carType.adapter = carTypeAdapter
        view.engine.adapter = carEngineAdapter
        view.gear.adapter = carGearAdapter
        view.color.adapter = carColorAdapter
        view.fuel.adapter = carFuelAdapter
        view.traction.adapter = carTractionAdapter

        view.carType.onItemSelectedListener = setOnItemSelected(carTypeList, "carType")
        view.engine.onItemSelectedListener = setOnItemSelected(carEngineList, "carEngine")
        view.gear.onItemSelectedListener = setOnItemSelected(carGearList, "carGear")
        view.color.onItemSelectedListener = setOnItemSelected(carColorList, "carColor")
        view.fuel.onItemSelectedListener = setOnItemSelected(carFuelList, "carFuel")
        view.traction.onItemSelectedListener = setOnItemSelected(carTractionList, "carTraction")


    }

    private fun getCategory(): List<CategoryModel> {
        _categoryList = conSQL.getCategory()
        return _categoryList!!
    }

    private fun getCity() = conSQL.getCity()

    private fun getProductByCity(): List<Product> {
        _randomListCity = conSQL.getProductByCity(user?.cityId.toString())
        return _randomListCity!!
    }

    private suspend fun getProductByRandom(size: Int = 20): List<Product> {
        _randomListProduct = conSQL.getProductByRandom(size, user?.id)
        return _randomListProduct!!
    }

    private var concatAdapter: ConcatAdapter? = null


    private fun initializeConcatAdapter() = GlobalScope.launch {

        categoryList = _categoryList ?: getCategory()
        randomListCity = _randomListCity ?: getProductByCity()
        randomListProduct = _randomListProduct ?: getProductByRandom()

        randomProductAdapter.updateList(randomListProduct)
        showCaseAdapter.updateList(randomListCity)
        categoryAdapter.updateList(categoryList)

        isUpdate.postValue(true)

    }

    private fun setOnItemSelected(
        match: List<Any>,
        filter: String
    ): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (filter) {
                    "carType" -> {
                        filterChooseCarType = (match as List<String>)[p2]
                    }
                    "carEngine" -> {
                        filterChooseCarEngine = (match as List<String>)[p2]
                    }
                    "carGear" -> {
                        filterChooseCarGear = (match as List<String>)[p2]
                    }
                    "carColor" -> {
                        filterChooseCarColor = (match as List<String>)[p2]
                    }
                    "carFuel" -> {
                        filterChooseCarFuel = (match as List<String>)[p2]
                    }
                    "carTraction" -> {
                        filterChooseCarTraction = (match as List<String>)[p2]
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun getArrayAdapter(list: List<String>): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list
        )
    }


    private fun setAdapter() {
        concatAdapter = ConcatAdapter(
            categoryAdapter,
            showCaseHeaderAdapter,
            showCaseAdapter,
            HeaderAdapter("Sizin için önerilenler"),
            randomProductAdapter
        )

        binding.homeListRecycler.apply {
            adapter = concatAdapter
        }
    }

    // kategori tıklanma olayı
    private fun itemClickCategory(position: Int) {
        findNavController().navigate(
            HomeScreenDirections.actionHomeScreenToAllProductFragment(
                id = categoryList[position].id!!,
                type = "category",
                header = categoryList[position].name
            )
        )

    }

    // sehire ait tüm ilanları göserme
    private fun itemClickShowCaseAll() {
        findNavController().navigate(
            HomeScreenDirections.actionHomeScreenToAllProductFragment(
                id=user?.cityId!!,
                type = "city-all",
                header = user?.city.toString()
            )
        )

    }

    // tıklanan vitrin ilanını gösterme
    private fun itemClickShowCase(position: Int) {
        findNavController().navigate(
            HomeScreenDirections.actionHomeScreenToProductDetailFragment(randomListProduct[position])
        )

    }

    // random ilana tıklanma
    private fun itemClickRandomProduct(position: Int) {

        findNavController().navigate(
            HomeScreenDirections.actionHomeScreenToProductDetailFragment(randomListProduct[position])
        )

    }

}