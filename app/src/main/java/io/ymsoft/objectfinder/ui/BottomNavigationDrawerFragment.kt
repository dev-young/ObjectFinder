package io.ymsoft.objectfinder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.ymsoft.objectfinder.R
import kotlinx.android.synthetic.main.fragment_bottom_nav_drawer.view.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_nav_drawer, container, false)

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {
            view.navView.setupWithNavController(navController)
        }

        return view
    }
}