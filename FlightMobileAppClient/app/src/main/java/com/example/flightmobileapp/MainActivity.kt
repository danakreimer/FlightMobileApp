package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.database.ServerUrlDAO
import com.example.flightmobileapp.database.ServerUrlDatabase
import com.example.flightmobileapp.database.ServerUrlRepository
import com.example.flightmobileapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serverUrlViewModel: ServerUrlViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao: ServerUrlDAO = ServerUrlDatabase.getInstance(application).serverUrlDAO
        val repository = ServerUrlRepository(dao)
        val factory = ServerUrlViewModelFactory(repository)
        serverUrlViewModel = ViewModelProvider(this, factory).get(ServerUrlViewModel::class.java)
        binding.myViewModel = serverUrlViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

        /*
        connectButton.setOnClickListener {
            startActivity(Intent(this, ControlActivity::class.java))
        }
         */

    }

    private fun initRecyclerView() {
        binding.urlRecyclerView.layoutManager = LinearLayoutManager(this)
        showUrlsList()
    }

    private fun showUrlsList() {
        serverUrlViewModel.urls.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            binding.urlRecyclerView.adapter =
                MyRecyclerViewAdapter(it) { selectedItem: ServerUrl->itemClicked(selectedItem)}
        })
    }

    private fun itemClicked(url: ServerUrl) {
        serverUrlViewModel.itemClicked(url)
    }
}