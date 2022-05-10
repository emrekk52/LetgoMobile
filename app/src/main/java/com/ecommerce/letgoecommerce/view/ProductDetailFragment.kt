package com.ecommerce.letgoecommerce.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ecommerce.letgoecommerce.R

import com.ecommerce.letgoecommerce.activity.MainActivity.Companion.bottomBar
import com.ecommerce.letgoecommerce.databinding.FragmentProductDetailBinding
import com.ecommerce.letgoecommerce.databinding.ProductXLargePhotoSliderDesignBinding
import com.ecommerce.letgoecommerce.extension.Constant.Companion.PRODUCT_STATE_ACTIVE
import com.ecommerce.letgoecommerce.extension.SpecialShared
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.extension.moneyFormatter
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.imageSlider.`interface`.ItemClickListener
import com.ecommerce.letgoecommerce.imageSlider.model.SlideUIModel
import com.ecommerce.letgoecommerce.model.User
import com.ecommerce.letgoecommerce.sql.ConSQL


class ProductDetailFragment : Fragment() {


    private lateinit var conSQL: ConSQL
    private lateinit var sp: SpecialShared


    private val args: ProductDetailFragmentArgs by navArgs()

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conSQL = ConSQL()
        sp = SpecialShared()
        setOnClicks()
        initializeInfo()
    }

    override fun onResume() {
        super.onResume()
        bottomBar?.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initializeInfo() {
        binding.apply {

            if (args.product.userId != sp.getUserId())
                removeButton.visibility = View.GONE

            val imageList = ArrayList<SlideUIModel>()
            args.product.bitmapList?.forEach { imageList.add(SlideUIModel(it)) }
            if (imageList.size > 0)
                binding.imageSlider.setImageList(imageList)

            binding.imageSlider.setItemClickListener(object:ItemClickListener{
                override fun onItemClick(model: SlideUIModel, position: Int) {
                   openXLargePhoto(imageList)
                }

            })

            price.text = moneyFormatter(args.product.price) + " TL"
            header.text = args.product.header
            description.text = args.product.description
            if (args.product.userId != sp.getUserId()) {
                val user = getUser()
                userCity.text = user?.city
                userName.text = user?.name + " " + user?.surname
                if (user?.photo_url != null)
                    userPhoto.setImageBitmap(convertImagetoBitmap(user.photo_url))
                else
                    userPhoto.setImageResource(R.drawable.ic_account)

                binding.otherUser.setOnClickListener {
                    findNavController().navigate(
                        ProductDetailFragmentDirections.actionProductDetailFragmentToUserScreen(user?.id!!)
                    )
                }
            } else
                binding.otherUser.visibility = View.GONE

            if (args.product.createdDate != null)
                binding.createdDate.text = args.product.createdDate

            statusEndLayout.visibility =
                if (args.product.state == PRODUCT_STATE_ACTIVE) View.GONE else View.VISIBLE

            if (args.product.categoryId == 13) {
                initializeCarExtension()
            }

        }
    }

    private fun openXLargePhoto(imageList: ArrayList<SlideUIModel>) {

        val view = ProductXLargePhotoSliderDesignBinding.inflate(layoutInflater)

        view.imageSlider.setImageList(imageList)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(view.root)
        dialog.show()

    }


    private fun initializeCarExtension() = binding.carExtension.apply {

        root.visibility = View.VISIBLE

        km.text =
            requireActivity().getString(R.string.carKmText) + ": " + args.product.carExtension?.km.toString()
        type.text =
            requireActivity().getString(R.string.carTypeText) + ": " + args.product.carExtension?.carType.toString()
        model.text =
            requireActivity().getString(R.string.carModelText) + ": " + args.product.carExtension?.carModel.toString()
        engine.text =
            requireActivity().getString(R.string.carEngineText) + ": " + args.product.carExtension?.carEngine.toString() + "+"
        gear.text =
            requireActivity().getString(R.string.carGearText) + ": " + args.product.carExtension?.carGear.toString()
        color.text =
            requireActivity().getString(R.string.carColorText) + ": " + args.product.carExtension?.carColor.toString()
        fuel.text =
            requireActivity().getString(R.string.carFuelText) + ": " + args.product.carExtension?.carFuel.toString()
        traction.text =
            requireActivity().getString(R.string.carTractionText) + ": " + args.product.carExtension?.carTraction.toString()

    }

    private fun setOnClicks() {

        binding.apply {

            cancelButton.setOnClickListener { findNavController().navigateUp() }
            removeButton.setOnClickListener { removeProduct() }


        }
    }


    private fun removeProduct() {

        AlertDialog.Builder(requireContext()).setTitle("İlanı kaldırmak mı istiyorsunuz?")
            .setPositiveButton("Evet") { _, _ ->
                val result = conSQL.removeProduct(args.product.id!!)
                showToast(result.message)
                findNavController().navigateUp()
            }.setNegativeButton("Hayır", null).show()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomBar?.visibility = View.VISIBLE
    }


    private fun getUser(): User? = conSQL.getUserInfo(args.product.userId)

}