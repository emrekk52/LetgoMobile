package com.ecommerce.letgoecommerce.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion.bottomBar
import com.ecommerce.letgoecommerce.adapter.RandomProduct.RandomProductAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentSearchScreenBinding
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.sql.ConSQL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchScreen : Fragment() {

    private val args: SearchScreenArgs by navArgs()

    private var _binding: FragmentSearchScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RandomProductAdapter

    private var list = arrayListOf<Product>()

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var conSQL: ConSQL

    private var isUpdate = MutableLiveData<Boolean>()
    private var _search = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        bottomBar?.visibility = View.GONE
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        conSQL = ConSQL()
        setOnClicks()
        setAdapter()

        if (args.filter != null)
            getFilterProduct()

        isUpdate.observe(viewLifecycleOwner) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.updateList(list)
                    adapter.notifyItemRangeChanged(0, list.size)

                    if (list.size > 0)
                        binding.notFoundLayout.visibility = View.GONE
                    else {
                        binding.errorText.text =
                            if (args.filter == null) "'$_search' ile ilgili ürün bulunamadı" else
                                "Filtrelenen sonuçlara göre böyle bir ürün bulunamadı"
                        binding.notFoundLayout.visibility = View.VISIBLE
                    }
                }
            }


        }
    }

    private fun setAdapter() {
        adapter = RandomProductAdapter {
            findNavController().navigate(
                SearchScreenDirections.actionSearchScreenToProductDetailFragment(list[it])
            )
        }

        adapter.updateList(list)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycler.adapter = adapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomBar?.visibility = View.VISIBLE
    }


    private fun setOnClicks() = binding.apply {
        if (args.filter == null) {
            binding.search.requestFocus()
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                0
            )
        } else {
            binding.editSearchLayout.visibility = View.GONE
        }


        backButton.setOnClickListener {


            val windowHeightMethod =
                InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
            val height = windowHeightMethod.invoke(inputMethodManager) as Int

            if (height > 0) {
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                return@setOnClickListener
            }
            findNavController().navigateUp()

        }

        binding.search.addTextChangedListener {
            _search = it.toString().trim()
            binding.searchButton.visibility =
                if (_search.isNotEmpty()) View.VISIBLE else View.GONE.also {
                    list.clear()
                    adapter.updateList(list)
                    adapter.notifyDataSetChanged()
                    binding.errorText.text = requireContext().getString(R.string.notFoundText)
                    binding.notFoundLayout.visibility = View.VISIBLE
                }


        }

        binding.searchButton.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            getProductBySearch()
        }

    }


    private fun getProductBySearch() {

        CoroutineScope(Dispatchers.IO).launch {
            list = conSQL.getProductBySearch(_search).await()
            isUpdate.postValue(true)
        }

    }

    private fun getFilterProduct() {
        CoroutineScope(Dispatchers.IO).launch {
            list = conSQL.getFilterProduct(args.filter).await()
            isUpdate.postValue(true)
        }
    }


}