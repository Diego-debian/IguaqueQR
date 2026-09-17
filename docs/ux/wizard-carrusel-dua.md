# Iguaque QR — flujo guiado tipo instalador

Estado: propuesta UX en rama `design/wizard-carrusel-dua`  
Alcance: análisis, arquitectura de interacción y boceto; no cambia todavía el comportamiento de producción.

## Veredicto

Sí es viable convertir la creación del QR en un flujo de varias pantallas. La app ya tiene navegación principal para `Crear QR`, `Escanear` e `Historial`; por tanto, el cambio puede vivir dentro de `Crear QR` como un asistente interno sin rehacer el `NavHost` principal.

La recomendación es un carrusel controlado por pasos, con avance y retroceso explícitos. El gesto horizontal puede existir como atajo, pero nunca debe ser la única forma de avanzar: esto mantiene el flujo descubrible, accesible y predecible.

## Qué existe hoy

| Zona | Evidencia | Consecuencia para el rediseño |
| --- | --- | --- |
| Navegación | `MainActivity.kt` ya separa Crear QR, Escanear e Historial | Se conserva la navegación global y se transforma solo Crear QR |
| Creación | `HomeScreen.kt` concentra contenido, logo, color, puntos, marcos, corrección, preview y acciones en un `Column` con scroll | Hay carga cognitiva y poca sensación de avance |
| Estado | `QRViewModel.kt` ya mantiene texto, colores, estilo, marco, nivel de error, logo y bitmap generado | Se puede añadir un `CreateStep` sin duplicar el borrador |
| Estilos | `QRRenderer.kt` tiene 14 formas de puntos y 9 marcos | Conviene mostrarlos como miniaturas visuales con nombre, no como emojis |
| Tema | `Color.kt` usa morado/rosa/azul y tarjetas tipo glass | Se puede conservar energía cromática, pero con roles semánticos y mejor contraste |
| Resultado | `QRViewModel` ya guarda en historial, comparte y exporta | El último paso debe ser una pantalla de resultado, no otro bloque al final del scroll |

## Flujo propuesto

```text
┌──────────────┐   ┌────────────────┐   ┌────────────────┐   ┌─────────────────┐   ┌──────────────┐
│ 1. Enlace    │ → │ 2. Destino │ → │ 3. Forma │ → │ 4. Marco │ → │ 5. Lectura │ → │ 6. Resultado │
│ contenido   │   │ confirma  │   │ módulos │   │ exterior │   │ contraste  │   │ generar     │
└──────────────┘   └────────────────┘   └────────────────┘   └─────────────────┘   └──────────────┘
        ↑                 ↑                    ↑                    ↑                     │
        └─────────────────┴────────────────────┴────────────────────┴──── Editar ─────────┘
```

### Paso 1 — Enlace primero

Objetivo: comenzar por la acción principal y reducir la pantalla a una decisión clara.

- Título: `¿Qué quieres convertir en QR?`
- Campo grande para URL o texto.
- Acción secundaria: `Pegar desde el portapapeles`.
- Detección amable: indicar si parece una URL, texto libre o enlace incompleto; no bloquear texto libre porque el producto genera QR de cualquier contenido.
- CTA: `Continuar`.

### Paso 2 — Confirma el destino

Objetivo: hacer visible qué se va a codificar antes de entrar a la personalización.

- Mostrar dominio o primeras líneas del contenido.
- Mostrar tipo detectado: `Enlace`, `Texto`, `Contacto` o `Otro`.
- Permitir `Editar contenido` sin perder el borrador.
- CTA: `Usar este contenido`.

### Paso 3 — Forma

Objetivo: elegir la forma de los módulos en una pantalla dedicada.

- Mostrar las formas de los módulos como miniaturas visuales con nombre.
- La pantalla solo modifica `dotStyle`; no mezcla la decisión con los marcos.
- Mantener un preview pequeño y vivo para que la decisión tenga respuesta inmediata.

