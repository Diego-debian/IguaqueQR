# INFORME FINAL

## GA8-220501096-AA1-EV02 — Módulos integrados

**Proyecto:** Iguaque QR  
**Autor:** Diego Alberto Parra Garzón  
**Fecha:** 17 de septiembre de 2026  
**Licencia:** GPL-3.0-or-later

## Tabla de contenido

1. Introducción  
2. Objetivos  
3. Alcance  
4. Problema y solución  
5. Requerimientos  
6. Trazabilidad  
7. Arquitectura  
8. Tecnologías  
9. Módulos integrados  
10. Entradas, procesos y salidas  
11. Flujo de generación  
12. Persistencia  
13. Dependencias y licencias  
14. Ambientes y configuración  
15. Pruebas y resultados  
16. Ejecutable  
17. Control de versiones  
18. Manual técnico y compilación  
19. Seguridad  
20. Licencia  
21. Limitaciones y conclusiones  
22. Referencias y anexos

## 1. Introducción

Este informe presenta la evidencia de integración de los módulos de Iguaque QR.
La aplicación permite crear, personalizar, guardar y compartir códigos QR
mediante un flujo guiado de seis pasos.

## 2. Objetivos

### Objetivo general

Documentar la integración técnica y funcional de Iguaque QR para la evidencia
GA8-220501096-AA1-EV02.

### Objetivos específicos

- Describir módulos, entradas, procesos, salidas y dependencias.
- Documentar la persistencia local y la configuración de compilación.
- Registrar resultados reales de compilación y pruebas observadas.
- Entregar un ejecutable debug reproducible y documentación de soporte.

## 3. Alcance

El alcance comprende bienvenida, entrada y confirmación de texto o URL,
personalización de forma y marco, evaluación de contraste, generación con
ZXing, presentación del resultado, guardado, compartir y persistencia local.
La lectura interna, cámara y escaneo no forman parte del producto.

## 4. Descripción del problema y solución

El problema abordado es la necesidad de crear códigos QR personalizados sin
perder legibilidad y conservar sus configuraciones y contenidos. Iguaque QR
resuelve esto con un asistente de seis pasos, un renderizador QR, validación de
contraste, almacenamiento Room y acciones Android de guardado y compartir.

## 5. Requerimientos

### Requerimientos funcionales

- RF-01: mostrar bienvenida e identidad de la aplicación.
- RF-02: recibir texto o URL.
- RF-03: confirmar el contenido y el destino.
- RF-04: seleccionar forma, marco, colores y corrección.
- RF-05: evaluar contraste y legibilidad.
- RF-06: generar y mostrar el código QR.
- RF-07: guardar el QR como imagen.
- RF-08: compartir el QR mediante Android.
- RF-09: persistir y consultar los QR generados en historial local.

### Requerimientos no funcionales

- RNF-01: aplicación Android con interfaz Jetpack Compose.
- RNF-02: persistencia local sin servidor propio.
- RNF-03: separación entre código original y dependencias de terceros.
- RNF-04: configuración debug identificable con sufijo `.test`.
- RNF-05: secretos y configuración local excluidos del paquete.
- RNF-06: distribución bajo GPL-3.0-or-later.

## 6. Matriz de trazabilidad

| Requisito | Módulo | Evidencia | Estado |
|---|---|---|---|
| RF-01 | Bienvenida | Captura y prueba CP-02 | Aprobado |
| RF-02/RF-03 | Entrada y destino | Capturas del flujo | Aprobado |
| RF-04 | Forma y marco | Capturas del flujo | Aprobado |
| RF-05 | Legibilidad | Código y pantalla del asistente | Pendiente de registro individual |
| RF-06 | Renderizado | APK y QR generado | Aprobado |
| RF-07 | Guardado | Código compilado | Pendiente de prueba manual formal |
| RF-08 | Compartir | Código compilado | Pendiente de prueba manual formal |
| RF-09 | Room e historial | Confirmación visual y código | Aprobado |

