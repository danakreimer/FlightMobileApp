package com.example.flightmobileapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.databinding.ListItemBinding
import com.example.flightmobileapp.generated.callback.OnClickListener

class MyRecyclerViewAdapter(private val urlsList: List<ServerUrl>,
                            private val clickListener: (ServerUrl)->Unit) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return urlsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(urlsList[position], clickListener)
    }
}

class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(serverUrl: ServerUrl, clickListener: (ServerUrl)->Unit) {
        binding.urlTextView.text = serverUrl.url
        binding.listItemLayout.setOnClickListener {
            clickListener(serverUrl)
        }
    }
}