package com.ecommerce.letgoecommerce.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion.bottomBar
import com.ecommerce.letgoecommerce.adapter.ChatListAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentChatScreenBinding
import com.ecommerce.letgoecommerce.extension.convertImagetoBitmap
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.model.Message
import com.ecommerce.letgoecommerce.model.User
import com.ecommerce.letgoecommerce.sql.ConSQL

class ChatScreenFragment : Fragment() {

    private var _binding: FragmentChatScreenBinding? = null
    private val binding get() = _binding!!
    private val args: ChatScreenFragmentArgs by navArgs()
    private lateinit var conSql: ConSQL
    private lateinit var adapter: ChatListAdapter
    private lateinit var myUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conSql = ConSQL()
        myUser = conSql.getUser()!!
        initializeUserInfo()
        initializeChatAdapter()
        binding.back.setOnClickListener { findNavController().navigateUp() }

        sendMessageSetup()
    }

    private fun sendMessageSetup() = binding.apply {
        sendMessage.setOnClickListener {

            if (editTextMessage.text.toString().isNotEmpty()) {
                val message = Message(
                    receiver_id = args.tmessage.user?.id,
                    sender_id = myUser.id,
                    message = editTextMessage.text.toString(),
                    product_id = args.tmessage.product?.id
                )
                val result = conSql.postMessage(message)
                if (result)
                    getMessage().also {
                        binding.editTextMessage.setText("")
                    }
                else
                    showToast("Mesaj gönderilemedi")
            } else
                showToast("Mesaj yazınız!")

        }
    }

    private fun getMessage() {

        args.tmessage.messageList = conSql.getListChatMessage(
            myUser.id!!,
            args.tmessage.user?.id!!,
            args.tmessage.product?.id!!
        )
        adapter.updateList(args.tmessage.messageList!!)
        binding.recycler.scrollToPosition(args.tmessage.messageList!!.size - 1);

        if (binding.swipeRefresh.isRefreshing)
            binding.swipeRefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        bottomBar?.visibility = View.GONE

    }

    private fun initializeChatAdapter() {
        adapter = ChatListAdapter(myUser, args.tmessage.user!!)
        if (args.tmessage.messageList != null) {
            adapter.updateList(args.tmessage.messageList!!)
            binding.recycler.scrollToPosition(args.tmessage.messageList!!.size - 1);
        } else
            getMessage()
        binding.recycler.adapter = adapter

    }

    private fun initializeUserInfo() = binding.apply {

        name.text = args.tmessage.user?.name + " " + args.tmessage.user?.surname
        city.text = args.tmessage.user?.city
        if (args.tmessage.user?.photo_url == null)
            photo.setImageResource(R.drawable.ic_account)
        else
            photo.setImageBitmap(convertImagetoBitmap(args.tmessage.user?.photo_url))

        args.tmessage.product?.bitmapList = conSql.getProductImages(args.tmessage.product?.id!!)
        productPhoto.setImageBitmap(args.tmessage.product?.bitmapList?.get(0))


        productPhoto.setOnClickListener {
            findNavController().navigate(
                ChatScreenFragmentDirections.actionChatScreenFragmentToProductDetailFragment(args.tmessage.product!!)
            )
        }

        photo.setOnClickListener {
            findNavController().navigate(
                ChatScreenFragmentDirections.actionChatScreenFragmentToUserScreen(args.tmessage.user?.id!!)
            )
        }


        swipeRefresh.setOnRefreshListener {
            getMessage()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomBar?.visibility = View.VISIBLE
    }


}