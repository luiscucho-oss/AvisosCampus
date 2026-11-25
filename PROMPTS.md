# PROMPTS

1. En mi app AvisosCampus (Kotlin, Jetpack Compose, MVVM + Repository) agrega un segundo canal de notificación llamado "Urgentes" con IMPORTANCE_HIGH para que el recordatorio aparezca como notificación emergente. El canal "Avisos" debe seguir igual para el botón y el modo avión. No uses Context ni NotificationManager dentro del ViewModel.

2. Modifica la función Notificador.mostrar(context, aviso) para que al expandir la notificación se vea el texto completo usando NotificationCompat.BigTextStyle, incluyendo el origen y la hora del aviso.

3. Quiero que el historial de avisos no se pierda al cerrar la app. Implementa la persistencia con Room: una entidad AvisoEntity, un AvisoDao con un Flow ordenado del más reciente al más antiguo y la base de datos AvisosDatabase. Crea AvisosRepositoryRoom que implemente la interface AvisosRepository sin recibir Context, solo el DAO.

4. ¿Cómo creo la base de datos de Room en la clase Application y le paso el repositorio al AvisosViewModelFactory sin romper MVVM? En el ViewModel convierte el Flow del repositorio en StateFlow con stateIn.

5. Rediseña la interfaz de PantallaAvisos y HistorialAvisos para que se vea moderna y atractiva: un encabezado con degradado y contadores (avisos, permiso y receptor), tarjetas redondeadas con un icono y color propio para cada sección, un anillo circular animado para la cuenta regresiva del recordatorio, un banner de error con icono cuando el permiso está denegado y un historial con iconos de colores según el origen. Mantén la misma lógica y no cambies el ViewModel.
