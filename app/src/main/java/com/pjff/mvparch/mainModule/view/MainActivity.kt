package com.pjff.mvparch.mainModule.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pjff.mvparch.common.SportEvent
import com.pjff.mvparch.databinding.ActivityMainBinding
import com.pjff.mvparch.mainModule.presenter.MainPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() , OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(this)
        presenter.onCreate()

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            lifecycleScope.launch { presenter.refresh() }
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    lifecycleScope.launch { presenter.registerAd() }
                }
            }
            setOnLongClickListener {  view ->
                lifecycleScope.launch {
                    lifecycleScope.launch { presenter.closeAd() }
                }
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { presenter.getEvents() }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    /*
    * OnClickListener
    * */
    override fun onClick(result: SportEvent.ResultSuccess) {
        binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            presenter.saveResult(result)
        }
    }
    /*
    * View layer
    * */
    fun add(event: SportEvent.ResultSuccess) {
        adapter.add(event)
    }

    fun clearAdapter() {
        adapter.clear()
    }

    suspend fun showAdUI(isVisible: Boolean) = withContext(Dispatchers.Main) {
        binding.btnAd.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun showProgress(isVisible: Boolean) {
        binding.srlResults.isRefreshing = isVisible
    }

    suspend fun showToast(msg: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}