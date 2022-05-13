package com.ecommerce.letgoecommerce.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.model.Message
import com.ecommerce.letgoecommerce.model.User

class ChatListAdapter(val myUser: User, val otherUser: User) :
    RecyclerView.Adapter<ChatListAdapter.MyChatViewHolder>() {

    private val myChatList = ArrayList<Message>()
    private val LEFT_MESSAGE = 1
    private val RIGHT_MESSAGE = 0

    override fun getItemViewType(position: Int): Int {
        return if (myUser.id == myChatList[position].sender_id)
            RIGHT_MESSAGE
        else
            LEFT_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatViewHolder {
        return MyChatViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    if (viewType == RIGHT_MESSAGE) R.layout.right_chat_message else R.layout.left_chat_message,
                    parent,
                    false
                ), myUser, otherUser
        )
    }

    override fun onBindViewHolder(holder: MyChatViewHolder, position: Int) {
        if (myUser.id == myChatList[position].sender_id)
            holder.rightMessage(myChatList[position])
        else
            holder.leftMessage(myChatList[position])
    }

    override fun getItemCount() = myChatList.size

    fun updateList(list: List<Message>) {
        myChatList.clear()
        myChatList.addAll(list)
        notifyItemRangeChanged(0, myChatList.size)
    }

    class MyChatViewHolder(
        val view: View,
        private val myUser: User,
        private val otherUser: User,
    ) :
        RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.date)
        val messageText: TextView = view.findViewById(R.id.message)
        val name: TextView = view.findViewById(R.id.name)
        val photo: ImageView = view.findViewById(R.id.photo)


        fun leftMessage(message: Message) {

            if (otherUser.photo_url == null)
                photo.setImageResource(R.drawable.ic_account)
            else
                photo.setImageBitmap(convertImagetoBitmap(otherUser.photo_url))


            date.text = message.created_date?.substring(0,5)
            messageText.text = message.message
            name.text = "${otherUser.name} ${otherUser.surname}"

        }

        fun rightMessage(message: Message) {

            if (myUser.photo_url == null)
                photo.setImageResource(R.drawable.ic_account)
            else
                photo.setImageBitmap(convertImagetoBitmap(myUser.photo_url))


            date.text = message.created_date?.substring(0,5)
            messageText.text = message.message
            name.text = "Sen"

        }


    }

}


