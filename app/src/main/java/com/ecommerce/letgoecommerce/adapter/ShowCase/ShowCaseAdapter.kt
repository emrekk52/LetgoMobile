package com.ecommerce.letgoecommerce.adapter.ShowCase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.databinding.ShowcaseAdDesignBinding
import com.ecommerce.letgoecommerce.extension.load
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.ShowCaseModel

class ShowCaseAdapter(private val imageClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<ShowCaseAdapter.ShowCaseItemDesign>() {

    private val showCaseList = ArrayList<Product>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCaseItemDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemShowCaseBinding = ShowcaseAdDesignBinding.inflate(layoutInflater, parent, false)
        return ShowCaseItemDesign(itemShowCaseBinding)
    }

    override fun onBindViewHolder(holder: ShowCaseItemDesign, position: Int) {
        holder.bind(showCaseList[position], position, imageClick)
    }

    class ShowCaseItemDesign(private var itemShowCaseBinding: ShowcaseAdDesignBinding) :
        RecyclerView.ViewHolder(itemShowCaseBinding.root) {

        fun bind(product: Product, position: Int, imageClick: (position: Int) -> Unit) {

            with(itemShowCaseBinding) {

                imageCardClick.setOnClickListener { imageClick(position) }

                if (product.bitmapList?.size!! > 0)
                    image.setImageBitmap(product.bitmapList?.get(0))

            }
        }
    }

    override fun getItemCount(): Int = showCaseList.size

    fun updateList(list: List<Product>) {
        showCaseList.clear()
        showCaseList.addAll(list)
        notifyItemRangeChanged(0, showCaseList.size - 1)
    }

}