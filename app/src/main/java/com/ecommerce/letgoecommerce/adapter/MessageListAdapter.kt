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
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.TMessage
import com.ecommerce.letgoecommerce.sql.ConSQL
import de.hdodenhof.circleimageview.CircleImageView

class MessageListAdapter(
    val itemClick: (position: Int) -> Unit
) :
    RecyclerView.Adapter<MessageListAdapter.MyMessageViewHolder>() {

    private val myMessageList = ArrayList<TMessage>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageViewHolder {
        return MyMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_list_design, parent, false), itemClick
        )
    }

    override fun onBindViewHolder(holder: MyMessageViewHolder, position: Int) {
        holder.bind(myMessageList[position])
    }

    override fun getItemCount() = myMessageList.size

    fun updateList(list: List<TMessage>) {
        myMessageList.clear()
        myMessageList.addAll(list)
        notifyItemRangeChanged(0, myMessageList.size)
    }

    class MyMessageViewHolder(
        val view: View,
        val itemClick: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.date)
        val lastMessage: TextView = view.findViewById(R.id.last_message)
        val name: TextView = view.findViewById(R.id.name)
        val photo: ImageView = view.findViewById(R.id.photo)
        val product_photo: ImageView = view.findViewById(R.id.product_photo)
        val click: ConstraintLayout = view.findViewById(R.id.click)
        val cnq = ConSQL()

        init {
            click.setOnClickListener { itemClick(adapterPosition) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(message: TMessage) {

            if (message.user?.photo_url == null)
                photo.setImageResource(R.drawable.ic_account)
            else
                photo.setImageBitmap(convertImagetoBitmap(message.user?.photo_url))


            date.text = message.messageList?.get(message.messageList?.size!! - 1)!!.created_date
            lastMessage.text = message.messageList?.get(message.messageList?.size!! - 1)!!.message
            name.text = "${message.user?.name} ${message.user?.surname}"

            val bitmapStr = cnq.getProductImage(message.product?.id!!)
            if (bitmapStr != null) {
                product_photo.setImageBitmap(convertImagetoBitmap(bitmapStr))
            }

        }


    }

}


