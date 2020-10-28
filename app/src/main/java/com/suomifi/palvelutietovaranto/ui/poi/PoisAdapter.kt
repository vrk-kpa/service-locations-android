package com.suomifi.palvelutietovaranto.ui.poi

import android.databinding.DataBindingUtil
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.databinding.ItemPoiBinding
import com.suomifi.palvelutietovaranto.domain.ptv.model.PoiDetails
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage

interface PoiItemClickedCallback {
    fun onPoiClicked(poiDetails: PoiDetails)
}

class PoiItemViewHolder(val binding: ItemPoiBinding) : RecyclerView.ViewHolder(binding.root)

object PoiItemCallback : DiffUtil.ItemCallback<PoiDetails>() {
    override fun areItemsTheSame(oldItem: PoiDetails, newItem: PoiDetails): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PoiDetails, newItem: PoiDetails): Boolean {
        return oldItem == newItem
    }
}

class PoisAdapter(
        private val callback: PoiItemClickedCallback
) : ListAdapter<PoiDetails, PoiItemViewHolder>(PoiItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemPoiBinding>(
                LayoutInflater.from(parent.context), R.layout.item_poi, parent, false
        )
        binding.callback = callback
        return PoiItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiItemViewHolder, position: Int) {
        holder.binding.poiDetails = getItem(position)
        holder.binding.selectedLanguage = holder.itemView.context.selectedLanguage()
        holder.binding.executePendingBindings()
    }
}
