package io.ymsoft.objectfinder.util

import android.view.View
import androidx.navigation.fragment.FragmentNavigator
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageDetailBinding
import io.ymsoft.objectfinder.databinding.ItemStorageBinding

object SharedViewUtil {

    fun makeStorageTransition(rootView: List<View>): FragmentNavigator.Extras {
        return FragmentNavigator.Extras.Builder().apply {
            rootView.forEach { it ->
                it.transitionName?.let {name ->
                    addSharedElement(it, name)
                }
            }
        }.build()
    }

    fun setTransitionName(binding: FragmentStorageDetailBinding, model: StorageModel) {
//        binding.root.transitionName = "root_${model.id}"
        binding.imageLayout.transitionName = "layout_${model.id}"
        binding.pointer.transitionName = "pointer_${model.id}"
        binding.imgView.transitionName = "img_${model.id}"
//        binding.chipGroup.transitionName = "objects_${model.id}"
    }

    fun setTransitionName(binding: ItemStorageBinding, model: StorageModel) {
//        binding.root.transitionName = "root_${model.id}"
        binding.photoLayout.transitionName = "layout_${model.id}"
        binding.pointer.transitionName = "pointer_${model.id}"
        binding.imgView.transitionName = "img_${model.id}"
//        binding.objects.transitionName = "objects_${model.id}"
    }
}