## 7. Arquitectura del sistema

La aplicación utiliza una arquitectura de presentación Compose con `MainActivity`
y pantallas, un `QRViewModel` para el estado y las operaciones, `QRRenderer`
para la generación, y una capa Room compuesta por `QRDatabase`, `QRHistory` y
`QRHistoryDao`. `FileProvider`, MediaStore e intents Android soportan compartir
y guardar.

## 8. Tecnologías

Kotlin, Gradle, Android SDK 36, Android Gradle Plugin 8.7.3, Jetpack Compose,
Material 3, Navigation Compose, ZXing Core 3.5.2, Room 2.5.2, Coil Compose,
JUnit y AndroidX Test/Espresso.

## 9. Módulos integrados

1. Bienvenida e identidad.
2. Entrada de texto o URL.
3. Detección y confirmación del contenido.
4. Personalización de formas.
5. Selección de marcos.
6. Evaluación de contraste y legibilidad.
7. Generación mediante ZXing.
8. Resultado.
9. Guardado.
10. Compartir.
11. Persistencia y consulta del historial de QR generados.

## 10. Entradas, procesos y salidas

| Módulo | Entrada | Proceso | Salida | Componente | Dependencias | Integración |
|---|---|---|---|---|---|---|
| Bienvenida | Apertura | Presenta identidad | Acceso al creador | WelcomeScreen | Compose | Integrada |
| Contenido | Texto/URL | Captura y valida | Contenido confirmado | HomeScreen/ViewModel | Compose | Integrada |
| Personalización | Forma/marco/colores | Actualiza estado | Configuración QR | HomeScreen | Compose | Integrada |
| Legibilidad | Contraste | Evalúa combinación | Advertencia o aprobación | HomeScreen | Kotlin/Compose | Integrada |
| Generación | Configuración | QRCodeWriter produce bitmap | QR renderizado | QRRenderer | ZXing | Integrada |
| Resultado | Bitmap | Presenta acciones | QR listo | HomeScreen | Compose | Integrada |
| Guardado | Bitmap | Escribe PNG y MediaStore | Imagen local | QRViewModel | AndroidX | Integrada |
| Compartir | Bitmap | FileProvider e intent | Selector Android | QRViewModel | AndroidX | Integrada |
| Historial | Registro generado | Room inserta/consulta | Lista persistente | QRDatabase/HistoryScreen | Room | Aprobada visualmente |

## 11. Flujo completo de generación

La persona abre la aplicación, revisa la bienvenida, introduce texto o URL,
confirma el destino, selecciona forma y marco, revisa contraste y legibilidad,
genera el QR, visualiza el resultado y puede guardarlo o compartirlo. El
contenido generado se inserta en Room con `type = "generated"`.

## 12. Persistencia con Room

La base local se llama `qr_art_database`, usa la tabla `qr_history` y mantiene
la versión de esquema 1. La entidad `QRHistory` y `QRHistoryDao` se conservan
sin cambio estructural. La persistencia no depende de red.

## 13. Dependencias y licencias

Las dependencias se diferencian del código original en
[`THIRD_PARTY_NOTICES.md`](../../THIRD_PARTY_NOTICES.md). ZXing Core se usa para
generar QR. Room gestiona el historial; Compose la interfaz; Coil la carga de
imágenes; JUnit y Espresso están declarados para pruebas. La licencia del
proyecto es GPL-3.0-or-later y cada componente conserva sus condiciones.

## 14. Ambientes y configuración

### Desarrollo

Windows, JDK 21, Gradle Wrapper 8.9, Android SDK instalado localmente y
Android Studio/Gradle compatible.

### Pruebas

Xiaomi M2101K6G, Android 13, API 33. El paquete publicado
`com.diegodebian.iguaqueqr` permaneció instalado; la variante evaluativa fue
`com.diegodebian.iguaqueqr.test`.

### Compilación

