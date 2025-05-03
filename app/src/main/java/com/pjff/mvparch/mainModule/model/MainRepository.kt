package com.pjff.mvparch.mainModule.model


import com.pjff.mvparch.common.EventBus
import com.pjff.mvparch.common.SportEvent
import com.pjff.mvparch.common.getAdEventsInRealtime
import com.pjff.mvparch.common.getResultEventsInRealtime
import com.pjff.mvparch.common.someTime
import kotlinx.coroutines.delay


class MainRepository {
    suspend fun getEvents() {
        val events = getResultEventsInRealtime()
        events.forEach {  event ->
            delay(someTime())
            publishEvent(event)
        }
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        val response = if (result.isWarning)
            SportEvent.ResultError(30, "Error al guardar.")
        else SportEvent.SaveEvent
        publishEvent(response)
    }

    suspend fun registerAd() {
        val events = getAdEventsInRealtime()
        publishEvent(events.first())
    }

    suspend fun closeAd() {
        publishEvent(SportEvent.ClosedAdEvent)
    }

    private suspend fun publishEvent(event: SportEvent) {
        EventBus.instance().publish(event)
    }
}