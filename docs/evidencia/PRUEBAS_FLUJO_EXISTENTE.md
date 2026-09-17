# Pruebas del flujo existente — GA8-220501096-AA1-EV02

Fecha de registro: 17 de septiembre de 2026  
Aplicación: Iguaque QR  
Variante probada: `com.diegodebian.iguaqueqr.test` (`1.0.1-test`)  
Dispositivo: Xiaomi M2101K6G, Android 13, API 33  

## Alcance

Iguaque QR crea, personaliza, guarda y comparte códigos QR mediante un flujo
guiado de seis pasos. La validación de legibilidad del resultado puede hacerse
con un lector externo; la aplicación no incorpora lectura interna.

## Resultados confirmados

| Caso | Verificación | Resultado | Evidencia |
|---|---|---|---|
| CP-01 | Apertura de la aplicación | Aprobado | Prueba controlada en teléfono |
| CP-02 | Pantalla informativa inicial | Aprobado | Flujo observado en teléfono |
| CP-16 | Generación del QR | Aprobado | QR generado y mostrado |
| CP-21 | Consulta del historial | Aprobado | QR generado visible en `HistoryScreen` |
| NAV-01 | Barra inferior visible | Aprobado | Confirmación visual del usuario |
| NAV-02 | Barra con solo Crear QR e Historial | Aprobado | Confirmación visual del usuario |
| NAV-03 | Regreso a Crear QR | Aprobado | Confirmación visual del usuario |
| NAV-04 | Botones del asistente visibles | Aprobado | Confirmación visual del usuario |

## Casos pendientes de ejecución documentada

CP-03 a CP-15, CP-17 a CP-20 y CP-22 requieren registro manual individual con
datos de entrada, captura y resultado. No se marcan como aprobados por
inferencia.

## Casos fuera del alcance

No aplican permiso de cámara, escaneo interno, rotación de cámara, iluminación,
distancia ni apertura de URL escaneada. Esas funciones fueron retiradas del
producto.

## Estado de instalación

La variante de pruebas fue actualizada conservando sus datos. La aplicación
publicada `com.diegodebian.iguaqueqr` permaneció instalada sin modificaciones.

APK debug:

`app/build/outputs/apk/debug/app-debug.apk`

SHA-256:

`EFA6D69F4307A113C3AE4799240C0E682E8D17C26C09D1D0A0FAA18909F6841C`
