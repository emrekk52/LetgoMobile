package com.ecommerce.letgoecommerce.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.adapter.MessageListAdapter
import com.ecommerce.letgoecommerce.databinding.FragmentMessageScreenBinding
import com.ecommerce.letgoecommerce.model.Product
import com.ecommerce.letgoecommerce.model.TMessage
import com.ecommerce.letgoecommerce.sql.ConSQL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageScreen : Fragment() {

    private var _binding: FragmentMessageScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MessageListAdapter
    private lateinit var conSQL: ConSQL
    private lateinit var messages: List<TMessage>
    private val isUpdate = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topBar.viewName.text = requireContext().getString(R.string.message_header)
        conSQL = ConSQL()
        _isUpdate()

        binding.swipeRefresh.setOnRefreshListener {
            initializeAdapter()
        }
    }

    override fun onResume() {
        super.onResume()
        initializeAdapter()
    }

    private fun _isUpdate() {

        isUpdate.observe(viewLifecycleOwner) {
            if (it) {
                adapter = MessageListAdapter(::itemClick)
                adapter.updateList(messages)
                binding.recyclerview.adapter = adapter

                if (binding.swipeRefresh.isRefreshing)
                    binding.swipeRefresh.isRefreshing = false
            }

        }

    }

    private fun initializeAdapter() = GlobalScope.launch {
        messages = getMessage().await()
        isUpdate.postValue(true)
    }

    private fun getMessage() = conSQL.getMessageList()


    private fun itemClick(position: Int) {
        findNavController().navigate(
            MessageScreenDirections.actionMessageScreenToChatScreenFragment(messages[position])
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}