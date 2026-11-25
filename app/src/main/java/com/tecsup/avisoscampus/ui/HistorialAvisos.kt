package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.ui.theme.AcentoAviso
import com.tecsup.avisoscampus.ui.theme.AcentoAvion
import com.tecsup.avisoscampus.ui.theme.AcentoHistorial
import com.tecsup.avisoscampus.ui.theme.AcentoRecordatorio
import com.tecsup.avisoscampus.ui.theme.TextoFuerte
import com.tecsup.avisoscampus.ui.theme.TextoSuave

/**
 * Tarjeta Historial. Solo recibe la lista: no conoce al ViewModel.
 * Usa Column + forEach porque la pantalla ya tiene desplazamiento.
 */
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    TarjetaSeccion(
        titulo = "Historial",
        subtitulo = if (avisos.isEmpty()) "Aquí aparecerán tus avisos" else "${avisos.size} avisos guardados",
        icono = R.drawable.ic_historial,
        acento = AcentoHistorial
    ) {
        if (avisos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painterResource(R.drawable.ic_campana),
                    contentDescription = null,
                    tint = AcentoHistorial.copy(alpha = 0.35f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("Sin avisos todavía", color = TextoSuave, fontWeight = FontWeight.Medium)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                avisos.take(20).forEach { FilaAviso(it) }
            }
        }
    }
}

private fun estiloOrigen(origen: Origen): Pair<Int, Color> = when (origen) {
    Origen.BOTON -> R.drawable.ic_campana to AcentoAviso
    Origen.RECORDATORIO -> R.drawable.ic_alarma to AcentoRecordatorio
    Origen.MODO_AVION -> R.drawable.ic_avion to AcentoAvion
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    val (icono, color) = estiloOrigen(aviso.origen)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.07f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(icono),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(aviso.titulo, fontWeight = FontWeight.Bold, color = TextoFuerte)
            Text(aviso.texto, style = MaterialTheme.typography.bodyMedium, color = TextoSuave)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                aviso.hora,
                style = MaterialTheme.typography.labelMedium,
                color = TextoFuerte,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                aviso.origen.texto,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
