package io.ymsoft.objectfinder.ui.detail

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnItemClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageListBottomSheetBinding
import io.ymsoft.objectfinder.ui.storage_list.StorageListAdapter


class StorageListBottomSheetFragment(listener:(model:StorageModel)-> Unit) : BottomSheetDialogFragment() {

    lateinit var binding : FragmentStorageListBottomSheetBinding

    private lateinit var bottomSheetBehavior : BottomSheetBehavior<View>

    var storageList = listOf<StorageModel>()
        set(value) {
            listAdapter.submitList(value)
        }

    private val listAdapter: StorageListAdapter = StorageListAdapter().apply {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                listener(currentList[position])
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet : BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        //inflating layout
        val view = View.inflate(context, R.layout.fragment_storage_list_bottom_sheet, null)
        binding = FragmentStorageListBottomSheetBinding.bind(view)

        //setting layout with bottom sheet
        bottomSheet.setContentView(view)

        bottomSheetBehavior =
            BottomSheetBehavior.from(view.parent as View)


        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO


        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {

                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {

                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        //aap bar cancel button clicked
        binding.cancelBtn.setOnClickListener { dismiss() }


        //hiding app bar at the start
//        hideAppBar(binding.appBarLayout)

        binding.recyclerView.adapter = listAdapter
//        listAdapter.submitList(storageList)

        return bottomSheet
    }

}