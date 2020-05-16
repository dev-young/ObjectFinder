package io.ymsoft.objectfinder.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.ui.view_holoer.PositionViewHolder

class PositionListAdapter : ListAdapter<PositionModel, PositionViewHolder>(object : DiffUtil.ItemCallback<PositionModel>(){
    override fun areItemsTheSame(oldItem: PositionModel, newItem: PositionModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PositionModel, newItem: PositionModel): Boolean {
        return oldItem.objString == newItem.objString
    }

}) {

    var clickListener : OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        return PositionViewHolder(parent, clickListener)
    }

    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}