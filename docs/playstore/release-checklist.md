# Checklist de publicación de Iguaque QR

## Proyecto

- [x] `applicationId` único: `com.diegodebian.iguaqueqr`
- [x] `compileSdk` y `targetSdk` preparados para API 36
- [x] `versionCode` y `versionName` definidos
- [x] Nombre, descripción y textos corregidos
- [x] Ícono vectorial propio dentro del proyecto
- [x] Política de privacidad redactada
- [x] Configuración de firma sin secretos en el repositorio

## Antes de subir

- [ ] Publicar `privacy-policy.html` en una URL HTTPS pública.
- [ ] Crear o conservar la cuenta de desarrollador de Google Play.
- [ ] Crear la clave de carga y activar Play App Signing.
- [ ] Completar `keystore.properties` localmente a partir de `keystore.properties.example`.
- [ ] Ejecutar `:app:bundleRelease` y verificar que el AAB esté firmado.
- [ ] Subir el AAB a la prueba interna.
- [ ] Completar Seguridad de datos, clasificación de contenido, público objetivo y ficha de tienda.
- [ ] Probar generación, lectura, guardado y compartir en la versión release.

## Firma local

La clave de carga y sus contraseñas no deben entrar en Git ni enviarse por chat. Guarda una copia segura fuera del proyecto.
