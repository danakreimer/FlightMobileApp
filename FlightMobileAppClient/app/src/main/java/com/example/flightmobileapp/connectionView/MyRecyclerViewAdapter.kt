package com.example.flightmobileapp.connectionView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flightmobileapp.R
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.databinding.ListItemBinding

// Recycler View to display the data
class MyRecyclerViewAdapter(private val urlsList: List<ServerUrl>,
                            private val clickListener: (ServerUrl)->Unit) :
    RecyclerView.Adapter<MyViewHolder>() {
    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater,
                R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    // Get the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {
        return urlsList.size
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(urlsList[position], clickListener)
    }
}

// A ViewHolder describes an item view and metadata about its place within the RecyclerView
class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    // Bind view and data
    fun bind(serverUrl: ServerUrl, clickListener: (ServerUrl)->Unit) {
        binding.urlTextView.text = serverUrl.url
        binding.listItemLayout.setOnClickListener {
            clickListener(serverUrl)
        }
    }
}