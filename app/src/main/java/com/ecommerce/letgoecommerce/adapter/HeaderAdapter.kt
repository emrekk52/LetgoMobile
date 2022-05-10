package com.ecommerce.letgoecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.databinding.ItemHeaderBinding

class HeaderAdapter(
    private val header: String,
    private val action: String? = null,
    private val itemClick: (() -> Unit)? = null
) :
    RecyclerView.Adapter<HeaderAdapter.HeaderDesign>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderDesign {
        val layoutInflater = LayoutInflater.from(parent.context)
        val headerBinding = ItemHeaderBinding.inflate(layoutInflater, parent, false)
        return HeaderDesign(headerBinding)
    }

    override fun onBindViewHolder(holder: HeaderDesign, position: Int) {
        holder.bind(header, action, itemClick)
    }

    class HeaderDesign(private var headerBinding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(headerBinding.root) {

        fun bind(header: String, action: String?, itemClick: (() -> Unit)?) {


            with(headerBinding) {
                tvHeader.text = header



                if (action != null)
                    tvAction.text = action
                else
                {
                    actionButton.visibility= View.GONE
                }

                if (itemClick != null)
                    actionButton.setOnClickListener { itemClick() }
            }


        }

    }

    override fun getItemCount(): Int = 1
}