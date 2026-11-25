package com.tecsup.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Escucha el anuncio del sistema cuando cambia el modo avión.
 * onReceive es corto: solo lee el estado y lo entrega a quien lo escucha.
 */
class ModoAvionReceiver(
    private val alCambiar: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            alCambiar(intent.getBooleanExtra("state", false))
        }
    }
}
