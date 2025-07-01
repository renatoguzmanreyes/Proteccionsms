package com.example.demomysmsapp

import androidx.lifecycle.MutableLiveData

/**
 * Objeto singleton para gestionar los contadores de mensajes (SMS y Notificaciones) bloqueados.
 * Utiliza LiveData para permitir que la UI observe los cambios en tiempo real.
 */
object BlockedMessageCounter {
    // LiveData para el contador de SMS bloqueados
    val blockedSmsCount = MutableLiveData(0)

    // LiveData para el contador de notificaciones bloqueadas
    val blockedNotificationCount = MutableLiveData(0)

    /**
     * Incrementa el contador de SMS bloqueados.
     */
    fun incrementBlockedSmsCount() {
        blockedSmsCount.postValue((blockedSmsCount.value ?: 0) + 1)
    }

    /**
     * Incrementa el contador de notificaciones bloqueadas.
     */
    fun incrementBlockedNotificationCount() {
        blockedNotificationCount.postValue((blockedNotificationCount.value ?: 0) + 1)
    }
}
