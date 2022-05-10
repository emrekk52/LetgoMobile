package com.ecommerce.letgoecommerce.adapter.ShowCase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.databinding.GeneralRecyclerBinding
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.ShowCaseModel

class ShowCaseListAdapter(private val imageClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<ShowCaseListAdapter.ShowCaseListDesign>() {


    private val showCaseList = ArrayList<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCaseListDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listShowCaseBinding = GeneralRecyclerBinding.inflate(layoutInflater, parent, false)
        return ShowCaseListDesign(listShowCaseBinding)
    }

    override fun onBindViewHolder(holder: ShowCaseListDesign, position: Int) {
        holder.bind(showCaseList, imageClick)
    }

    class ShowCaseListDesign(private var listShowCaseBinding: GeneralRecyclerBinding) :
        RecyclerView.ViewHolder(listShowCaseBinding.root) {

        fun bind(showCasesList: List<Product>, imageClick: (position: Int) -> Unit) {


            listShowCaseBinding.generalRecycler.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


                adapter = ShowCaseAdapter(imageClick).also {
                    it.updateList(showCasesList)
                }
            }
        }
    }

    override fun getItemCount(): Int = 1

    fun updateList(list: List<Product>) {

        showCaseList.clear()
        showCaseList.addAll(list)


    }


}