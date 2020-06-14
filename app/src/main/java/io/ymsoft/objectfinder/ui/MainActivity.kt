package io.ymsoft.objectfinder.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ActivityMainBinding
import io.ymsoft.objectfinder.util.SingleClickListener
import io.ymsoft.objectfinder.util.hideKeyboard
import io.ymsoft.objectfinder.util.setOnSingleClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navStorageList,
                R.id.navSearch,
                R.id.navAddStorage,
                R.id.navStorageDetail,
                R.id.navSettings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController)
//        binding.bottomAppBar.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, _, _ ->
            hideKeyboard()
        }

        binding.fab.setOnSingleClickListener {
            val extra = FragmentNavigatorExtras(binding.fab to binding.fab.transitionName)
            navController.navigate(R.id.action_global_navAddStorage, null, null, extra)
        }

        initBottomAppBar()

    }

    fun setAppBarVisible(top: Boolean, bottom: Boolean, search: Boolean) {
        if (top) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }

        if (bottom) {
            binding.bottomAppBar.hideOnScroll = true
            binding.bottomAppBar.apply {
                postDelayed({
                    performShow()
                    binding.fab.show()
                }, 400)
            }
        } else {
            binding.bottomAppBar.hideOnScroll = false
            binding.bottomAppBar.apply {
                performHide()
                binding.fab.hide()
                postDelayed({
                    performHide()
                    binding.fab.hide()
                }, 300)
            }
        }

        if (search) {
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.isFocusable = true
            binding.searchView.requestFocus()
        } else {
            binding.searchView.visibility = View.GONE
            binding.searchView.isFocusable = false
        }

    }

    private fun initBottomAppBar() {
        binding.bottomAppBar.setNavigationOnClickListener(SingleClickListener({
            val f =
                BottomNavigationDrawerFragment()
            f.show(supportFragmentManager, f.tag)
        }))

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> navController.navigate(R.id.action_global_navSearch)
//                R.id.action_settings -> setAppBarVisivle(false, false)
            }

            false
        }
    }

    fun replaceBottomMenu(@MenuRes newMenu: Int) {
        binding.bottomAppBar.replaceMenu(newMenu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
