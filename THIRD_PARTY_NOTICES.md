# Avisos de componentes de terceros

Este proyecto diferencia el código original de Iguaque QR de las dependencias
de terceros declaradas en `app/build.gradle.kts`. Las versiones corresponden a
las encontradas en la configuración auditada.

| Nombre | Organización | Versión encontrada | Uso en el proyecto | Licencia confirmada | Fuente oficial |
| --- | --- | --- | --- | --- | --- |
| AndroidX Core, Activity, Lifecycle y Navigation | Android Open Source Project / Google | Core KTX 1.10.1; Activity Compose 1.7.2; Lifecycle 2.6.1; Navigation Compose 2.6.0 | APIs Android, ciclo de vida, actividad Compose y navegación | Apache-2.0 | [AndroidX](https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt) |
| Jetpack Compose | Android Open Source Project / Google | BOM 2023.08.00; compilador 1.5.5 | Interfaz declarativa, Material 3, gráficos, iconos y tooling | Apache-2.0 | [AndroidX](https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt) |
| ZXing Core | ZXing authors | 3.5.2 | Generación de matrices QR mediante QRRenderer | Apache-2.0 | [Repositorio y POM de ZXing](https://github.com/zxing/zxing/blob/master/pom.xml) |
| Room | Android Open Source Project / Google | 2.5.2 | Base de datos local e historial | Apache-2.0 | [Room](https://github.com/androidx-releases/Room/blob/master/LICENSE) |
| Coil Compose | Coil contributors | 2.4.0 | Carga de imágenes para Compose | Apache-2.0 | [Repositorio de Coil](https://github.com/coil-kt/coil) |
| JUnit | JUnit team | 4.13.2 | Pruebas unitarias declaradas | EPL-1.0 | [POM y licencia de JUnit 4](https://github.com/junit-team/junit4/blob/main/pom.xml) |
| Espresso / AndroidX Test | Android Open Source Project / Google | Espresso Core 3.5.1; AndroidX Test JUnit 1.1.5 | Pruebas instrumentadas declaradas | Apache-2.0 | [Repositorio Android Test](https://github.com/android/android-test) |

Las licencias anteriores se registran para las coordenadas declaradas en
Gradle. CameraX y ML Kit Barcode Scanning fueron retirados. También se retiró
el módulo residual de lectura por estar fuera del alcance funcional; ZXing Core
se conserva exclusivamente para la generación. Las dependencias transitivas y
los artefactos exactos resueltos deberán volver a verificarse cuando se realice
la compilación autorizada.
