package com.ecommerce.letgoecommerce.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.extension.Constant.Companion.PRODUCT_STATE_ACTIVE
import com.ecommerce.letgoecommerce.model.Product

class MyProductListAdapter(val itemClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<MyProductListAdapter.MyProductViewHolder>() {

    private val myProductList = ArrayList<Product>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(

            LayoutInflater.from(parent.context).inflate(
                R.layout.my_product_design,
                parent,
                false
            ), itemClick
        )
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        holder.bind(myProductList[position])
    }

    override fun getItemCount() = myProductList.size

    fun updateList(list: List<Product>) {
        myProductList.clear()
        myProductList.addAll(list)
        notifyItemRangeChanged(0, myProductList.size - 1)
    }

    class MyProductViewHolder(val view: View, val itemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {

        val image = view.findViewById<ImageView>(R.id.productTitleImage)
        val header = view.findViewById<TextView>(R.id.productHeader)
        val description = view.findViewById<TextView>(R.id.productDescription)
        val clickPr = view.findViewById<ConstraintLayout>(R.id.clickPr)
        val retryActiveProduct = view.findViewById<TextView>(R.id.retryActiveProduct)

        init {
            clickPr.setOnClickListener { itemClick(position) }
        }

        fun bind(product: Product) {


            if (product?.bitmapList?.size!! > 0)
                image.setImageBitmap(product.bitmapList?.get(0))
            header.text = product.header
            description.text = product.description

            if (product.state == PRODUCT_STATE_ACTIVE)
                retryActiveProduct.visibility = View.GONE else
                retryActiveProduct.visibility = View.VISIBLE


        }


    }

}


