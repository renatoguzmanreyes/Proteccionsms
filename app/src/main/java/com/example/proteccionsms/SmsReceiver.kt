package com.example.demomysmsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.lifecycle.MutableLiveData

/**
 * BroadcastReceiver que escucha los SMS entrantes.
 * Procesa los mensajes, los muestra en la UI y aplica lógica de bloqueo.
 */
class SmsReceiver : BroadcastReceiver() {

    // LiveData para observar el último SMS recibido desde la UI
    companion object {
        val lastSmsMessage = MutableLiveData<String>()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (smsMessage in messages) {
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.displayMessageBody
                val fullSms = "De: $sender, Mensaje: $messageBody"
                Log.d("SmsReceiver", "SMS Recibido: $fullSms")

                // Actualizar LiveData para la UI con el último SMS
                lastSmsMessage.postValue(fullSms)

                // Lógica para bloquear SMS (smishing)
                if (shouldBlockSms(sender, messageBody)) {
                    Log.d("SmsReceiver", "SMS bloqueado (Phishing): $fullSms")
                    BlockedMessageCounter.incrementBlockedSmsCount() // Usar el nuevo contador
                    // Si deseas ABORTAR el broadcast para que otras apps (incluida la default SMS) no lo reciban,
                    // descomenta la siguiente línea. Úsala con EXTREMA PRECAUCIÓN y solo si tu app es la default SMS.
                    // abortBroadcast()
                }
            }
        }
    }

    /**
     * Determina si un SMS debe ser bloqueado basándose en el remitente o el contenido.
     * Aquí se implementa la lógica de detección de smishing.
     * @param sender El número de teléfono o nombre del remitente.
     * @param messageBody El cuerpo del mensaje SMS.
     * @return true si el SMS debe ser bloqueado, false en caso contrario.
     */
    private fun shouldBlockSms(sender: String, messageBody: String): Boolean {
        // Lista de palabras clave comunes en ataques de smishing (puedes expandirla)
        val phishingKeywords = listOf(
            "actualice su informacion", "su cuenta ha sido bloqueada", "verifique su identidad",
            "ganador de la loteria", "premio", "haga clic aqui", "urgente", "suspension",
            "problemas con su pago", "envio pendiente", "reembolso", "paquete retenido",
            "acceso no autorizado", "restablecer contraseña", "verifique su cuenta",
            "datos bancarios", "tarjeta de credito", "descuento exclusivo", "oferta limitada",
            "confirmar datos", "inicia sesion", "verificacion de seguridad"
        )

        // Verificar si el mensaje contiene alguna palabra clave de phishing (ignorando mayúsculas/minúsculas)
        if (phishingKeywords.any { messageBody.contains(it, ignoreCase = true) }) {
            return true
        }

        // Puedes añadir lógica para remitentes específicos (ej. números desconocidos, remitentes sospechosos)
        // if (sender == "5551234" || sender.contains("soporte", ignoreCase = true)) {
        //     return true
        // }

        return false
    }
}