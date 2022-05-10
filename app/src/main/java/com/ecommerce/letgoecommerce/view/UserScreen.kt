package com.ecommerce.letgoecommerce.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion.bottomBar
import com.ecommerce.letgoecommerce.adapter.RandomProduct.RandomProductAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentUserScreenBinding
import com.ecommerce.letgoecommerce.extension.*
import com.ecommerce.letgoecommerce.model.Image
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.User
import com.ecommerce.letgoecommerce.sql.ConSQL
import com.github.dhaval2404.imagepicker.ImagePicker


class UserScreen : Fragment() {

    private val args: UserScreenArgs by navArgs()
    private var _binding: FragmentUserScreenBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null

    private lateinit var conSQL: ConSQL
    private lateinit var sp: SpecialShared
    private lateinit var list: List<Product>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserScreenBinding.inflate(layoutInflater, container, false)
        bottomBar?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conSQL = ConSQL()
        sp = SpecialShared()


        initializeUserInfo()
        initializeClick()
    }

    private fun initializeClick() {
        binding.apply {

            if (args.userId == -1) {
                image.setOnClickListener { openGalleryOrCamera() }
                signOut.setOnClickListener {
                    signout()
                }
            }

        }

    }


    private fun signout() {
        sp.setUserId(-1)
        findNavController().navigate(
            UserScreenDirections.actionUserScreenToLoginScreen()
        )
        showToast("Çıkış yapıldı")
    }

    private fun openGalleryOrCamera() {


        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { startForProfileImageResult.launch(it) }


    }

    private fun getProductById() = conSQL.getProduct(args.userId)


    private fun uploadPhoto(uri: Uri) {


        val image = Image()
        image.photo_url = convertBitmaptoBase64(requireContext(),uri)
        image.uid = user?.id!!
        image.description = "profile"

        conSQL.uploadPhoto(image)

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                binding.image.setImageURI(fileUri)
                uploadPhoto(fileUri)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showToast(ImagePicker.getError(data))
            } else {
                showToast("İşlem reddedildi")
            }
        }


    @SuppressLint("SetTextI18n")
    private fun initializeUserInfo() {

        binding.topBar.viewName.text = "Profilim"

        if (args.userId == -1) {
            user = conSQL.getUserInfo()
        } else {
            user = conSQL.getUserInfo(args.userId)
            binding.topBar.viewName.text = "${user?.name} ${user?.surname}"
            binding.signOut.visibility = View.GONE
            list = getProductById()
            val adapter = RandomProductAdapter(itemClick)
            adapter.updateList(list)
            binding.recycler.layoutManager=GridLayoutManager(requireContext(),2)
            binding.recycler.adapter=adapter
        }

        binding.name.text = user?.name +" "+ user?.surname
        binding.city.text = user?.city

        if (user?.photo_url != null)
            binding.image.setImageBitmap(convertImagetoBitmap(user?.photo_url))
        else
            binding.image.setImageResource(R.drawable.ic_account)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        if (sp.getUserId() != -1)
            bottomBar?.visibility = View.VISIBLE

    }

    val itemClick : (Int) ->Unit= {

        findNavController().navigate(
            UserScreenDirections.actionUserScreenToProductDetailFragment(list[it])
        )


    }


}