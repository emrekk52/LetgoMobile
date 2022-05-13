package com.ecommerce.letgoecommerce.pages

import android.R
import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.letgoecommerce.activity.EditProductActivity
import com.ecommerce.letgoecommerce.adapter.CategoryAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentBuyAdvertScreenBinding
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.extension.startIntent
import com.ecommerce.letgoecommerce.model.CategoryModel
import com.ecommerce.letgoecommerce.sql.ConSQL
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BuyAdvertScreen : BottomSheetDialogFragment() {

    private var _binding: FragmentBuyAdvertScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var conSQL: ConSQL

    val REQUEST_CODE_GALLERY = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyAdvertScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private lateinit var list: List<CategoryModel>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conSQL = ConSQL()

        initializeRecycler()
    }

    fun initializeRecycler() {
        list = conSQL.getCategory()
        val adapter = CategoryAdapter(::itemClick, isLargeWidth = true)
        adapter.updateList(list)
        binding.recylcer.adapter = adapter
        binding.recylcer.layoutManager = GridLayoutManager(requireContext(), 3)

    }

    var categoryId: Int = 0

    fun itemClick(position: Int) {

        categoryId = list[position].id!!
        galleryPermissionCheck()

    }


    fun galleryPermissionCheck() {


        val requestList = ArrayList<String>()

        val permissionState = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionState) {
            requestList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (requestList.size == 0) {
            openGallery()
        } else
            requireActivity().requestPermissions(requestList.toTypedArray(), REQUEST_CODE_GALLERY)

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
                    val uriList = arrayListOf<Uri>()
                    for (i in 0 until clipData.itemCount) {
                        val uri: Uri = clipData.getItemAt(i).uri
                        uriList.add(uri)
                    }

                    requireActivity().startIntent(
                        EditProductActivity::class.java,
                        "urilist",
                        uriList,
                        key2 = "categoryId",
                        value2 = categoryId
                    )
                } else if (data != null) {
                    requireActivity().startIntent(
                        EditProductActivity::class.java,
                        key = "uri",
                        value = data,
                        key2 = "categoryId",
                        value2 = categoryId
                    )

                }

                dismiss()

            } else
                showToast("İşlem reddedildi")
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var tumuOnaylandi = true

        for (gr in grantResults) {
            if (gr != PackageManager.PERMISSION_GRANTED) {
                tumuOnaylandi = false
                break
            }
        }

        if (!tumuOnaylandi) {

            var tekrarGosterme = false

            for (permission in permissions) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                ) {

                    //reddedildi

                } else if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //onaylandı
                    openGallery()
                } else {

                    // tekrar gösterme
                    tekrarGosterme = true
                    break
                }

            }

            if (tekrarGosterme) {
                AlertDialog.Builder(requireContext())
                    .setTitle("İzin gerekli")
                    .setMessage("ayarlara giderek tüm izinleri onaylayınız")
                    .setPositiveButton("Ayarlar") { dialog, which ->
                        ayarlarAc()
                    }.setNegativeButton("Vazgeç", null).show()
            }

        } else {

            when (requestCode) {

                REQUEST_CODE_GALLERY -> showToast("Galeri izni verildi")

            }

        }

    }

    private fun ayarlarAc() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)

    }


}