Namespace `com.qrart`, `compileSdk = 36`, `targetSdk = 36`, `minSdk = 26`,
`versionCode = 2`, `versionName = 1.0.1`. Debug usa `applicationIdSuffix =
".test"`, `versionNameSuffix = "-test"` y el nombre `Iguaque QR — Pruebas`.

## 15. Pruebas y resultados reales

| Caso | Resultado |
|---|---|
| CP-01 Apertura | Aprobado por confirmación visual |
| CP-02 Bienvenida | Aprobado por flujo observado |
| CP-03 a CP-15 | Pendiente de registro individual |
| CP-16 Generación | Aprobado; QR generado |
| CP-17/CP-18 Validación externa | Pendiente de evidencia externa |
| CP-19 Guardado | Pendiente de prueba formal |
| CP-20 Compartir | Pendiente de prueba formal |
| CP-21 Historial | Aprobado por confirmación visual; QR visible |
| CP-22 Cierre y reapertura | Pendiente de registro individual |
| NAV-01 Barra visible | Aprobado por confirmación explícita desbloqueado |
| NAV-02 Solo Crear QR e Historial | Aprobado por confirmación explícita |
| NAV-03 Regreso a Crear QR | Aprobado por confirmación explícita |
| NAV-04 Botones visibles | Aprobado por confirmación explícita |

No se marcan como aprobadas pruebas basadas únicamente en inspección de código.
La lectura externa valida el QR producido, pero no es función interna.

## 16. Ejecutable

Archivo: `app-debug.apk`  
Paquete: `com.diegodebian.iguaqueqr.test`  
Versión: `1.0.1-test`  
SHA-256: `EFA6D69F4307A113C3AE4799240C0E682E8D17C26C09D1D0A0FAA18909F6841C`

Es un APK debug de evaluación. No es un release firmado para publicación.

## 17. Control de versiones

URL del repositorio: pendiente de publicación. No se inicializó Git, no se
creó remoto y no se realizó commit o push. El listado propuesto para incluir se
encuentra en `04_Repositorio/LISTADO_INCLUSION.txt`.

## 18. Manual técnico y de compilación

Consultar `DOCUMENTACION_TECNICA.md` y `MANUAL_TECNICO.md`. Para compilar en un
ambiente configurado: establecer la ubicación local del Android SDK, ejecutar
`gradlew.bat assembleDebug`, revisar el APK y verificar su hash. No incluir
`local.properties`, llaves ni contraseñas.

## 19. Seguridad

La copia limpia no contiene `.jks`, `keystore.properties`, `local.properties`,
`gradle.zip`, contraseñas ni archivos privados. Las carpetas `build` y `.gradle`
están excluidas. La firma release no fue utilizada ni modificada.

## 20. Licencia

Iguaque QR se distribuye bajo la GNU General Public License versión 3 o
posterior (`GPL-3.0-or-later`). El texto completo se encuentra en `LICENSE`.

## 21. Limitaciones y conclusiones

El proyecto no contiene pruebas unitarias implementadas; Gradle informa
`NO-SOURCE`. Las pruebas manuales restantes requieren registro humano. Existen
documentos UX históricos que mencionan escaneo y deben excluirse o actualizarse
antes de una entrega documental definitiva. La integración principal de
generación, persistencia, guardado, compartir y navegación confirmada queda
documentada.

## 22. Referencias y anexos

- `README.md`.
- `MANUAL_TECNICO.md`.
- `MATRIZ_PRUEBAS.md`.
- `REQUERIMIENTOS.md`.
- `THIRD_PARTY_NOTICES.md`.
- `LICENSE` y `NOTICE.md`.
- Capturas proporcionadas: figuras 1 a 6, fuente elaboración propia.

El detalle de títulos, explicaciones y casos relacionados está en
`ANEXO_CAPTURAS.md`. Las imágenes no están disponibles como archivos en el
espacio de trabajo actual y deben insertarse manualmente cuando se entreguen.
