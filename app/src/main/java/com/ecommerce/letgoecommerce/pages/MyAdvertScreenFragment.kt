package com.ecommerce.letgoecommerce.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ecommerce.letgoecommerce.adapter.MyProductListAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentMyAdvertScreenBinding
import com.ecommerce.letgoecommerce.extension.SpecialShared
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.sql.ConSQL
import java.text.FieldPosition

class MyAdvertScreen : Fragment() {

    private var _binding: FragmentMyAdvertScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var conSQL: ConSQL
    private lateinit var sp: SpecialShared


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAdvertScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conSQL = ConSQL()
        sp = SpecialShared()
        initializeTitle()


    }

    override fun onResume() {
        super.onResume()
        getMyProduct()
        binding.progress.visibility = View.GONE
    }

    private fun initializeTitle() {
        binding.topBar.viewName.text = "İlanlarım"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var prList: List<Product>

    private fun getMyProduct() {

        prList = conSQL.getProduct(sp.getUserId()!!)

        val adapter = MyProductListAdapter(::itemClick)
        adapter.updateList(prList)
        binding.productListRecycler.adapter = adapter


    }

    private fun itemClick(position: Int) {
        println(position)
        findNavController().navigate(
            MyAdvertScreenDirections.actionMyAdvertScreenToProductDetailFragment(prList[position])
        )

    }

}