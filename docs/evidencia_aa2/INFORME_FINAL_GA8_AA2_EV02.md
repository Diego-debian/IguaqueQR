# GA8-220501096-AA2-EV02 — APK: desarrollar módulos móviles según requerimientos del proyecto

**Autor:** Diego Alberto Parra Garzón
**Ficha:** 3186705
**Programa:** Análisis y Desarrollo de Software
**Proyecto:** Iguaque QR
**Repositorio:** https://github.com/Diego-debian/IguaqueQR
**Licencia:** GPL-3.0-or-later

## 1. Propósito

Iguaque QR es una aplicación Android local para crear, personalizar, guardar y compartir códigos QR. El usuario introduce un enlace o texto, recorre un asistente de seis estados y obtiene una imagen QR configurada con forma, marco, color y nivel de corrección.

El alcance no incluye lectura de códigos, cámara ni servicios web. El historial se conserva localmente mediante Room.

## 2. Arquitectura y módulos

La aplicación utiliza una interfaz Jetpack Compose, navegación entre `home` e `history`, un `QRViewModel` para el estado y las acciones, `QRRenderer` para producir el bitmap, ZXing Core para codificación QR y Room para persistencia.

| Módulo | Responsabilidad | Entrada principal | Salida principal |
|---|---|---|---|
| `WelcomeScreen` | Presentación inicial | Acción del usuario | Acceso al creador |
| `HomeScreen` | Asistente de creación | Texto, color, forma, marco y nivel | Revisión y QR generado |
| `HistoryScreen` | Consulta de registros | Flujo Room | Lista y eliminación |
| `QRViewModel` | Estado y coordinación | Acciones de pantallas | Bitmap, mensajes y persistencia |
| `ContentClassifier` | Clasificación de contenido | Texto | Tipo descriptivo |
| `ContrastUtils` | Cálculo de contraste | Colores ARGB | Relación y legibilidad |
| `QRRenderer` | Renderizado del QR | Configuración visual | `Bitmap` |
| `QRDatabase` / `QRHistoryDao` | Persistencia local | `QRHistory` | Flujo, inserción y borrado |

## 3. Flujo integrado

El flujo inicia en la bienvenida y llega a `HomeScreen`. Sus seis pasos son estados internos del asistente, no rutas independientes: Contenido, Destino, Forma, Marco, Lectura/accesibilidad y Resultado/revisión. Al generar, `QRViewModel` solicita el bitmap a `QRRenderer`, que utiliza ZXing con codificación UTF-8. Luego se inserta un registro `type = "generated"` en Room y se muestra el resultado.

## 4. Persistencia, guardado y compartir

`QRDatabase` mantiene la entidad `QRHistory` en la tabla `qr_history`, versión de esquema 1. `QRHistoryDao` consulta por fecha descendente, inserta, elimina un registro y limpia el historial. El guardado utiliza `MediaStore` y el compartir utiliza `FileProvider` junto con un `Intent` de tipo `image/png`.

## 5. Dependencias principales

| Dependencia | Versión encontrada | Uso |
|---|---:|---|
| AndroidX Core / Lifecycle / Activity | 1.10.1 / 2.6.1 / 1.7.2 | Base Android y ciclo de vida |
| Jetpack Compose BOM | 2023.08.00 | Interfaz declarativa |
| Navigation Compose | 2.6.0 | Rutas `home` e `history` |
| ZXing Core | 3.5.2 | Generación QR |
| Room | 2.5.2 | Base local |
| Coil Compose | 2.4.0 | Carga de imágenes |
| JUnit | 4.13.2 | Pruebas JVM |
| AndroidX Test / Espresso | 1.1.5 / 3.5.1 | Pruebas Android |

Las condiciones de terceros se mantienen diferenciadas del código original en `THIRD_PARTY_NOTICES.md`.

## 6. Pruebas y resultados

Se ejecutaron 22 pruebas automatizadas y las 22 fueron aprobadas. También se diseñaron cinco pruebas Compose UI que no pudieron ejecutarse por un fallo del runner de instrumentación en el dispositivo Xiaomi/MIUI.

| Grupo | Diseñadas | Ejecutadas | Aprobadas | Fallidas | No ejecutadas |
|---|---:|---:|---:|---:|---:|
| JVM | 13 | 13 | 13 | 0 | 0 |
| Room | 4 | 4 | 4 | 0 | 0 |
| QRRenderer | 5 | 5 | 5 | 0 | 0 |
| Compose UI | 5 | 0 | 0 | 0 | 5 |
| **Total** | **27** | **22** | **22** | **0** | **5** |

El defecto Unicode detectado fue la ausencia del hint explícito de codificación. Se corrigió con `EncodeHintType.CHARACTER_SET to "UTF-8"`; las pruebas posteriores generaron y decodificaron correctamente tildes, `ñ` y caracteres no ASCII.

La incidencia completa del runner se encuentra en `INCIDENCIA_COMPOSE_UI_TEST.md`. Las verificaciones manuales de navegación e historial se consideran evidencia complementaria, no pruebas automatizadas.

## 7. Compilación

- `testDebugUnitTest`: exitoso.
- `assembleDebugAndroidTest`: exitoso.
- `assembleDebug`: exitoso.
- `lintDebug`: exitoso con advertencias no bloqueantes.

## 8. Diagramas técnicos

Los archivos editables, SVG y PNG están en `diagramas/`:

1. Diagrama de clases.
2. Diagrama de paquetes.
3. Diagrama de componentes.
4. Mapa de navegación.
5. Secuencia de generación.

## 9. APK y configuración

La variante de evaluación utiliza `applicationIdSuffix = ".test"` y `versionNameSuffix = "-test"`. El paquete de evaluación es `com.diegodebian.iguaqueqr.test`. No se incluyen llaves, contraseñas, configuración local, carpetas de compilación ni credenciales.

## 10. Conclusión

Los módulos de interfaz, estado, generación QR, almacenamiento local, guardado y compartir están integrados y compilados. La limitación documentada corresponde exclusivamente a la ejecución automatizada de Compose UI en el entorno Xiaomi/MIUI; no constituye evidencia de un fallo funcional de la aplicación.

**SPDX-License-Identifier: GPL-3.0-or-later**