### Paso 4 — Marco

Objetivo: elegir la decoración exterior del QR sin competir con la decisión de forma.

- Mostrar los nueve marcos en una pantalla independiente.
- Cada opción combina miniatura + nombre + estado seleccionado; nunca depende del emoji.
- `Ninguno` debe ser una opción visible y fácil de recuperar.

### Paso 5 — Accesibilidad y lectura

Objetivo: evitar que una personalización atractiva produzca un QR difícil de leer.

- Selector de color de QR y fondo con valor hexadecimal visible.
- Indicador textual de contraste: `Buena lectura`, `Revisa el contraste` o `No recomendado`.
- No comunicar el estado solo por color: incluir icono, texto y borde.
- Carga opcional de logo.
- Nivel de corrección H preseleccionado cuando hay logo; explicar el motivo en lenguaje simple.
- Acción `Usar configuración segura` para recuperar una combinación legible.

### Paso 6 — Revisa y genera

Objetivo: convertir la configuración en una decisión final clara.

- Preview grande y centrado.
- Resumen compacto: destino, estilo, logo y estado de lectura.
- CTA primario: `Crear código QR`.
- Tras generar: `Guardar`, `Compartir`, `Crear otro` y `Ver historial`.
- La generación conserva el comportamiento actual de `QRViewModel.generateQR()` y el guardado en Room.

## Comportamiento del carrusel

1. Mostrar siempre `Paso n de 6` junto con el nombre del paso. Las barras o puntos son apoyo visual, no el único indicador.
2. `Continuar` queda abajo, en una zona estable; `Atrás` queda visible desde el paso 2.
3. El gesto de deslizar es opcional y debe respetar los mismos validadores que los botones.
4. El botón del sistema Atrás vuelve al paso anterior antes de salir del creador.
5. Si se abandona el flujo, ofrecer `Continuar borrador` y `Descartar`.
6. El error aparece junto al campo que lo causa, con una solución concreta.
7. Los objetivos táctiles deben ser de al menos 48 dp y el foco debe seguir un orden lógico.
8. No usar texto diminuto, transparencia excesiva ni estados que dependan solo de color.

## Arquitectura recomendada

### Opción elegida: estado de flujo dentro de Crear QR

No se recomienda crear seis rutas globales nuevas. Es mejor mantener `Screen.Home` y añadir un estado de flujo en el ViewModel o en un `CreateWizardState`:

```kotlin
enum class CreateStep {
    CONTENT, DESTINATION, SHAPE, FRAME, ACCESSIBILITY, REVIEW
}

data class QRDraft(
    val text: String,
    val dotStyle: DotStyle,
    val frameType: FrameType,
    val errorLevel: ErrorLevel,
    val qrColor: Int,
    val bgColor: Int,
    val logoBitmap: Bitmap?
)
```

La pantalla puede usar `AnimatedContent` para las transiciones y mantener un único borrador. `generateQR()`, `saveQRToGallery()`, `shareQR()` e historial siguen siendo servicios del ViewModel. El preview se actualiza de forma controlada, no en cada pulsación pesada si eso afecta el rendimiento.

### Por qué no seis rutas nuevas

- Evita duplicar el mismo ViewModel y los argumentos de navegación.
- Hace más sencillo restaurar el borrador.
- Permite que Atrás signifique “volver un paso”.
- Conserva los deep links y la navegación global para Escanear e Historial.

Si en una fase futura se necesita abrir directamente un paso concreto desde una notificación o un enlace externo, el estado `CreateStep` puede exponerse como ruta sin cambiar el modelo de datos.

## Paleta DUA propuesta

DUA aquí significa Diseño Universal para el Aprendizaje: múltiples formas de comprender, actuar y recibir retroalimentación. La paleta se usa por roles, no como decoración infantil.

