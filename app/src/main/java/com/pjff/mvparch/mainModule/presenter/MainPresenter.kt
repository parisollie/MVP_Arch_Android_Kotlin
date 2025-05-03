package com.pjff.mvparch.mainModule.presenter

import android.util.Log
import com.pjff.mvparch.mainModule.view.MainActivity
import com.pjff.mvparch.common.EventBus
import com.pjff.mvparch.common.SportEvent
import com.pjff.mvparch.mainModule.model.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

//V-31,Paso 102.0, la capa del presentador es la clave para implementar el MVP
//Hacemos una inyeccion de dependencias con el el private
class MainPresenter(private val view: MainActivity) {
    //Se llama al repositorio tanto como a la vista
    private val repository = MainRepository()
    //Corrutina
    private lateinit var viewScope: CoroutineScope

    fun onCreate() {
        //Inicializamos la corrutina
        viewScope = CoroutineScope(Dispatchers.IO + Job())
        //Paso 102.3 Función para poder suscribirnos al evento
        onEvent()
    }

    //Paso 102.1
    fun onDestroy() {
        viewScope.cancel()
    }

    //Funciones que serán llamadas desde la vista
    suspend fun refresh() {
        view.clearAdapter()
        //Mostramos el boton de publicidad
        view.showAdUI(true)
        getEvents()
    }

    suspend fun getEvents() {
        //Consulta a nuestra fuente de datos
        view.showProgress(true)
        //Acedemos al repositorio
        repository.getEvents()
    }

    suspend fun registerAd() {
        //Registra un evento de publicidad
        repository.registerAd()
    }

    suspend fun closeAd() {
        //cerrar la publicidad
        repository.closeAd()
    }

    //Guardar el resultado
    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        //Mostramos el progreso
        view.showProgress(true)
        repository.saveResult(result)
    }

    //Paso 102.2
    private fun onEvent() {
        viewScope.launch {
            EventBus.instance().subscribe<SportEvent> { event ->
                this.launch {
                    when (event) {
                        is SportEvent.ResultSuccess -> {
                            //Le decimos a la vista que añada el evento
                            view.add(event)
                            view.showProgress(false)
                        }

                        is SportEvent.ResultError -> {
                            view.showSnackbar("Code: ${event.code}, Message: ${event.msg}")
                            view.showProgress(false)
                        }

                        is SportEvent.AdEvent ->
                            view.showToast("Ad click. Send data to server...")

                        is SportEvent.ClosedAdEvent -> {
                            view.showAdUI(false)
                            Log.i("CursosANTAG", "Ad was closed. Send data to server...")
                        }

                        is SportEvent.SaveEvent -> {
                            view.showToast("Guardado")
                            view.showProgress(false)
                        }
                    }
                }
            }
        }
    }
}