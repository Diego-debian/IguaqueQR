# Iguaque QR

Aplicación Android para crear, personalizar, guardar y compartir códigos QR
mediante un flujo guiado de seis pasos.

SPDX-License-Identifier: GPL-3.0-or-later

## Licencia

Iguaque QR se distribuye bajo la GNU General Public License versión 3 o
posterior (`GPL-3.0-or-later`).

Copyright © 2026 Diego Alberto Parra Garzón.

Puede consultar el texto completo en el archivo [LICENSE](LICENSE).

## Funciones

- Entrada de texto o URL y confirmación del contenido.
- Personalización de forma, marco, colores y nivel de corrección.
- Evaluación de legibilidad y contraste.
- Renderizado, guardado, compartir e historial local de códigos generados.

El módulo residual de lectura fue retirado por estar fuera del alcance funcional
del producto. La validación de legibilidad puede realizarse con un lector
externo, sin constituir una función interna de Iguaque QR.

## Historial de cambios de dependencias

Se retiró ML Kit Barcode Scanning y posteriormente el módulo residual de
lectura. ZXing Core se conserva porque QRRenderer lo utiliza para generar
códigos QR.

## Pruebas de generación

La matriz de pruebas contempla apertura, bienvenida, entrada y validación de
texto/URL, confirmación, personalización, contraste, generación, validación con
un lector externo, guardado, compartir, historial y reapertura. La lectura
externa solo valida el QR generado y no es una función interna.
