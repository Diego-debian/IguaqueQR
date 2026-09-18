# Resultados de pruebas — GA8-220501096-AA2-EV02

## Cierre técnico

Se ejecutaron 22 pruebas automatizadas y todas fueron aprobadas. Las cinco pruebas Compose UI permanecen diseñadas en el proyecto, pero no se ejecutaron por una incompatibilidad del runner de instrumentación con el estado de bloqueo/AOD del dispositivo Xiaomi/MIUI.

> 22 pruebas automatizadas ejecutadas y aprobadas; cinco pruebas UI adicionales quedaron sin ejecución por fallo del runner de instrumentación en el dispositivo Xiaomi/MIUI.

| Grupo | Diseñadas | Ejecutadas | Aprobadas | Fallidas | No ejecutadas |
|---|---:|---:|---:|---:|---:|
| JVM | 13 | 13 | 13 | 0 | 0 |
| Room | 4 | 4 | 4 | 0 | 0 |
| QRRenderer | 5 | 5 | 5 | 0 | 0 |
| Compose UI | 5 | 0 | 0 | 0 | 5 |
| **Total** | **27** | **22** | **22** | **0** | **5** |

## Cobertura comprobada

- JVM: clasificación de contenido y cálculo de contraste.
- Room: inserción, consulta ordenada, eliminación individual, limpieza y base vacía.
- QRRenderer: dimensiones, estilos y marcos, niveles de corrección, contenido Unicode, contenido largo y rechazo de contenido vacío.
- Compose UI diseñada: bienvenida, entrada al creador, bloqueo de entrada vacía, navegación del asistente, generación y navegación al historial. No se presenta como cobertura automatizada ejecutada.
- Las verificaciones manuales de navegación e historial se conservan como evidencia complementaria independiente.

## Verificaciones de construcción

- `testDebugUnitTest`: exitoso; 13/13.
- Pruebas instrumentadas Room: exitosas; 4/4.
- Pruebas instrumentadas QRRenderer: exitosas; 5/5.
- `assembleDebug`: exitoso.
- `lintDebug`: exitoso, con advertencias no bloqueantes existentes.

## Corrección aplicada previamente

Se corrigió la codificación Unicode de ZXing mediante el hint oficial `EncodeHintType.CHARACTER_SET = "UTF-8"`. No se modificaron estilos, marcos, colores, dimensiones, Room ni navegación durante este cierre.

## Estado de entrega

No se realizó commit, push ni publicación. La aplicación publicada no fue modificada.
