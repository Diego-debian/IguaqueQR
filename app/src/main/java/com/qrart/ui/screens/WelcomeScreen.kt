package com.qrart.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrart.ui.theme.DUAAccent
import com.qrart.ui.theme.DUABlue
import com.qrart.ui.theme.DUACanvas
import com.qrart.ui.theme.DUAInk
import com.qrart.ui.theme.DUASurface
import com.qrart.ui.theme.DUATeal
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val pulse by rememberInfiniteTransition(label = "welcome-pulse").animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "welcome-pulse-scale"
    )

    LaunchedEffect(Unit) {
        delay(120)
        visible = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DUACanvas
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEAF0FF),
                            DUACanvas,
                            Color(0xFFF2EEFF)
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(650)) + slideInVertically(
                    animationSpec = tween(650),
                    initialOffsetY = { it / 5 }
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .scale(pulse)
                            .size(104.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.linearGradient(listOf(DUABlue, DUAAccent))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = "Iguaque QR",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = DUAInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Una idea. Un código. Una conexión.",
                        style = MaterialTheme.typography.titleMedium,
                        color = DUABlue,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Iguaque QR convierte tus enlaces e ideas en códigos visuales, claros y con identidad.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DUAInk.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))
                    WelcomeInfoCard()
                    Spacer(modifier = Modifier.height(14.dp))
                    ContactCard()
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Creado por diegodebian",
                        color = DUAAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DUABlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Entrar a la app", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Outlined.Link, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeInfoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DUASurface.copy(alpha = 0.94f))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Water, contentDescription = null, tint = DUATeal)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "¿Por qué Iguaque?",
                fontWeight = FontWeight.Bold,
                color = DUAInk,
                fontSize = 16.sp
            )
        }
        Text(
            text = "Iguaque representa el origen y la conexión. En la tradición muisca, la laguna de Iguaque es un lugar de nacimiento y memoria. Tomamos ese símbolo para crear una herramienta que convierte tus ideas en un punto de encuentro: un QR que lleva a tu mundo, tu proyecto o tu historia.",
            color = DUAInk.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 21.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureItem(Icons.Outlined.QrCode, "Genera")
            FeatureItem(Icons.Outlined.Palette, "Personaliza")
            FeatureItem(Icons.Outlined.Link, "Conecta")
        }
    }
}

@Composable
private fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = DUAAccent, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = DUAInk.copy(alpha = 0.65f), fontSize = 12.sp)
    }
}

@Composable
private fun ContactCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Email, contentDescription = null, tint = DUABlue)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("Contacto", color = DUAInk.copy(alpha = 0.58f), fontSize = 12.sp)
            Text(
                "profediegoparra01@gmail.com",
                color = DUAInk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
