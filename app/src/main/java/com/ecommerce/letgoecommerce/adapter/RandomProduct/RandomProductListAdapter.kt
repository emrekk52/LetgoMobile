package com.ecommerce.letgoecommerce.adapter.RandomProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.databinding.GeneralRecyclerBinding
import com.ecommerce.letgoecommerce.model.Product

class RandomProductListAdapter(private val itemClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<RandomProductListAdapter.RandomProductDesign>() {


    private val randomProductList = ArrayList<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomProductDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listShowCaseBinding = GeneralRecyclerBinding.inflate(layoutInflater, parent, false)
        return RandomProductDesign(listShowCaseBinding)
    }

    override fun onBindViewHolder(holder: RandomProductDesign, position: Int) {
        holder.bind(randomProductList, itemClick)
    }

    class RandomProductDesign(private var listRandomProductBinding: GeneralRecyclerBinding) :
        RecyclerView.ViewHolder(listRandomProductBinding.root) {

        fun bind(randomProductList: List<Product>, itemClick: (position: Int) -> Unit) {


            listRandomProductBinding.generalRecycler.apply {

                layoutManager = GridLayoutManager(context, 2)

                adapter = RandomProductAdapter(itemClick).also {
                    it.updateList(randomProductList)
                }
            }
        }
    }

    override fun getItemCount(): Int = 1

    fun updateList(list: List<Product>) {

        randomProductList.clear()
        randomProductList.addAll(list)


    }


}