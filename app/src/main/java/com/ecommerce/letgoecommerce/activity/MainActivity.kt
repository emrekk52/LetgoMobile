package com.ecommerce.letgoecommerce.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.ecommerce.letgoecommerce.R
import com.ecommerce.letgoecommerce.databinding.ActivityMainBinding
import com.ecommerce.letgoecommerce.extension.SpecialShared
import com.ecommerce.letgoecommerce.model.CategoryModel
import com.ecommerce.letgoecommerce.model.Product
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var sp: SpecialShared


    companion object {
        var bottomBar: SmoothBottomBar? = null

        @SuppressLint("StaticFieldLeak")
        var _context: Context? = null

          var _randomListProduct: List<Product>?=null
          var _randomListCity: List<Product>?=null
          var _categoryList: List<CategoryModel>?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _context = this


        initSpecialShared()
        initializeStartScreen()
        setupSmoothBottomMenu()


    }


    private fun initSpecialShared() {
        sp = SpecialShared(_context!!)

    }

    private fun initializeStartScreen() {

        bottomBar = binding.bottomBar

        val host = findNavController(R.id.fragmentView)
        val navGraph = host.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(
            if (sp.getUserId() != -1 && sp.getUserId() != null)
                R.id.homeScreen.also { bottomBar?.visibility = View.VISIBLE }
            else
                R.id.loginScreen
        )

        host.graph = navGraph

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentView) as NavHostFragment
        navController = navHostFragment.navController
    }


    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.custom_bottom_nav_menu)
        val menu = popupMenu.menu
        bottomBar!!.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}