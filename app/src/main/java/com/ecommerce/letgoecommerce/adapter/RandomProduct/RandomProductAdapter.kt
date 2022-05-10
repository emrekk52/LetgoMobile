package com.ecommerce.letgoecommerce.adapter.RandomProduct

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.databinding.RandomProductDesignBinding
import com.ecommerce.letgoecommerce.extension.moneyFormatter
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.sql.ConSQL

class RandomProductAdapter(private val itemClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<RandomProductAdapter.RandomProductDesign>() {

    private val randomProductList = ArrayList<Product>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomProductDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val randomProductBinding = RandomProductDesignBinding.inflate(layoutInflater, parent, false)
        return RandomProductDesign(randomProductBinding)
    }

    override fun onBindViewHolder(holder: RandomProductDesign, position: Int) {
        holder.bind(randomProductList[position], position, itemClick)
    }

    class RandomProductDesign(private var randomProductBinding: RandomProductDesignBinding) :
        RecyclerView.ViewHolder(randomProductBinding.root) {

        private var conSql = ConSQL()


        @SuppressLint("SetTextI18n")
        fun bind(product: Product, position: Int, itemClick: (position: Int) -> Unit) {

            with(randomProductBinding) {

                card.setOnClickListener { itemClick(position) }

                category.text = conSql.getCategoryId(product.categoryId)?.name ?: "-"
                city.text = conSql.getUserInfo(product.userId)?.city
                header.text = product.header
                price.text = "₺"+moneyFormatter(product.price)

                if (product.bitmapList?.size!! > 0)
                    image.setImageBitmap(product.bitmapList?.get(0))

            }
        }
    }

    override fun getItemCount(): Int = randomProductList.size

    fun updateList(list: List<Product>) {
        randomProductList.clear()
        randomProductList.addAll(list)
        notifyItemRangeChanged(0, randomProductList.size - 1)
    }

}