package com.tecsup.avisoscampus

import android.app.Application
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.data.AvisosRepositoryRoom
import com.tecsup.avisoscampus.data.local.AvisosDatabase
import com.tecsup.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {

    // La base de datos se crea aquí (sí hay Context) y el Repository solo recibe el DAO.
    val repositorio: AvisosRepository by lazy {
        AvisosRepositoryRoom(AvisosDatabase.crear(this).avisoDao())
    }

    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
