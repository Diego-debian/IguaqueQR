# Manual técnico — Iguaque QR

## Estructura

- `app/src/main/java/com/qrart/ui/screens`: pantallas Compose.
- `app/src/main/java/com/qrart/viewmodel`: estado y operaciones.
- `app/src/main/java/com/qrart/drawing`: renderizado QR.
- `app/src/main/java/com/qrart/data`: entidad, DAO y base Room.
- `app/src/main/res`: recursos Android.

## Operación interna

`HomeScreen` recopila el contenido y la configuración. `QRViewModel` coordina
`QRRenderer`, Room, MediaStore y FileProvider. `QRRenderer` usa `QRCodeWriter`
de ZXing Core para producir el bitmap. `HistoryScreen` consulta el Flow del DAO
y permite seleccionar o eliminar registros.

## Base de datos

Base: `qr_art_database`; tabla: `qr_history`; versión: 1. No se requiere
migración para la copia congelada.

## Compilación

1. Instalar JDK 17 o superior y Android SDK 36.
2. Configurar el SDK local sin versionar `local.properties`.
3. Ejecutar `gradlew.bat assembleDebug`.
4. Revisar `app/build/outputs/apk/debug/app-debug.apk`.
5. Calcular SHA-256 antes de entregar.

No usar credenciales de firma para el APK debug de evaluación.

## Seguridad de entrega

Excluir `.jks`, `keystore.properties`, `local.properties`, `gradle.zip`,
`.gradle` y carpetas `build`. El archivo `.gitignore` contiene estas reglas.
