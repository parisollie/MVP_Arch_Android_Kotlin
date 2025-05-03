package com.pjff.mvparch.mainModule.model


import com.pjff.mvparch.common.EventBus
import com.pjff.mvparch.common.SportEvent
import com.pjff.mvparch.common.getAdEventsInRealtime
import com.pjff.mvparch.common.getResultEventsInRealtime
import com.pjff.mvparch.common.someTime
import kotlinx.coroutines.delay

//V-29,Paso 100.2, métodos que tienen que ver con los datos- MODELO
class MainRepository {
    //Suspend ,trabaremos con corrutinas
    suspend fun getEvents() {
        val events = getResultEventsInRealtime()
        events.forEach {  event ->
            delay(someTime())
            publishEvent(event)
        }
    }

    //Paso 100.3,resice un resultado
    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        //V-32,paso 103.0, para simular una respuesta del servidor
        val response = if (result.isWarning)
            SportEvent.ResultError(30, "Error al guardar.")
        else SportEvent.SaveEvent
        publishEvent(response)
    }

    //Paso 100.4
    suspend fun registerAd() {
        val events = getAdEventsInRealtime()
        publishEvent(events.first())
    }

    //Paso 100.5
    suspend fun closeAd() {
        publishEvent(SportEvent.ClosedAdEvent)
    }

    //Paso 100.6
    private suspend fun publishEvent(event: SportEvent) {
        EventBus.instance().publish(event)
    }
}