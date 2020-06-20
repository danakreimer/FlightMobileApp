package com.example.flightmobileapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightmobileapp.connectionView.MyRecyclerViewAdapter
import com.example.flightmobileapp.connectionView.ServerUrlViewModel
import com.example.flightmobileapp.connectionView.ServerUrlViewModelFactory
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.database.ServerUrlDAO
import com.example.flightmobileapp.database.ServerUrlDatabase
import com.example.flightmobileapp.database.ServerUrlRepository
import com.example.flightmobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serverUrlViewModel: ServerUrlViewModel

    // Initialize the objects required for the connection view
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the activity with the appropriate view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Create data access object
        val dao: ServerUrlDAO = ServerUrlDatabase.getInstance(application).serverUrlDAO
        // Create repository
        val repository = ServerUrlRepository(dao)
        // Create view model factory
        val factory = ServerUrlViewModelFactory(repository, this)
        // Create view model
        serverUrlViewModel = ViewModelProvider(this, factory).get(ServerUrlViewModel::class.java)
        // Set binding
        binding.myViewModel = serverUrlViewModel
        binding.lifecycleOwner = this
        // Initialize recycler view
        initRecyclerView()
    }

    // Initialize recycler view to display the URLs list
    private fun initRecyclerView() {
        binding.urlRecyclerView.layoutManager = LinearLayoutManager(this)
        showUrlsList()
    }

    // Display the URLs list
    private fun showUrlsList() {
        serverUrlViewModel.urls.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            binding.urlRecyclerView.adapter =
                MyRecyclerViewAdapter(it) { selectedItem: ServerUrl -> itemClicked(selectedItem) }
        })
    }

    // Change the URL text view to display the clicked URL
    private fun itemClicked(url: ServerUrl) {
        serverUrlViewModel.itemClicked(url)
    }
}