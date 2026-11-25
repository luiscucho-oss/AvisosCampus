package com.tecsup.avisoscampus.data

import com.tecsup.avisoscampus.data.local.AvisoDao
import com.tecsup.avisoscampus.data.local.AvisoEntity
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface AvisosRepository {
    val avisos: Flow<List<Aviso>>
    suspend fun registrar(titulo: String, texto: String, origen: Origen): Aviso
}

private fun horaActual(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

/** Historial persistente con Room: no se pierde al cerrar la app. No usa Context. */
class AvisosRepositoryRoom(private val dao: AvisoDao) : AvisosRepository {

    override val avisos: Flow<List<Aviso>> =
        dao.observarTodos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
        val entidad = AvisoEntity(
            titulo = titulo,
            texto = texto,
            origen = origen.name,
            hora = horaActual()
        )
        val id = dao.insertar(entidad).toInt()
        return entidad.copy(id = id).aModelo()
    }
}

/** Versión en memoria (útil para pruebas sin Android). */
class AvisosRepositoryMemoria : AvisosRepository {
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: Flow<List<Aviso>> = _avisos.asStateFlow()
    private var siguiente = 1

    override suspend fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
        val aviso = Aviso(siguiente++, titulo, texto, origen, horaActual())
        _avisos.value = listOf(aviso) + _avisos.value // lo más reciente arriba
        return aviso
    }
}
