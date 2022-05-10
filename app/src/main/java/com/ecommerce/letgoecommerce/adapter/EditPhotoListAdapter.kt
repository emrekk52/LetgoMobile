package com.ecommerce.letgoecommerce.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.R

class EditPhotoListAdapter(val addPhoto: () -> Unit) :
    RecyclerView.Adapter<EditPhotoListAdapter.UriViewHolder>() {

    private val uriList = ArrayList<Uri>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UriViewHolder {
        return UriViewHolder(

            LayoutInflater.from(parent.context).inflate(
                R.layout.product_photo_list_design,
                parent,
                false
            ), addPhoto
        )
    }

    override fun onBindViewHolder(holder: UriViewHolder, position: Int) {
        holder.bind(uriList[position])
    }

    override fun getItemCount() = uriList.size

    fun updateList(list: List<Uri>) {
        uriList.clear()
        uriList.addAll(list)
        notifyItemRangeChanged(0, uriList.size - 1)
    }

    class UriViewHolder(view: View, val addClick: () -> Unit) :
        RecyclerView.ViewHolder(view) {

        val image = view.findViewById<ImageView>(R.id.image)


        fun bind(uri: Uri) {

            if (uri.toString() != "") {
                image.setImageURI(uri)
                image.setOnClickListener(null)
            } else {
                image.setImageResource(R.drawable.ic_add)
                image.setOnClickListener { addClick() }
            }

        }


    }

}


