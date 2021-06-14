package io.ymsoft.objectfinder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import io.ymsoft.objectfinder.R

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_nav_drawer, container, false)

        val navController = activity?.findNavController(R.id.nav_host_fragment)
        if (navController != null) {
            view.findViewById<NavigationView>(R.id.navView).setupWithNavController(navController)
        }
        // TODO: 2021-06-15 버텀시트에서 메뉴 실행시 화면전환 효과를 앱바에서 실행시키는것과 동일하게 수정하기 (아예 중복되는 메뉴는 제거하는것도 괜찮은 생각같다!)
        return view
    }
}