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
import io.ymsoft.objectfinder.util.SingleClickListener
import io.ymsoft.objectfinder.util.hideKeyboard
import io.ymsoft.objectfinder.util.setOnSingleClickListener

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

        binding.fab.setOnSingleClickListener {
            navController.navigate(R.id.action_global_navAddStorage)
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
            binding.searchView.isFocusable = true
        }
        else {
            searchView.visibility = View.GONE
            binding.searchView.isFocusable = false
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
            binding.bottomAppBar.apply { post { performHide() } }
            binding.fab.hide()
        }

    }

    private fun initBottomAppBar() {
        binding.bottomAppBar.setNavigationOnClickListener(SingleClickListener({
            val f =
                BottomNavigationDrawerFragment()
            f.show(supportFragmentManager, f.tag)
        }))

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            Log.d("", item.toString())
            when(item.itemId){
                R.id.action_search -> navController.navigate(R.id.action_global_navSearch)
//                R.id.action_settings -> setAppBarVisivle(false, false)
            }

            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
