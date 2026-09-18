# Incidencia de Compose UI Test

## Dispositivo

- Fabricante: Xiaomi.
- Modelo: M2101K6G.
- Android: 13.
- API: 33.
- Variante objetivo: `com.diegodebian.iguaqueqr.test`.
- Runner: `androidx.test.runner.AndroidJUnitRunner`.

## Síntoma

La actividad `com.qrart.MainActivity` de la variante `.test` abre correctamente cuando se inicia de forma directa. Sin embargo, al ejecutar `MainFlowInstrumentedTest.welcomeOpensCreator`, AndroidJUnitRunner termina el proceso objetivo y Compose Test informa:

```text
No compose hierarchies found in the app
```

El reporte anterior también registró:

```text
INSTRUMENTATION_FAILED / Process crashed
```

## Diagnóstico

En la única ejecución diagnóstica final se limpió únicamente `logcat`. El dispositivo informó inicialmente `mWakefulness=Dozing`. El registro de MIUI mostró posteriormente:

- `mKeyguardShowing=true`;
- `mAodShowing=true`;
- `mDreamingLockscreen=true`;
- `isKeyguardLocked=true`.

Además, el log mostró que `ActivityScenario` recibió una actividad ya abierta con un `Intent` distinto al que esperaba el runner. La actividad objetivo pasó a estado invisible y AndroidX lanzó `InstrumentationActivityInvoker$EmptyActivity`. Al perderse la jerarquía Compose, la prueba terminó con `IllegalStateException`.

## Clasificación

Incidencia de infraestructura del runner en el entorno Xiaomi/MIUI. No demuestra un fallo funcional de la aplicación.

No se identificó una corrección segura dentro de los ajustes autorizados que resolviera el problema sin alterar el entorno del teléfono o modificar la estrategia de ejecución.

## Evidencia y XML

La actividad objetivo abrió correctamente en el diagnóstico. No se generó un XML nuevo para la ejecución directa de AndroidJUnitRunner. El reporte existente de Gradle registra `welcomeOpensCreator` como `FAILED` por `INSTRUMENTATION_FAILED`, pero el cierre documental lo clasifica técnicamente como fallo de infraestructura y no como fallo funcional.

## Decisión de corte

Se detienen los reintentos de Compose UI. Las cinco pruebas permanecen en el proyecto y se clasifican como no ejecutadas por incompatibilidad del runner de instrumentación en el dispositivo Xiaomi/MIUI.

Las validaciones manuales existentes de la interfaz se mantienen como evidencia complementaria, sin convertirlas en pruebas automatizadas.

## Seguridad y alcance

- No se desactivó la seguridad del teléfono.
- No se desinstalaron aplicaciones.
- No se borraron datos.
- No se modificó la aplicación publicada.
- No se realizó commit ni push.
