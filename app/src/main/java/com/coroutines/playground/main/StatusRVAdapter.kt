package com.coroutines.playground.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coroutines.playground.R
import com.coroutines.playground.network.models.ComponentInfo

class StatusRVAdapter : ListAdapter<ComponentInfo, StatusRVAdapter.ComponentInfoVH>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<ComponentInfo>() {
            override fun areContentsTheSame(oldItem: ComponentInfo, newItem: ComponentInfo) = oldItem == newItem
            override fun areItemsTheSame(oldItem: ComponentInfo, newItem: ComponentInfo) = oldItem.id == newItem.id
        }
    }

    class ComponentInfoVH(view: View) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.name)
        private val status: TextView = view.findViewById(R.id.status)

        fun bind(ci: ComponentInfo) {
            name.text = ci.name
            status.text = ci.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentInfoVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_component_status, parent, false)

        return ComponentInfoVH(view)
    }

    override fun onBindViewHolder(holder: ComponentInfoVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}