package com.tecsup.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.PermisoUi
import com.tecsup.avisoscampus.notificacion.Notificador
import com.tecsup.avisoscampus.receiver.ModoAvionReceiver
import com.tecsup.avisoscampus.ui.theme.AcentoAviso
import com.tecsup.avisoscampus.ui.theme.AcentoAvion
import com.tecsup.avisoscampus.ui.theme.AcentoPermiso
import com.tecsup.avisoscampus.ui.theme.AcentoRecordatorio
import com.tecsup.avisoscampus.ui.theme.Fucsia
import com.tecsup.avisoscampus.ui.theme.Indigo
import com.tecsup.avisoscampus.ui.theme.RojoDenegado
import com.tecsup.avisoscampus.ui.theme.TextoFuerte
import com.tecsup.avisoscampus.ui.theme.TextoSuave
import com.tecsup.avisoscampus.ui.theme.Violeta
import com.tecsup.avisoscampus.viewmodel.AvisosViewModel

private const val SEGUNDOS_RECORDATORIO = 10

@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val avisos by vm.avisos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }

    DisposableEffect(escuchando) {
        if (escuchando) {
            val receptor = ModoAvionReceiver { activo -> vm.alCambiarModoAvion(activo) }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            if (!mostrado) vm.noSePudoMostrar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Encabezado(
            totalAvisos = avisos.size,
            permiso = permiso,
            escuchando = escuchando
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Permiso
            TarjetaSeccion(
                titulo = "Permiso de notificaciones",
                subtitulo = "Necesario en Android 13 o superior",
                icono = R.drawable.ic_escudo,
                acento = AcentoPermiso
            ) {
                EtiquetaPermiso(permiso)
                BotonPrincipal(
                    texto = "Permitir notificaciones",
                    icono = R.drawable.ic_escudo,
                    colores = listOf(AcentoPermiso, Color(0xFF059669))
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        vm.permisoRespondido(true)
                    }
                }
                AnimatedVisibility(
                    visible = mensaje != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BannerError(texto = mensaje.orEmpty()) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                        contexto.startActivity(intent)
                    }
                }
            }

            // Avisos
            TarjetaSeccion(
                titulo = "Avisos",
                subtitulo = "Envía una notificación al instante",
                icono = R.drawable.ic_campana,
                acento = AcentoAviso
            ) {
                BotonPrincipal(
                    texto = "Mostrar aviso",
                    icono = R.drawable.ic_campana,
                    colores = listOf(Indigo, Violeta)
                ) { vm.avisarAhora() }
            }

            // Recordatorio
            TarjetaSeccion(
                titulo = "Recordatorio",
                subtitulo = "Te avisa en $SEGUNDOS_RECORDATORIO segundos (canal Urgentes)",
                icono = R.drawable.ic_alarma,
                acento = AcentoRecordatorio
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    AnilloCuenta(faltan)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { vm.recordarEn(SEGUNDOS_RECORDATORIO) },
                            enabled = faltan == null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AcentoRecordatorio)
                        ) {
                            Text("Recordar en $SEGUNDOS_RECORDATORIO s", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { vm.cancelarRecordatorio() },
                            enabled = faltan != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }

            // Receptor
            TarjetaSeccion(
                titulo = "Receptor de anuncios",
                subtitulo = "Escucha al sistema con un BroadcastReceiver",
                icono = R.drawable.ic_avion,
                acento = AcentoAvion
            ) {
                val fondo by animateColorAsState(
                    if (escuchando) AcentoAvion.copy(alpha = 0.12f) else Color(0xFFF3F4F6),
                    label = "fondoSwitch"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(fondo)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Escuchar el modo avión",
                            fontWeight = FontWeight.SemiBold,
                            color = TextoFuerte
                        )
                        Text(
                            if (escuchando) "Escuchando cambios…" else "Desactivado",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (escuchando) AcentoAvion else TextoSuave
                        )
                    }
                    Switch(
                        checked = escuchando,
                        onCheckedChange = { vm.cambiarEscucha(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = AcentoAvion,
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }

            HistorialAvisos(avisos)
        }
    }
}

@Composable
private fun Encabezado(totalAvisos: Int, permiso: PermisoUi, escuchando: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.linearGradient(listOf(Indigo, Violeta, Fucsia)))
    ) {
        // Círculos decorativos
        Box(
            Modifier
                .padding(start = 250.dp, top = 10.dp)
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            Modifier
                .padding(start = 300.dp, top = 120.dp)
                .size(90.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
        )
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_campana),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "AvisosCampus",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Tu centro de avisos del campus",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Estadistica("$totalAvisos", "avisos", Modifier.weight(1f))
                Estadistica(
                    when (permiso) {
                        PermisoUi.SinPedir -> "—"
                        PermisoUi.Concedido -> "Sí"
                        PermisoUi.Denegado -> "No"
                    },
                    "permiso",
                    Modifier.weight(1f)
                )
                Estadistica(if (escuchando) "ON" else "OFF", "receptor", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun TarjetaSeccion(
    titulo: String,
    subtitulo: String? = null,
    @DrawableRes icono: Int? = null,
    acento: Color = Indigo,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icono != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(acento.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(icono),
                            contentDescription = null,
                            tint = acento,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    Text(
                        titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextoFuerte
                    )
                    if (subtitulo != null) {
                        Text(
                            subtitulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSuave
                        )
                    }
                }
            }
            contenido()
        }
    }
}

@Composable
private fun BotonPrincipal(
    texto: String,
    @DrawableRes icono: Int,
    colores: List<Color>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(colores)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
private fun AnilloCuenta(faltan: Int?) {
    val progreso by animateFloatAsState(
        targetValue = (faltan ?: 0) / SEGUNDOS_RECORDATORIO.toFloat(),
        label = "progreso"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(96.dp)) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = AcentoRecordatorio.copy(alpha = 0.15f),
            strokeWidth = 9.dp,
            trackColor = Color.Transparent
        )
        CircularProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxSize(),
            color = AcentoRecordatorio,
            strokeWidth = 9.dp,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (faltan != null) "$faltan" else "—",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextoFuerte
            )
            Text(
                if (faltan != null) "Faltan $faltan s" else "inactivo",
                style = MaterialTheme.typography.labelSmall,
                color = TextoSuave
            )
        }
    }
}

@Composable
private fun BannerError(texto: String, abrirAjustes: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFEF2F2))
            .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                painterResource(R.drawable.ic_alerta),
                contentDescription = null,
                tint = RojoDenegado,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(texto, color = Color(0xFF7F1D1D), style = MaterialTheme.typography.bodyMedium)
        }
        OutlinedButton(onClick = abrirAjustes, shape = RoundedCornerShape(12.dp)) {
            Text("Abrir ajustes", color = RojoDenegado, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EtiquetaPermiso(permiso: PermisoUi) {
    val (texto, color) = when (permiso) {
        PermisoUi.SinPedir -> "Sin pedir" to Color(0xFF6B7280)
        PermisoUi.Concedido -> "Permitido" to Color(0xFF059669)
        PermisoUi.Denegado -> "Denegado" to RojoDenegado
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(texto, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}
