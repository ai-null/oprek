package com.ainul.oprek.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ainul.oprek.R

class ListItemAdapter : RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>() {
    var data = listOf<String>()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val holder = layoutInflater.inflate(R.layout.list_item, parent, false)

        return ListItemViewHolder(holder)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = data[position]
    }

    override fun getItemCount() = data.size
}