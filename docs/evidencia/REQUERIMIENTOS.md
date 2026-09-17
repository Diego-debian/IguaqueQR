# Requerimientos y trazabilidad

| ID | Requerimiento | Implementación | Estado |
|---|---|---|---|
| RF-01 | Bienvenida | `WelcomeScreen` | Aprobado |
| RF-02 | Texto o URL | `HomeScreen`/`QRViewModel` | Aprobado |
| RF-03 | Confirmación | Asistente de seis pasos | Aprobado |
| RF-04 | Forma y marco | `DotStyle`/`FrameType` | Aprobado |
| RF-05 | Contraste | Evaluación en `HomeScreen` | Pendiente de prueba formal |
| RF-06 | Generación | `QRRenderer` y ZXing | Aprobado |
| RF-07 | Guardado | MediaStore y archivo local | Pendiente de prueba formal |
| RF-08 | Compartir | FileProvider e intent | Pendiente de prueba formal |
| RF-09 | Historial | Room y `HistoryScreen` | Aprobado visualmente |

## No funcionales

Kotlin/Compose, persistencia local, variante debug identificable, licencias
documentadas y exclusión de secretos.
