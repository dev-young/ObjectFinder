package io.ymsoft.objectfinder.ui.view_holoer

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.ItemStorageBinding
import io.ymsoft.objectfinder.util.*

class StorageViewHolder private constructor(
    val binding: ItemStorageBinding,
    clickListener: ((Int, List<View>) -> Unit?)?,
    longClickListener: OnItemLongClickListener?
) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    init {
        binding.clickableLayout.setOnSingleClickListener {
            clickListener?.invoke(
                adapterPosition,
                arrayListOf(
                    binding.cardView
                )
            )
        }
        binding.clickableLayout.setOnLongClickListener {
            longClickListener?.onItemLongClick(adapterPosition)
            true
        }
    }


    fun onBind(model: StorageModel) {
//        binding.cardView.transitionName = "test${model.id}"
        SharedViewUtil.setTransitionName(binding, model)

        if (model.imgUrl.isNullOrBlank()) {
            binding.photoLayout.visibility = GONE
        } else {
            binding.photoLayout.visibility = VISIBLE
            binding.imgView.loadFilePath(model.imgUrl)
            PointerUtil.movePointerByRelative(binding.pointer, binding.imgView, model.x, model.y)
        }

        if (model.name.isNullOrBlank()) {
            binding.title.visibility = GONE
        } else {
            binding.title.text = model.name
            binding.title.visibility = VISIBLE
        }

        val c = binding.root.context
        if (model.objString.isNullOrBlank()) {
            binding.objects.text = c.getString(R.string.empty_msg_storage)
            binding.objects.setTextColor(c.resources.getColor(android.R.color.darker_gray, null))
        } else {
            binding.objects.text = model.objString
            binding.objects.setTextColor(c.resources.getColor(R.color.colorPrimaryDark, null))
        }

    }

    companion object {
        fun from(
            parent: ViewGroup,
            clickListener: ((Int, List<View>) -> Unit?)?,
            longClickListener: OnItemLongClickListener?
        ): StorageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return StorageViewHolder(
                ItemStorageBinding.inflate(inflater, parent, false),
                clickListener,
                longClickListener
            )
        }
    }
}