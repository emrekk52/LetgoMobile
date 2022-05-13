package com.ecommerce.letgoecommerce.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.extension.Constant.Companion.readyMessageList
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.model.TMessage
import com.ecommerce.letgoecommerce.sql.ConSQL

class ReadyMessageAdapter(
    val itemClick: (position: Int) -> Unit
) :
    RecyclerView.Adapter<ReadyMessageAdapter.MyMessageViewHolder>() {

    private val readyMessages = readyMessageList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageViewHolder {
        return MyMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ready_message_design, parent, false), itemClick
        )
    }

    override fun onBindViewHolder(holder: MyMessageViewHolder, position: Int) {
        holder.bind(readyMessages[position])
    }

    override fun getItemCount() = readyMessages.size


    class MyMessageViewHolder(
        val view: View,
        val itemClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        val _message: TextView = view.findViewById(R.id.readyMessage)

        init {
            _message.setOnClickListener { itemClick(adapterPosition) }
        }


        fun bind(message: String) {

            _message.text = message

        }


    }

}


