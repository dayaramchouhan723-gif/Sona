package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.SonaGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrbView(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    primaryColor: Color = Color(0xFFFF2D75),
    secondaryColor: Color = Color(0xFFFF1744),
    orbType: String = "Neon", // Classic, Energy, Neon
    isMaster: Boolean = false,
    hasRealAvatar: Boolean = false,
    isSpeaking: Boolean = false,
    showSpectrum: Boolean = true,
    showAuraBorder: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = if (isSpeaking) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    Box(
        modifier = modifier
            .size(sizeDp)
            .testTag("orb_view_container"),
        contentAlignment = Alignment.Center
    ) {
        if (hasRealAvatar) {
            // MAYA Real Girl Avatar with royal animated aura
            Box(
                modifier = Modifier
                    .size(sizeDp * pulseScale)
                    .clip(CircleShape)
                    .border(
                        width = if (showAuraBorder) 3.dp else 1.dp,
                        brush = Brush.sweepGradient(
                            listOf(SonaGold, Color(0xFFFF9100), primaryColor, SonaGold)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.maya_avatar),
                    contentDescription = "MAYA Real Girl AI Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Neural Breathing Orb
            Canvas(
                modifier = Modifier
                    .size(sizeDp * pulseScale)
                    .testTag("orb_canvas")
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.minDimension / 2.3f

                // Outer Aura Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = auraAlpha * 0.7f),
                            secondaryColor.copy(alpha = auraAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * 1.35f
                    ),
                    radius = baseRadius * 1.35f,
                    center = center
                )

                // Neon Energy Rings (if Energy or Neon type)
                if (orbType != "Classic") {
                    val ringCount = if (orbType == "Neon") 3 else 2
                    for (i in 0 until ringCount) {
                        val angleOffset = (rotationAngle + i * (360f / ringCount)) * (PI / 180f)
                        val rx = baseRadius * (0.95f + i * 0.12f)
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(primaryColor, secondaryColor, SonaGold),
                                start = Offset(
                                    center.x + rx * cos(angleOffset).toFloat(),
                                    center.y + rx * sin(angleOffset).toFloat()
                                ),
                                end = Offset(
                                    center.x - rx * cos(angleOffset).toFloat(),
                                    center.y - rx * sin(angleOffset).toFloat()
                                )
                            ),
                            radius = rx,
                            center = center,
                            style = Stroke(width = if (showAuraBorder) 2.5f else 1f)
                        )
                    }
                }

                // Inner Luminous Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            primaryColor,
                            secondaryColor,
                            Color(0xFF090712).copy(alpha = 0.9f)
                        ),
                        center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
                        radius = baseRadius
                    ),
                    radius = baseRadius * 0.85f,
                    center = center
                )

                // Spectrum Visualizer Bars around the orb
                if (showSpectrum) {
                    val barCount = 24
                    for (b in 0 until barCount) {
                        val barAngle = (b * (360f / barCount)) * (PI / 180f)
                        val barHeight = if (isSpeaking) {
                            (8f + 14f * sin((rotationAngle + b * 15f) * (PI / 180f)).toFloat().coerceAtLeast(0f))
                        } else {
                            (4f + 5f * sin((rotationAngle + b * 20f) * (PI / 180f)).toFloat().coerceAtLeast(0f))
                        }
                        val startDist = baseRadius * 1.05f
                        val endDist = startDist + barHeight
                        val startX = center.x + startDist * cos(barAngle).toFloat()
                        val startY = center.y + startDist * sin(barAngle).toFloat()
                        val endX = center.x + endDist * cos(barAngle).toFloat()
                        val endY = center.y + endDist * sin(barAngle).toFloat()

                        drawLine(
                            color = if (b % 2 == 0) primaryColor else SonaGold,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 2.5f
                        )
                    }
                }
            }
        }

        // Master Golden Crown Icon for Queen SONA
        if (isMaster) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(sizeDp * 0.32f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Golden Crown Master",
                    tint = SonaGold,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
