package io.ymsoft.objectfinder.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ItemPhotoBinding
import io.ymsoft.objectfinder.util.loadFilePath
import io.ymsoft.objectfinder.util.setVisible

class CheckablePhotoListAdapter(
    private var itemLongClickListener: ((filePath: String) -> Unit)? = null,
    var checkedChangeListener: ((Set<String>) -> Unit)? = null
) : RecyclerView.Adapter<CheckablePhotoListAdapter.PhotoViewHolder>() {
    var photoList = listOf<String>()
        set(value) {
            field = value
            checkedPhotos.clear()
            checkAll(true)
        }

    private val checkedPhotos = HashSet<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        val binding = ItemPhotoBinding.bind(view)
        val holder = PhotoViewHolder(binding)
        binding.checkbox.visibility = View.VISIBLE
        binding.clickableLayout.setOnClickListener {
            holder.toggle()
            if (holder.isChecked)
                checkedPhotos.add(photoList[holder.adapterPosition])
            else
                checkedPhotos.remove(photoList[holder.adapterPosition])
            checkedChangeListener?.invoke(checkedPhotos)
        }
        binding.clickableLayout.setOnLongClickListener {
            itemLongClickListener?.invoke(photoList[holder.adapterPosition])
            false
        }
        return holder
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        holder.binding.imgView.loadFilePath(photo)
        holder.isChecked = checkedPhotos.contains(photo)
    }

    fun checkAll(check: Boolean) {
        if (check) {
            checkedPhotos.addAll(photoList)
        } else {
            checkedPhotos.clear()
        }
        checkedChangeListener?.invoke(checkedPhotos)
        notifyDataSetChanged()
    }

    fun getCheckedImages(): List<String> {
        return checkedPhotos.toList()
    }

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root),
        Checkable {
        override fun isChecked(): Boolean {
            return binding.checkView.isVisible
        }

        override fun toggle() {
            binding.checkView.isVisible = !binding.checkView.isVisible
            binding.checkbox.toggle()
        }

        override fun setChecked(checked: Boolean) {
            binding.checkView.setVisible(checked)
            binding.checkbox.isChecked = checked
        }
    }
}