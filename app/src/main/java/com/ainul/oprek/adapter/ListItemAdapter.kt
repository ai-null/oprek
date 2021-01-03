package com.ainul.oprek.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.database.Project
import com.ainul.oprek.databinding.ListItemBinding

class ProjectDiffUtil : DiffUtil.ItemCallback<Project>() {
    override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem == newItem
    }

}

class ListItemAdapter(private val clickListener: ListItemListener) :
    ListAdapter<Project, ListItemAdapter.ListItemViewHolder>(ProjectDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(it)
        }
    }

    class ListItemViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Project) {
            binding.project = data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val holder = ListItemBinding.inflate(layoutInflater, parent, false)

                return ListItemViewHolder(holder)
            }
        }
    }
}