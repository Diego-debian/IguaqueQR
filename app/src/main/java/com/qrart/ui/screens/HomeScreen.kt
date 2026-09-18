package com.qrart.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import java.util.Locale
import com.qrart.drawing.DotStyle
import com.qrart.drawing.ErrorLevel
import com.qrart.drawing.FrameType
import com.qrart.logic.ContrastUtils
import com.qrart.logic.classifyContent
import com.qrart.ui.components.ErrorLevelCard
import com.qrart.ui.components.GlassCard
import com.qrart.ui.components.InfoBanner
import com.qrart.ui.components.QRPreview
import com.qrart.ui.components.GradientButton
import com.qrart.ui.theme.DUAAccent
import com.qrart.ui.theme.DUAAttention
import com.qrart.ui.theme.DUABlue
import com.qrart.ui.theme.DUACanvas
import com.qrart.ui.theme.DUAError
import com.qrart.ui.theme.DUAInk
import com.qrart.ui.theme.DUALine
import com.qrart.ui.theme.DUATeal
import com.qrart.ui.theme.DUASurface
import com.qrart.viewmodel.QRViewModel

private enum class CreateStep(val number: Int, val label: String) {
    CONTENT(1, "Enlace"),
    DESTINATION(2, "Destino"),
    SHAPE(3, "Forma"),
    FRAME(4, "Marco"),
    ACCESSIBILITY(5, "Lectura"),
    REVIEW(6, "Resultado")
}

