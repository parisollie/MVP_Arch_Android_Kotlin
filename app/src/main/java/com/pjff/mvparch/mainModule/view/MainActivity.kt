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

    //-----Modelo anterior
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

    //-----
    //Paso 103.2, inyectamos el presentador
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Paso 103.3
        presenter = MainPresenter(this)
        presenter.onCreate()

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
    }

    //----- *** pega metodos anteriores--------------------
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
            //Paso 103.4
            lifecycleScope.launch { presenter.refresh() }
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    //Paso 103.5
                    lifecycleScope.launch { presenter.registerAd() }
                }
            }
            setOnLongClickListener {  view ->
                lifecycleScope.launch {
                    //Paso 103.6
                    lifecycleScope.launch { presenter.closeAd() }
                }
                true
            }
        }
    }


    override fun onStart() {
        super.onStart()
        //Paso 103.7
        lifecycleScope.launch { presenter.getEvents() }
    }


    /*
    * ***OnClickListener
    *
    */
    override fun onClick(result: SportEvent.ResultSuccess) {
        binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            //Paso 103.8
            presenter.saveResult(result)
        }
    }
    //---------------------
    //Paso 103.9
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }


    /*
    * V-30, paso 101.0 , tiene que ver con la View layer
    * Definimos todos los metodos que serán despachados del presentador
    * */
    fun add(event: SportEvent.ResultSuccess) {
        adapter.add(event)
    }

    fun clearAdapter() {
        adapter.clear()
    }

    //Paso 101.1 ,Mostar la visibilidad el boton de anuncio
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