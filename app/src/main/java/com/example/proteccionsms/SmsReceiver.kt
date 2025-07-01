package com.example.proteccionsms // Asegúrate de que este sea el nombre de tu paquete

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
                Log.d("SmsReceiver", "SMS Received: $fullSms") // Mensaje de log en inglés

                // Update LiveData for the UI with the latest SMS
                lastSmsMessage.postValue(fullSms)

                // Logic to block SMS (smishing)
                if (shouldBlockSms(sender, messageBody)) {
                    Log.d("SmsReceiver", "SMS Blocked (Phishing): $fullSms") // Mensaje de log en inglés
                    BlockedMessageCounter.incrementBlockedSmsCount() // Use the correct counter
                    // If you want to ABORT the broadcast so other apps (including the default SMS app) don't receive it,
                    // uncomment the following line. Use it with EXTREME CAUTION and only if your app is the default SMS app.
                    // abortBroadcast()
                }
            }
        }
    }

    /**
     * Determines if an SMS should be blocked based on the sender or content.
     * Here, the smishing detection logic is implemented.
     * @param sender The phone number or name of the sender.
     * @param messageBody The body of the SMS message.
     * @return true if the SMS should be blocked, false otherwise.
     */
    private fun shouldBlockSms(sender: String, messageBody: String): Boolean {
        // List of common smishing keywords (you can expand this)
        val phishingKeywords = listOf(
            "actualice su informacion", "su cuenta ha sido bloqueada", "verifique su identidad",
            "ganador de la loteria", "premio", "haga clic aqui", "urgente", "suspension",
            "problemas con su pago", "envio pendiente", "reembolso", "paquete retenido",
            "acceso no autorizado", "restablecer contraseña", "verifique su cuenta",
            "datos bancarios", "tarjeta de credito", "descuento exclusivo", "oferta limitada",
            "confirmar datos", "inicia sesion", "verificacion de seguridad"
        )

        // Check if the message contains any phishing keyword (ignoring case)
        if (phishingKeywords.any { messageBody.contains(it, ignoreCase = true) }) {
            return true
        }

        // You can add logic for specific senders (e.g., unknown numbers, suspicious senders)
        // if (sender == "5551234" || sender.contains("soporte", ignoreCase = true)) {
        //     return true
        // }

        return false
    }
}