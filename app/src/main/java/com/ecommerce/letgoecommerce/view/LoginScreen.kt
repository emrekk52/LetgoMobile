package com.ecommerce.letgoecommerce.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.ecommerce.letgoecommerce.activity.MainActivity.Companion._context
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.activity.MainActivity
import com.ecommerce.letgoecommerce.databinding.FragmentLoginScreenBinding
import com.ecommerce.letgoecommerce.extension.SpecialShared
import com.ecommerce.letgoecommerce.extension.showToast
import com.ecommerce.letgoecommerce.model.City
import com.ecommerce.letgoecommerce.model.User
import com.ecommerce.letgoecommerce.sql.ConSQL

class LoginScreen : Fragment() {

    private var _binding: FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!
    lateinit var sp: SpecialShared

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeShared()
        initializeCityAdapter()
        initializeOnClick()
    }

    private var chooseCityId = -1
    private fun initializeCityAdapter() {
        val conSQL = ConSQL()
        val list = conSQL.getCity()
        val _list = list.map { it.name }
        val adapter = ArrayAdapter<String>(
            _context!!, android.R.layout.simple_spinner_dropdown_item, _list
        )
        binding.editCity.setAdapter(adapter)

        binding.editCity.setOnItemClickListener { _, _, position, _ ->
            chooseCityId = list.first {
                it.name.trim().lowercase() == binding.editCity.text.toString().trim().lowercase()
            }.id

        }


    }

    private fun initializeShared() {
        sp = SpecialShared()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.bottomBar?.visibility = View.VISIBLE

    }

    private fun initializeOnClick() {

        binding.signUpButton.setOnClickListener {

            if (binding.editNameLayout.visibility == View.GONE) {
                binding.loginButton.visibility = View.GONE
                binding.editNameLayout.visibility = View.VISIBLE
                binding.editSurnameLayout.visibility = View.VISIBLE
                binding.editCityLayout.visibility = View.VISIBLE
                binding.back.visibility = View.VISIBLE
                binding.loginText.text = getString(R.string.signUpText)
            } else {

                // kayıt olma işlemi
                signUp()

            }

        }

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.back.setOnClickListener {

            binding.loginText.text = getString(R.string.loginText)
            binding.loginButton.visibility = View.VISIBLE

            binding.editNameLayout.visibility = View.GONE
            binding.editSurnameLayout.visibility = View.GONE
            binding.editCityLayout.visibility = View.GONE
            binding.back.visibility = View.GONE
        }

    }

    private fun login() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
            val result = ConSQL().login(email, password)

            if (result.state) {
                showToast(result.message)
                sp.setCityId(chooseCityId)
                findNavController().navigate(
                    LoginScreenDirections.actionLoginScreenToHomeScreen()
                )
            } else {
                showToast(result.message)
            }

        } else
            showToast("Tüm alanların doldurulması zorunludur!")


    }

    private fun signUp() {

        val name = binding.editName.text.toString()
        val surname = binding.editSurname.text.toString()
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()



        if (name.trim().isNotEmpty() && surname.trim().isNotEmpty() && email.trim()
                .isNotEmpty() && password.isNotEmpty() && chooseCityId != -1
        ) {
            val user = User(
                name = name,
                surname = surname,
                email = email,
                password = password,
                cityId = chooseCityId
            )
            val result = ConSQL().signUp(user)
            if (result.state) {
                showToast(result.message)

                sp.setCityId(chooseCityId)
                findNavController().navigate(
                    LoginScreenDirections.actionLoginScreenToHomeScreen()
                )
            } else
                showToast(result.message)


        } else
            showToast("Tüm alanların doldurulması zorunludur!")


    }


}