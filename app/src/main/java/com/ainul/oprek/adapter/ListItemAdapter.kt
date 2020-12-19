package com.ainul.oprek.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.databinding.ListItemBinding

class ListItemAdapter(private val clickListener: ListItemListener) :
    RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>() {
    var data = listOf<String>()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener.onClick(it)
        }
    }

    override fun getItemCount() = data.size

    class ListItemViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.id = data
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