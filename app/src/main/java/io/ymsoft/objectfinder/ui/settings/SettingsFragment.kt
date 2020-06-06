package io.ymsoft.objectfinder.ui.settings

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.ymsoft.objectfinder.databinding.FragmentSettingsBinding
import io.ymsoft.objectfinder.util.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding
    private val viewModel by viewModels<SettingslViewModel>()

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

        viewModel.isWorking.observe(viewLifecycleOwner, Observer(binding.progress::setVisible))
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer(requireContext()::makeToast))


        //test

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAppBarVisible(top = true)
    }

    private fun removeUnusedImg() {
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        viewModel.removeUnusedImg(dir)
    }

}