@Composable
fun HomeScreen(
    viewModel: QRViewModel,
    onShowInfo: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    var stepIndex by rememberSaveable { mutableIntStateOf(0) }
    val step = CreateStep.values()[stepIndex]

    LaunchedEffect(step) {
        scrollState.scrollTo(0)
    }

    val logoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = context.contentResolver.openInputStream(it)?.use(BitmapFactory::decodeStream)
            viewModel.logoBitmap = bitmap
            if (bitmap != null) viewModel.errorLevel = ErrorLevel.H
        }
    }

    fun goBack() {
        if (stepIndex > 0) {
            if (step == CreateStep.REVIEW) viewModel.generatedBitmap = null
            stepIndex--
        }
    }

    BackHandler(enabled = stepIndex > 0) {
        goBack()
    }

    fun goNext() {
        if (step == CreateStep.CONTENT && viewModel.text.isBlank()) {
            viewModel.errorMessage = "Introduce un enlace o texto para continuar"
            return
        }
        viewModel.clearError()
        if (stepIndex < CreateStep.values().lastIndex) stepIndex++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DUACanvas)
    ) {
        WizardHeader(
            step = step,
            onBack = if (stepIndex > 0) ::goBack else null,
            onInfo = onShowInfo
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            when (step) {
                    CreateStep.CONTENT -> ContentStep(
                        viewModel = viewModel,
                        onPaste = {
                            clipboard.getText()?.text?.let { pasted ->
                                if (pasted.isNotBlank()) viewModel.text = pasted
                            }
                        },
                        onNext = ::goNext
                    )
                    CreateStep.DESTINATION -> DestinationStep(
                        text = viewModel.text,
                        onEdit = { stepIndex = CreateStep.CONTENT.ordinal }
                    )
                    CreateStep.SHAPE -> ShapeStep(
                        viewModel = viewModel
                    )
                    CreateStep.FRAME -> FrameStep(
                        viewModel = viewModel
                    )
                    CreateStep.ACCESSIBILITY -> AccessibilityStep(
                        viewModel = viewModel,
                        onLogoPick = { logoLauncher.launch("image/*") }
                    )
                    CreateStep.REVIEW -> ReviewStep(
                        viewModel = viewModel,
                        context = context,
                        onEdit = {
                            viewModel.generatedBitmap = null
                            stepIndex = CreateStep.SHAPE.ordinal
                        },
                        onCreateAnother = {
                            viewModel.generatedBitmap = null
                            viewModel.hasGenerated = false
                            viewModel.clearError()
                            stepIndex = CreateStep.CONTENT.ordinal
                        }
                    )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (step != CreateStep.REVIEW) {
            WizardFooter(
                onBack = if (stepIndex > 0) ::goBack else null,
                onNext = ::goNext,
                nextLabel = if (step == CreateStep.ACCESSIBILITY) "Revisar" else "Continuar"
            )
        }
    }
}

@Composable
private fun WizardHeader(
    step: CreateStep,
    onBack: (() -> Unit)?,
    onInfo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DUASurface)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = DUAInk)
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Iguaque QR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DUAInk
                )
                Text(
                    text = "Crea tu código paso a paso",
                    style = MaterialTheme.typography.bodySmall,
                    color = DUAInk.copy(alpha = 0.62f)
                )
            }
            androidx.compose.material3.IconButton(onClick = onInfo) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Información de Iguaque QR",
                    tint = DUAAccent
                )
            }
            Text(
                text = "${step.number} / ${CreateStep.values().size}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = DUABlue
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CreateStep.values().forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (item.ordinal <= step.ordinal) DUABlue else DUALine)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (item == step) FontWeight.Bold else FontWeight.Normal,
                        color = if (item.ordinal <= step.ordinal) DUABlue else DUAInk.copy(alpha = 0.48f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun WizardFooter(
    onBack: (() -> Unit)?,
    onNext: () -> Unit,
    nextLabel: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DUASurface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (onBack != null) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(0.85f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DUABlue)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text("Atrás")
            }
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1.15f).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DUABlue,
                contentColor = Color.White
            )
        ) {
            Text(nextLabel, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun StepTitle(
    eyebrow: String,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 18.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFFE9EEFF)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = DUABlue,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = DUAInk,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DUAInk.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
private fun ContentStep(
    viewModel: QRViewModel,
    onPaste: () -> Unit,
    onNext: () -> Unit
) {
    val hasError = viewModel.text.isBlank() && viewModel.errorMessage != null

    StepTitle(
        eyebrow = "Paso 1 · Enlace",
        title = "Empieza por el contenido",
        subtitle = "Pega un enlace o escribe cualquier texto que quieras convertir en QR.",
        icon = { Icon(Icons.Outlined.TextFields, contentDescription = null, tint = DUABlue) }
    )

    GlassCard {
        OutlinedTextField(
            value = viewModel.text,
            onValueChange = {
                viewModel.text = it
                viewModel.clearError()
                viewModel.generatedBitmap = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("URL o texto") },
            placeholder = { Text("https://ejemplo.com/...") },
            minLines = 4,
            maxLines = 6,
            isError = hasError,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onNext() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DUAInk,
                unfocusedTextColor = DUAInk,
                focusedContainerColor = DUASurface,
                unfocusedContainerColor = DUASurface,
                focusedBorderColor = DUABlue,
                unfocusedBorderColor = DUALine,
                focusedLabelColor = DUABlue,
                cursorColor = DUABlue
            )
        )

        if (hasError) {
            Text(
                text = viewModel.errorMessage.orEmpty(),
                color = DUAError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        OutlinedButton(
            onClick = onPaste,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DUATeal)
        ) {
            Icon(Icons.Outlined.ContentPaste, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pegar desde el portapapeles")
        }
    }

}

@Composable
private fun DestinationStep(
    text: String,
    onEdit: () -> Unit
) {
    StepTitle(
        eyebrow = "Paso 2 · Destino",
        title = "Confirma el destino",
        subtitle = "Revisa el contenido antes de entrar a la parte visual.",
        icon = { Icon(Icons.Outlined.QrCode, contentDescription = null, tint = DUABlue) }
    )

    GlassCard {
        Text("TIPO DETECTADO", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(classifyContent(text), style = MaterialTheme.typography.titleLarge, color = DUAInk, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = DUACanvas),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = DUAInk,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedButton(onClick = onEdit, shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Editar contenido")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    InfoBanner(text = "Usaremos este contenido exactamente como aparece arriba.")
}

@Composable
private fun ShapeStep(
    viewModel: QRViewModel
) {
    StepTitle(
        eyebrow = "Paso 3 · Forma",
        title = "Elige la forma",
        subtitle = "Selecciona cómo quieres que se vean los módulos del código.",
        icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = DUAAccent) }
    )

    GlassCard {
        Text("PUNTOS", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        DotStyle.values().toList().chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { style ->
                    DotStyleCard(
                        style = style,
                        selected = viewModel.dotStyle == style,
                        onClick = { viewModel.dotStyle = style },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}

@Composable
private fun FrameStep(
    viewModel: QRViewModel
) {
    StepTitle(
        eyebrow = "Paso 4 · Marco",
        title = "Elige el marco",
        subtitle = "Añade un remate visual sin cambiar la forma de los módulos.",
        icon = { Icon(Icons.Outlined.Tune, contentDescription = null, tint = DUAAccent) }
    )

    GlassCard {
        Text("MARCOS", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "El marco se aplicará alrededor del QR y puedes quitarlo en cualquier momento.",
            style = MaterialTheme.typography.bodySmall,
            color = DUAInk.copy(alpha = 0.62f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        FrameType.values().toList().chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { frame ->
                    FrameChoice(
                        frame = frame,
                        selected = viewModel.frameType == frame,
                        onClick = { viewModel.frameType = frame },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    InfoBanner(text = "Elige Ninguno para mantener un QR limpio y sin decoración exterior.")
}

@Composable
private fun AccessibilityStep(
    viewModel: QRViewModel,
    onLogoPick: () -> Unit
) {
    val contrast = ContrastUtils.ratio(viewModel.qrColor, viewModel.bgColor)
    val readable = ContrastUtils.isReadable(contrast)

    StepTitle(
        eyebrow = "Paso 5 · Lectura",
        title = "Hazlo legible",
        subtitle = "La estética nunca debe impedir que una cámara lea el código.",
        icon = { Icon(Icons.Outlined.Shield, contentDescription = null, tint = DUATeal) }
    )

    ContrastStatus(readable = readable, ratio = contrast)

    Spacer(modifier = Modifier.height(12.dp))
    GlassCard {
        Text("COLOR DEL CÓDIGO", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ColorSwatches(
            selected = Color(viewModel.qrColor),
            colors = listOf(DUAInk, DUABlue, DUATeal),
            onSelect = { viewModel.qrColor = it.toArgb() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("COLOR DEL FONDO", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ColorSwatches(
            selected = Color(viewModel.bgColor),
            colors = listOf(Color.White, Color(0xFFF6F8FB), Color(0xFFE9EEFF)),
            onSelect = { viewModel.bgColor = it.toArgb() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${formatContrast(contrast)} de contraste",
            style = MaterialTheme.typography.bodySmall,
            color = DUAInk.copy(alpha = 0.62f)
        )
        if (!readable) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    viewModel.qrColor = DUAInk.toArgb()
                    viewModel.bgColor = Color.White.toArgb()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DUATeal)
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Usar configuración segura")
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = DUAAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Logo opcional", style = MaterialTheme.typography.titleMedium, color = DUAInk, fontWeight = FontWeight.Bold)
                Text(
                    text = if (viewModel.logoBitmap == null) "El nivel H se usará si añades uno." else "Logo cargado · corrección H activa",
                    style = MaterialTheme.typography.bodySmall,
                    color = DUAInk.copy(alpha = 0.62f)
                )
            }
            OutlinedButton(onClick = onLogoPick, shape = RoundedCornerShape(14.dp)) {
                Text(if (viewModel.logoBitmap == null) "Añadir" else "Cambiar")
            }
        }
        if (viewModel.logoBitmap != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { viewModel.logoBitmap = null }) {
                Text("Quitar logo", color = DUAError)
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    GlassCard {
        Text("CORRECCIÓN DE ERRORES", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Permite que el QR siga siendo legible si se daña o lleva un logo.",
            style = MaterialTheme.typography.bodySmall,
            color = DUAInk.copy(alpha = 0.62f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        ErrorLevel.values().toList().chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { level ->
                    ErrorLevelCard(
                        selected = viewModel.errorLevel == level,
                        onClick = { viewModel.errorLevel = level },
                        level = level.displayName,
                        description = level.description,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ContrastStatus(readable: Boolean, ratio: Float) {
    val background = if (readable) Color(0xFFE2F3F0) else Color(0xFFFFF4DF)
    val color = if (readable) DUATeal else DUAAttention
    val icon = if (readable) Icons.Outlined.CheckCircle else Icons.Outlined.WarningAmber
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    if (readable) "Buena lectura" else "Revisa el contraste",
                    style = MaterialTheme.typography.titleMedium,
                    color = DUAInk,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (readable) "La combinación tiene ${formatContrast(ratio)} de contraste." else "Una combinación más oscura y clara será más confiable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DUAInk.copy(alpha = 0.68f)
                )
            }
        }
    }
}

@Composable
private fun ReviewStep(
    viewModel: QRViewModel,
    context: android.content.Context,
    onEdit: () -> Unit,
    onCreateAnother: () -> Unit
) {
    val generated = viewModel.generatedBitmap != null

    StepTitle(
        eyebrow = "Paso 6 · Resultado",
        title = when {
            generated -> "Tu QR está listo"
            viewModel.hasGenerated -> "Genera la nueva versión"
            else -> "Listo para crear"
        },
        subtitle = when {
            generated -> "Guárdalo, compártelo o crea una nueva versión."
            viewModel.hasGenerated -> "Cambiaste el diseño; vuelve a generarlo para aplicar los cambios."
            else -> "Revisa el resumen antes de generar."
        },
        icon = { Icon(Icons.Outlined.QrCode, contentDescription = null, tint = DUABlue) }
    )

    if (viewModel.errorMessage != null) {
        ErrorNotice(viewModel.errorMessage.orEmpty())
        Spacer(modifier = Modifier.height(12.dp))
    }

    QRPreview(
        bitmap = viewModel.generatedBitmap,
        modifier = Modifier.fillMaxWidth().testTag("qr_preview")
    )

    Spacer(modifier = Modifier.height(12.dp))
    GlassCard {
        Text("RESUMEN", style = MaterialTheme.typography.labelSmall, color = DUABlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow("Destino", viewModel.text)
        SummaryRow("Puntos", dotLabel(viewModel.dotStyle))
        SummaryRow("Marco", frameLabel(viewModel.frameType))
        SummaryRow("Contraste", formatContrast(ContrastUtils.ratio(viewModel.qrColor, viewModel.bgColor)))
    }

    Spacer(modifier = Modifier.height(12.dp))
    if (viewModel.isGenerating) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = DUABlue)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Generando tu código QR...", color = DUAInk.copy(alpha = 0.68f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    } else if (!generated) {
        GradientButton(
            text = if (viewModel.hasGenerated) "Regenerar código QR" else "Crear código QR",
            icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color.White) },
            onClick = {
                viewModel.generatedBitmap = null
                viewModel.generateQR()
            },
            modifier = Modifier.fillMaxWidth().testTag("generate_qr"),
            gradientColors = listOf(DUABlue, DUAAccent)
        )
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { viewModel.shareQR(viewModel.generatedBitmap!!) },
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DUABlue)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Compartir")
            }
            Button(
                onClick = {
                    val message = viewModel.saveQRToGallery(viewModel.generatedBitmap!!)
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DUABlue)
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Guardar")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Ajustar") }
            TextButton(onClick = onCreateAnother, modifier = Modifier.weight(1f)) { Text("Crear otro") }
        }
    }

}

@Composable
private fun ErrorNotice(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E4)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = DUAError)
            Spacer(modifier = Modifier.width(8.dp))
            Text(message, color = DUAError, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.width(92.dp), color = DUAInk.copy(alpha = 0.58f), style = MaterialTheme.typography.bodySmall)
        Text(value, modifier = Modifier.weight(1f), color = DUAInk, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DotStyleCard(style: DotStyle, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .border(2.dp, if (selected) DUABlue else DUALine, RoundedCornerShape(13.dp))
            .background(if (selected) Color(0xFFE9EEFF) else DUASurface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DotPreview(style = style, modifier = Modifier.size(34.dp))
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = dotLabel(style),
            fontSize = 10.sp,
            color = if (selected) DUABlue else DUAInk.copy(alpha = 0.72f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FrameChoice(frame: FrameType, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, if (selected) DUABlue else DUALine, RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFE9EEFF) else DUASurface)
            .clickable(onClick = onClick)
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(frameLabel(frame), fontSize = 11.sp, color = if (selected) DUABlue else DUAInk.copy(alpha = 0.72f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DotPreview(style: DotStyle, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DUAInk)
    ) {
        // Un solo módulo grande hace que cada forma sea reconocible de inmediato.
        // La cuadrícula anterior reducía todas las figuras a un patrón parecido a un dado.
        drawPreviewModule(
            style = style,
            center = Offset(size.width / 2f, size.height / 2f),
            moduleSize = size.minDimension * 0.7f
        )
    }
}

private fun DrawScope.drawPreviewModule(
    style: DotStyle,
    center: Offset,
    moduleSize: Float
) {
    val color = if (style == DotStyle.CIRCLE) Color(0xFFDCE5FF) else Color.White
    val half = moduleSize / 2f
    val left = center.x - half
    val top = center.y - half

    when (style) {
        DotStyle.SQUARE -> drawRect(color, Offset(left, top), Size(moduleSize, moduleSize))
        DotStyle.CIRCLE -> drawCircle(color, half * 0.92f, center)
        DotStyle.HEART -> {
            val path = Path().apply {
                moveTo(center.x, top + moduleSize * 0.95f)
                cubicTo(left, top + moduleSize * 0.55f, left, top, center.x, top + moduleSize * 0.3f)
                cubicTo(center.x + half, top - half * 0.05f, left + moduleSize, top + moduleSize * 0.55f, center.x, top + moduleSize * 0.95f)
                close()
            }
            drawPath(path, color)
        }
        DotStyle.STAR -> drawStarPreview(center, half, color)
        DotStyle.PAW -> {
            drawCircle(color, half * 0.48f, Offset(center.x, center.y + half * 0.25f))
            drawCircle(color, half * 0.2f, Offset(center.x - half * 0.55f, center.y - half * 0.35f))
            drawCircle(color, half * 0.22f, Offset(center.x, center.y - half * 0.58f))
            drawCircle(color, half * 0.2f, Offset(center.x + half * 0.55f, center.y - half * 0.35f))
        }
        DotStyle.CAT -> {
            drawCircle(color, half * 0.62f, Offset(center.x, center.y + half * 0.12f))
            val ears = Path().apply {
                moveTo(left + half * 0.05f, center.y - half * 0.05f)
                lineTo(left + half * 0.15f, top)
                lineTo(center.x - half * 0.05f, center.y - half * 0.35f)
                close()
                moveTo(center.x + half * 0.05f, center.y - half * 0.35f)
                lineTo(left + moduleSize - half * 0.15f, top)
                lineTo(left + moduleSize - half * 0.05f, center.y - half * 0.05f)
                close()
            }
            drawPath(ears, color)
        }
        DotStyle.DIAMOND -> {
            val path = Path().apply {
                moveTo(center.x, top)
                lineTo(left + moduleSize, center.y)
                lineTo(center.x, top + moduleSize)
                lineTo(left, center.y)
                close()
            }
            drawPath(path, color)
        }
        DotStyle.FLOWER -> {
            drawCircle(color, half * 0.46f, Offset(center.x - half * 0.45f, center.y - half * 0.45f))
            drawCircle(color, half * 0.46f, Offset(center.x + half * 0.45f, center.y - half * 0.45f))
            drawCircle(color, half * 0.46f, Offset(center.x - half * 0.45f, center.y + half * 0.45f))
            drawCircle(color, half * 0.46f, Offset(center.x + half * 0.45f, center.y + half * 0.45f))
            drawCircle(color, half * 0.55f, center)
        }
        DotStyle.LEAF -> {
            val path = Path().apply {
                moveTo(left + moduleSize * 0.12f, top + moduleSize * 0.9f)
                cubicTo(left + moduleSize * 0.12f, top + moduleSize * 0.3f, center.x, top + moduleSize * 0.08f, left + moduleSize * 0.9f, top + moduleSize * 0.12f)
                cubicTo(left + moduleSize * 0.7f, top + moduleSize * 0.55f, left + moduleSize * 0.9f, top + moduleSize * 0.9f, left + moduleSize * 0.12f, top + moduleSize * 0.9f)
                close()
            }
            drawPath(path, color)
        }
        DotStyle.TRIANGLE -> {
            val path = Path().apply {
                moveTo(center.x, top)
                lineTo(left + moduleSize, top + moduleSize)
                lineTo(left, top + moduleSize)
                close()
            }
            drawPath(path, color)
        }
        DotStyle.HEXAGON -> {
            val path = polygonPreview(center, half * 0.95f, 6)
            drawPath(path, color)
        }
        DotStyle.SPARKLE -> {
            val path = Path().apply {
                moveTo(center.x, top)
                quadraticBezierTo(center.x, center.y, left + moduleSize, center.y)
                quadraticBezierTo(center.x, center.y, center.x, top + moduleSize)
                quadraticBezierTo(center.x, center.y, left, center.y)
                quadraticBezierTo(center.x, center.y, center.x, top)
                close()
            }
            drawPath(path, color)
        }
        DotStyle.MOON -> {
            drawCircle(color, half * 0.86f, center)
            drawCircle(DUAInk, half * 0.72f, Offset(center.x + half * 0.35f, center.y - half * 0.28f))
        }
        DotStyle.GHOST -> {
            drawCircle(color, half * 0.78f, Offset(center.x, center.y - half * 0.08f))
            drawRect(color, Offset(left + half * 0.22f, center.y), Size(moduleSize - half * 0.44f, half * 0.78f))
            drawCircle(DUAInk, half * 0.12f, Offset(center.x - half * 0.28f, center.y - half * 0.12f))
            drawCircle(DUAInk, half * 0.12f, Offset(center.x + half * 0.28f, center.y - half * 0.12f))
        }
    }
}

private fun DrawScope.drawStarPreview(center: Offset, radius: Float, color: Color) {
    drawPath(polygonPreview(center, radius, 10, alternateRadius = radius * 0.42f), color)
}

private fun polygonPreview(
    center: Offset,
    radius: Float,
    points: Int,
    alternateRadius: Float = radius
): Path {
    val path = Path()
    repeat(points) { index ->
        val angle = -Math.PI / 2.0 + index * (2.0 * Math.PI / points)
        val currentRadius = if (alternateRadius != radius && index % 2 == 1) alternateRadius else radius
        val point = Offset(
            center.x + cos(angle).toFloat() * currentRadius,
            center.y + sin(angle).toFloat() * currentRadius
        )
        if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    path.close()
    return path
}

@Composable
private fun ColorSwatches(selected: Color, colors: List<Color>, onSelect: (Color) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .border(if (selected == color) 3.dp else 1.dp, if (selected == color) DUABlue else DUALine, RoundedCornerShape(13.dp))
                    .background(color)
                    .clickable { onSelect(color) }
            ) {
                if (selected == color) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = "Seleccionado", tint = if (color.luminance() < 0.5f) Color.White else DUABlue, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}


private fun dotLabel(style: DotStyle): String = when (style) {
    DotStyle.SQUARE -> "Cuadrado"
    DotStyle.CIRCLE -> "Redondo"
    DotStyle.HEART -> "Corazón"
    DotStyle.STAR -> "Estrella"
    DotStyle.PAW -> "Huella"
    DotStyle.CAT -> "Gato"
    DotStyle.DIAMOND -> "Rombo"
    DotStyle.FLOWER -> "Flor"
    DotStyle.LEAF -> "Hoja"
    DotStyle.TRIANGLE -> "Triángulo"
    DotStyle.HEXAGON -> "Hexágono"
    DotStyle.SPARKLE -> "Destello"
    DotStyle.MOON -> "Luna"
    DotStyle.GHOST -> "Fantasma"
}

private fun frameLabel(frame: FrameType): String = when (frame) {
    FrameType.NONE -> "Ninguno"
    FrameType.BEAR -> "Osito"
    FrameType.CAT_FRAME -> "Gato"
    FrameType.TV -> "Tele"
    FrameType.PHONE -> "Móvil"
    FrameType.CLOUD -> "Nube"
    FrameType.HOUSE -> "Casa"
    FrameType.CROWN -> "Corona"
    FrameType.FROG -> "Rana"
}

private fun formatContrast(value: Float): String = String.format(Locale.getDefault(), "%.1f:1", value)
