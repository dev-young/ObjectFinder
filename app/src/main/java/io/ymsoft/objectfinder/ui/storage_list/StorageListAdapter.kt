package io.ymsoft.objectfinder.ui.storage_list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.ui.view_holoer.StorageViewHolder

class StorageListAdapter : ListAdapter<StorageModel, StorageViewHolder>(object : DiffUtil.ItemCallback<StorageModel>(){
    override fun areItemsTheSame(oldItem: StorageModel, newItem: StorageModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StorageModel, newItem: StorageModel): Boolean {
        return oldItem == newItem
    }

}) {

    fun setClickListener(listener: (position: Int, sharedViews :List<View>) -> Unit){
        clickListener = listener
    }
    private var clickListener : ((Int, List<View>) -> Unit)? = null
    var longClickListener : OnItemLongClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
        return StorageViewHolder.from(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: StorageViewHolder, storage: Int) {
        holder.onBind(getItem(storage))
    }

}