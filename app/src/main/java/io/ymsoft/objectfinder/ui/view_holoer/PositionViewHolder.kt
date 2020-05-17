package io.ymsoft.objectfinder.ui.view_holoer

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ItemPositionBinding
import io.ymsoft.objectfinder.loadFilePath
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel

class PositionViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    constructor(parent:ViewGroup, clickListener: OnItemClickListener?):
            this(LayoutInflater.from(parent.context).inflate(R.layout.item_position, parent, false), clickListener)

    private val binding : ItemPositionBinding by lazy {ItemPositionBinding.bind(itemView)}

    init {
        binding.clickableLayout.setOnClickListener { clickListener?.onItemClick(adapterPosition) }
    }

    fun onBind(model: PositionModel) {
        if (model.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = GONE
        } else {
            binding.imgView.visibility = VISIBLE
            binding.imgView.loadFilePath(model.imgUrl)
        }

        if(model.name.isNullOrBlank()){
            binding.title.visibility = GONE
        } else {
            binding.title.text = model.name
            binding.title.visibility = VISIBLE
        }
        binding.objects.text = model.objString
    }
}