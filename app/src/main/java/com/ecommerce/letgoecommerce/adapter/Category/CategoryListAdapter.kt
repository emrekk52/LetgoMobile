package com.ecommerce.letgoecommerce.adapter.Category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.adapter.CategoryAdapter
import com.ecommerce.letgoecommerce.databinding.CategoryDesignBinding
import com.ecommerce.letgoecommerce.databinding.GeneralRecyclerBinding
import com.ecommerce.letgoecommerce.model.CategoryModel

class CategoryListAdapter(private val itemClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryListDesign>() {


    private val categoryList = ArrayList<CategoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listCategoryBinding = GeneralRecyclerBinding.inflate(layoutInflater, parent, false)
        return CategoryListDesign(listCategoryBinding)
    }

    override fun onBindViewHolder(holder: CategoryListDesign, position: Int) {
        holder.bind(categoryList, itemClick)
    }

    class CategoryListDesign(private var listCategoryBinding: GeneralRecyclerBinding) :
        RecyclerView.ViewHolder(listCategoryBinding.root) {

        fun bind(categoriesList: List<CategoryModel>, itemClick: (position: Int) -> Unit) {


            listCategoryBinding.generalRecycler.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                adapter = CategoryAdapter(itemClick).also {
                    it.updateList(categoriesList)
                }
            }
        }
    }

    override fun getItemCount(): Int = 1


    fun updateList(list: List<CategoryModel>) {
        categoryList.clear()
        categoryList.addAll(list)
    }


}