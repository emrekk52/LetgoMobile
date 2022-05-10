package com.ecommerce.letgoecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.model.CategoryModel
import com.ecommerce.letgoecommerce.model.ShowCaseModel
import de.hdodenhof.circleimageview.CircleImageView

class CategoryAdapter(val itemClick: (position: Int) -> Unit, val isLargeWidth: Boolean = false) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val _categoryList = ArrayList<CategoryModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(

            LayoutInflater.from(parent.context).inflate(
                if (!isLargeWidth) R.layout.category_design else R.layout.category_grid_design,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(_categoryList[position], itemClick)
    }

    override fun getItemCount() = _categoryList.size

    fun updateList(list: List<CategoryModel>) {
        _categoryList.clear()
        _categoryList.addAll(list)
        notifyItemRangeChanged(0, _categoryList.size - 1)
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageBackground = view.findViewById<CircleImageView>(R.id.imageBackground)
        val image = view.findViewById<ImageView>(R.id.image)
        val name = view.findViewById<TextView>(R.id.name)
        val click = view.findViewById<ConstraintLayout>(R.id.click)

        fun bind(model: CategoryModel, itemClick: (position: Int) -> Unit) {


            if (model.color != null && model.image != null) {
                imageBackground.setImageResource(model.color!!)
                image.setImageResource(model.image!!)
            }
            name.text = model.name

            click.setOnClickListener { itemClick(adapterPosition) }

        }


    }

}


