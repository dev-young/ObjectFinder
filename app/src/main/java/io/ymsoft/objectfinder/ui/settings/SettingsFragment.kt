package io.ymsoft.objectfinder.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentSettingsBinding
import io.ymsoft.objectfinder.util.*

class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.removeUnusedImgBtn.setOnSingleClickListener {
            removeUnusedImg()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAppBarVisible(top = true)
    }

    private fun removeUnusedImg() {
        findNavController().navigate(R.id.action_navSettings_to_navDeleteUnusedImg)
    }

}