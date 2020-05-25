package io.ymsoft.objectfinder.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ActivityMainBinding
import io.ymsoft.objectfinder.util.hideKeyboard
import io.ymsoft.objectfinder.util.showKeyboard

class MainActivity : AppCompatActivity(){

    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var searchView : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        searchView = binding.searchView
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navStorageList,
            R.id.navSearch,
            R.id.navAddStorage,
            R.id.navStorageDetail
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController)
//        binding.bottomAppBar.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            hideKeyboard()
            setAppBarByDestination(destination)
        }

        binding.fab.setOnClickListener {
            navController.navigate(R.id.navAddStorage)
        }

        initBottomAppBar()

    }

    /**NavDestination의 값에 따라 툴바와 바텀앱바의 구성을 변경한다.*/
    private fun setAppBarByDestination(destination: NavDestination) {

        when(destination.id){
            R.id.navStorageDetail -> {
                setAppBarVisivle(top = true, bottom = false)
                binding.fab.hide()
            }
            R.id.navSearch -> {
                setAppBarVisivle(top = true, bottom = false)
                binding.searchView.requestFocus()
                showKeyboard()
            }
            R.id.navAddStorage -> {
                setAppBarVisivle(top = true, bottom = false)
            }
            else -> {
                setAppBarVisivle(top = true, bottom = true)
            }
        }


        if(destination.id == R.id.navSearch){
            searchView.visibility = View.VISIBLE
        }
        else {
            searchView.visibility = View.GONE
        }

        // 필요한 경우 destination 마다 다른 메뉴를 설정해 줄 수 있다.
        binding.bottomAppBar.replaceMenu(R.menu.main)
    }

    private fun setAppBarVisivle(top: Boolean, bottom: Boolean) {
        if(top){
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }

        if(bottom){
            binding.bottomAppBar.hideOnScroll = true
            binding.bottomAppBar.performShow()
            binding.fab.show()
        } else {
            binding.bottomAppBar.hideOnScroll = false
            binding.bottomAppBar.performHide()
            binding.fab.hide()
        }

    }

    private fun initBottomAppBar() {
        binding.bottomAppBar.setNavigationOnClickListener { v ->
            val f =
                BottomNavigationDrawerFragment()
            f.show(supportFragmentManager, f.tag)
        }
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            Log.d("", item.toString())
            when(item.itemId){
                R.id.action_search -> navController.navigate(R.id.navSearch)
//                R.id.action_settings -> setAppBarVisivle(false, false)
            }

            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}