| Rol | Color | Uso |
| --- | --- | --- |
| Tinta principal | `#172033` | Texto, iconos y módulos QR |
| Fondo | `#F6F8FB` | Lienzo general, baja fatiga visual |
| Superficie | `#FFFFFF` | Tarjetas y campos |
| Acción | `#2557D6` | CTA, foco y progreso activo |
| Acción alternativa | `#0F766E` | Escanear, configuración segura, éxito |
| Atención | `#B45309` | Avisos que requieren revisión; usar texto oscuro |
| Error | `#C2410C` | Errores y acciones destructivas |
| Acento | `#6D28D9` | Destacar estilo sin convertirlo en señal obligatoria |
| Línea/foco suave | `#D7DEE8` / `#0EA5E9` | Separación y foco visible |

Reglas de accesibilidad visual:

- Cada estado debe tener texto o icono además del color.
- Los controles seleccionados usan borde de 2 dp y una marca, no solo un relleno.
- El modo oscuro debe conservar los mismos roles semánticos, ajustando luminosidad.
- La personalización del QR debe advertir si el color elegido reduce la legibilidad.
- La identidad visual se apoya en ritmo, contraste y movimiento corto, no en degradados constantes.

## Ritmo visual

- Transiciones de 180–240 ms: desplazamiento horizontal corto + desvanecido leve.
- Una sola tarjeta principal por paso, con jerarquía clara.
- Preview siempre visible en miniatura; preview grande solo en revisión.
- Fondo limpio con pequeños puntos de navegación; los puntos indican progreso, no son adornos.
- Tipografía con titulares firmes y cuerpo cómodo; evitar mayúsculas sostenidas.
- Iconos lineales consistentes; eliminar emojis de la selección de estilos.

## Plan de implementación por cortes

1. Crear `CreateStep`, `QRDraft` y validadores sin cambiar el renderer.
2. Extraer el contenido de `HomeScreen` a seis composables de paso.
3. Añadir contenedor del wizard, progreso, Atrás, Continuar y manejo de abandono.
4. Reemplazar emojis por miniaturas dibujadas con Compose para puntos y marcos.
5. Añadir el validador de contraste y el estado textual DUA.
6. Mover Guardar/Compartir al resultado final y probar restauración desde Historial.
7. Probar TalkBack, fuente grande, modo oscuro, teclado, rotación y pantalla pequeña.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
| --- | --- |
| El usuario siente que son demasiados pasos | Mantener seis como máximo, mostrar resumen y permitir volver sin perder datos |
| El carrusel se interpreta como obligatorio | Permitir `Saltar a revisión` solo cuando los mínimos estén completos |
| Un estilo rompe la lectura del QR | Validar contraste y reservar los patrones finder para el renderer |
| El preview consume demasiado | Regenerar al confirmar un paso o con debounce corto |
| Estado perdido al rotar | Guardar el draft en `SavedStateHandle` o estado persistente del ViewModel |
| Iconos/emoji se ven infantiles o se rompen por codificación | Usar formas vectoriales y texto UTF-8 validado |

## Criterios de aceptación del rediseño

- Una persona puede pegar un enlace y llegar al resultado en seis decisiones o menos.
- Cada pantalla explica qué se espera y qué ocurrirá después.
- Atrás vuelve un paso sin borrar el borrador.
- El usuario puede pegar o escribir el contenido que quiere convertir en QR.
- El estado de contraste se entiende sin distinguir colores.
- El flujo funciona con TalkBack, fuente grande y navegación por teclado/acciones IME.
- Crear QR sigue guardando el mismo registro en historial y conserva Guardar/Compartir.
- Escanear e Historial siguen disponibles desde la navegación principal.

## Boceto

El boceto editable está en [`wizard-carrusel-dua.svg`](wizard-carrusel-dua.svg). Es deliberadamente de baja fidelidad: líneas, módulos y puntos para discutir jerarquía, ritmo y flujo antes de invertir en componentes finales.
