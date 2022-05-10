package com.ecommerce.letgoecommerce.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion.bottomBar
import com.ecommerce.letgoecommerce.adapter.RandomProduct.RandomProductAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentAllProductBinding
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.sql.ConSQL

class AllProductFragment : Fragment() {

    private var _binding: FragmentAllProductBinding? = null
    private val binding get() = _binding!!

    private val args: AllProductFragmentArgs by navArgs()
    private lateinit var conSQL: ConSQL

    private lateinit var productList: List<Product>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllProductBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        bottomBar?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conSQL = ConSQL()
        initializeRecyclerAndHeader()

    }

    private fun getProductByCity() = conSQL.getProductByCity(args.id.toString(), all = true)
    private fun getProductByCategory() = conSQL.getProductByCategory(args.id)

    private fun initializeRecyclerAndHeader() {
        binding.topBar.viewName.text = args.header

        when (args.type) {
            "city-all" -> {
                productList = getProductByCity()
                setAdapter(productList)
            }

            "category" -> {
                productList = getProductByCategory()
                setAdapter(productList)
            }


        }
        println()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomBar?.visibility = View.VISIBLE
    }

    private fun setAdapter(list: List<Product>) {


        val manager = GridLayoutManager(requireContext(), 2)
        binding.recycler.layoutManager = manager
        val adapter = RandomProductAdapter(::itemClick)
        adapter.updateList(list)
        binding.recycler.adapter = adapter

    }

    private fun itemClick(position: Int) {

        findNavController().navigate(
            AllProductFragmentDirections.actionAllProductFragmentToProductDetailFragment(productList[position])
        )

    }


}