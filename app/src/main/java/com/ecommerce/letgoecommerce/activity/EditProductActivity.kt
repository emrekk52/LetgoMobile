package com.ecommerce.letgoecommerce.activity

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ecommerce.letgoecommerce.adapter.EditPhotoListAdapter
import com.ecommerce.letgoecommerce.databinding.ActivityEditProductBinding
import com.ecommerce.letgoecommerce.extension.*
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carColorList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carEngineList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carFuelList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carGearList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carTractionList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carTypeList
import com.ecommerce.letgoecommerce.extension.Constant.Companion.carYearList
import com.ecommerce.letgoecommerce.model.CarExtension
import com.ecommerce.letgoecommerce.model.DbResult
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.sql.ConSQL
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private var categoryId = 0
    private lateinit var sp: SpecialShared
    private var uriList: ArrayList<Uri>? = null
    private lateinit var adapter: EditPhotoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        conSQL = ConSQL()
        sp = SpecialShared()
        getPhotos()
        getCategory()
        setOnCliks()


    }

    fun getPhotos() {

        uriList =
            if (intent.getParcelableArrayListExtra<Uri>("urilist") == null) arrayListOf<Uri>(intent.data!!) else intent.getParcelableArrayListExtra<Uri>(
                "urilist"
            )


        categoryId = intent.getIntExtra("categoryId", 0)


        if (categoryId == 13)
            initializeCarEdittext()

        if (uriList != null) {

            adapter = EditPhotoListAdapter(::addPhoto)
            if (uriList?.size!! < 10)
                uriList?.add(Uri.parse(""))
            adapter.updateList(uriList!!)
            binding.photoRecycler.adapter = adapter

        }


    }

    private fun addPhoto() {

        openGallery()

    }


    private fun openGallery() {


        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startForUploadImageResult.launch(intent)


    }


    private val startForUploadImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {


            if (it.resultCode == Activity.RESULT_OK) {

                val clipData = it.data?.clipData
                val data = it.data?.data

                if (clipData != null) {
                    uriList?.removeAt(uriList?.size!! - 1)
                    for (i in 0 until clipData.itemCount) {
                        val uri: Uri = clipData.getItemAt(i).uri
                        uriList?.add(uri)
                    }
                    if (uriList?.size!! < 10)
                        uriList?.add(Uri.parse(""))
                    adapter.updateList(uriList!!)

                } else if (data != null) {
                    uriList?.removeAt(uriList?.size!! - 1)
                    uriList?.add(data)
                    if (uriList?.size!! < 10)
                        uriList?.add(Uri.parse(""))
                    adapter.updateList(uriList!!)
                }


            } else
                showToast("İşlem reddedildi")
        }


    private lateinit var conSQL: ConSQL
    private fun getCategory() {


        val category = conSQL.getCategoryId(categoryId)
        binding.topBar.viewName.text = category?.name

    }

    private fun setOnCliks() {
        binding.productScreen.apply {

            createProductButton.setOnClickListener {


                if (editProductHeader.text.toString()
                        .isNotEmpty() && editProductPrice.text.toString().isNotEmpty()
                ) {

                    if (categoryId == 13) {
                        val km = binding.productScreen.editCarKm.text.toString().trim()
                        val carType = binding.productScreen.editCarType.text.toString().trim()
                        val carEngine = binding.productScreen.editCarEngine.text.toString().trim()
                        val carColor = binding.productScreen.editCarColor.text.toString().trim()
                        val carGear = binding.productScreen.editCarGear.text.toString().trim()
                        val carYear = binding.productScreen.editCarModel.text.toString().trim()
                        val carFuel = binding.productScreen.editCarFuel.text.toString().trim()
                        val carTraction =
                            binding.productScreen.editCarTraction.text.toString().trim()

                        val carExtension = CarExtension(
                            km = km.toInt(),
                            carGear = carGear,
                            carFuel = carFuel,
                            carEngine = carEngine,
                            carColor = carColor,
                            carType = carType,
                            carModel = carYear.toInt(),
                            carTraction = carTraction
                        )

                        if (km.isNotEmpty() && carType.isNotEmpty() && carEngine.isNotEmpty() && carColor.isNotEmpty() && carGear.isNotEmpty() &&
                            carYear.isNotEmpty() && carFuel.isNotEmpty() && carTraction.isNotEmpty()
                        )
                            addProduct(carExtension)
                        else
                            showToast("Tüm alanların doldurulması zorunludur")

                    } else {
                        addProduct()
                    }


                } else {

                    showToast("Başlık ve Fiyat bilgisi boş bırakılamaz!")

                }


            }


        }
    }

    private fun addProduct(carExtension: CarExtension? = null) {

        val _header = binding.productScreen.editProductHeader.text.toString()
        val _price = binding.productScreen.editProductPrice.getNumericValue().toFloat()
        val _description = binding.productScreen.editProductDescription.text.toString()


        uriList?.removeAt(uriList?.size!! - 1)
        val product = Product(
            categoryId = categoryId,
            userId = sp.getUserId()!!,
            header = _header,
            description = _description,
            price = _price,
            pictureList = uriList?.shuffled()
        )
        var result: Deferred<DbResult>? = null

        if (product.categoryId == 13 && carExtension != null)
            product.carExtension = carExtension


        CoroutineScope(Dispatchers.IO).launch {
            result = conSQL.addProduct(this@EditProductActivity, product)
        }

        CoroutineScope(Dispatchers.Main).launch {
            showToast(result?.await()?.message.toString(), true)
            finish()
        }


    }

    private fun initializeCarEdittext() = binding.productScreen.apply {
        val list = mapOf<TextInputLayout, List<String>?>(
            editCarColorLayout to carColorList,
            editCarEngineLayout to carEngineList,
            editCarFuelLayout to carFuelList,
            editCarGearLayout to carGearList,
            editCarModelLayout to carYearList,
            editCarTractionLayout to carTractionList,
            editCarTypeLayout to carTypeList,
            editKmLayout to null
        )

        val spinnerList = listOf<AutoCompleteTextView>(
            editCarColor,
            editCarEngine,
            editCarFuel,
            editCarGear,
            editCarModel,
            editCarTraction,
            editCarType
        )


        var i = 0
        list.forEach {
            it.key.visibility = View.VISIBLE
            if (it.value != null) {
                val adapter = ArrayAdapter<String>(
                    this@EditProductActivity, R.layout.simple_spinner_dropdown_item, it.value!!
                )
                spinnerList[i++].setAdapter(adapter)
            }
        }

    }


}