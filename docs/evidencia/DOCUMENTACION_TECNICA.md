# Documentación técnica — Iguaque QR

## Producto

Aplicación Android para crear, personalizar, guardar y compartir códigos QR
mediante un flujo guiado de seis pasos.

## Módulos y flujo

| Módulo | Entrada | Proceso | Salida |
|---|---|---|---|
| Bienvenida | Apertura de la app | Presenta identidad e información | Acceso al creador |
| Enlace | Texto o URL | Captura y valida contenido | Contenido confirmado |
| Destino | Contenido capturado | Detecta y confirma el destino | Destino aceptado |
| Forma | Selección visual | Configura estilo de módulos | Estilo seleccionado |
| Marco | Opción de marco | Aplica o elimina marco | Marco seleccionado |
| Legibilidad | Colores y corrección | Calcula contraste y advertencias | Estado de legibilidad |
| Renderizado | Configuración confirmada | `QRRenderer` usa ZXing Core | Bitmap QR |
| Resultado | Bitmap generado | Presenta acciones disponibles | QR listo |
| Guardado | Bitmap y acción del usuario | Escribe imagen local y MediaStore | Imagen PNG |
| Compartir | Bitmap y acción del usuario | Usa `FileProvider` e intent Android | Selector de compartir |
| Historial | Registro generado | Room consulta y persiste | Lista local de QR generados |

## Persistencia

Room utiliza la base local `qr_art_database`, tabla `qr_history`, versión de
esquema `1`. La entidad `QRHistory` y `QRHistoryDao` se conservaron sin cambios
estructurales. `generateQR()` inserta registros con `type = "generated"`.

## Configuración y ejecutable

- Namespace: `com.qrart`.
- Application ID release: `com.diegodebian.iguaqueqr`.
- Application ID debug: `com.diegodebian.iguaqueqr.test`.
- Versión debug: `1.0.1-test`.
- Licencia: `GPL-3.0-or-later`.
- No se incluyen credenciales, llaves ni `local.properties`.

## Dependencias diferenciadas

Las dependencias de terceros y sus licencias se documentan en
[`THIRD_PARTY_NOTICES.md`](../../THIRD_PARTY_NOTICES.md). ZXing Core permanece
porque participa en la generación. CameraX y ML Kit ya no son dependencias
activas.
