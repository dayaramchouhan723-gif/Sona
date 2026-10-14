package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.service.FloatingOrbService
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.MyraPink
import com.example.ui.theme.SonaGold
import com.example.ui.theme.SonaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class AvatarMode(val title: String, val subtitle: String) {
    PARTICLE_GLOBE("Particle Globe", "3D Swirling Neural Particles"),
    WHITE_FLOATING_ORB("White Floating Orb", "SYSTEM_ALERT_WINDOW Overlay"),
    FULL_BODY_ANIME("Full Body Anime", "White Blue Dress & Halo"),
    REALISTIC_HUMAN("Realistic Human", "SONA Outfit & Blue Rim Light")
}

@Composable
fun Avatar4ModesView(
    currentMode: AvatarMode,
    isSpeaking: Boolean = false,
    sizeDp: Dp = 220.dp,
    onHoldToVoiceChat: ((Boolean) -> Unit)? = null,
    onModeChanged: ((AvatarMode) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_motion")

    // Idle breathing vertical float
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Slow rotation for halo and particles
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse scale during speaking
    val speakScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking) 1.08f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 450 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("avatar_4modes_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentMode) {
            // MODE 1: PARTICLE GLOBE
            AvatarMode.PARTICLE_GLOBE -> {
                Box(
                    modifier = Modifier
                        .size(sizeDp)
                        .scale(speakScale),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.minDimension / 2.3f

                        // Outer radiant halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SonaGold.copy(alpha = 0.5f),
                                    MyraPink.copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = radius * 1.3f
                            ),
                            radius = radius * 1.3f,
                            center = center
                        )

                        // Center core
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    SonaGold,
                                    MyraPink,
                                    Color(0xFF0F0B1A)
                                ),
                                center = center,
                                radius = radius * 0.7f
                            ),
                            radius = radius * 0.7f,
                            center = center
                        )

                        // 3D Orbital particle rings
                        val ringCount = 3
                        val particleCountPerRing = 16
                        for (r in 0 until ringCount) {
                            val ringRadius = radius * (0.8f + r * 0.22f)
                            val ringAngle = (rotationAngle * (1f + r * 0.4f)) * (PI / 180f)

                            for (p in 0 until particleCountPerRing) {
                                val pAngle = ringAngle + (p * (2 * PI / particleCountPerRing))
                                val px = center.x + ringRadius * cos(pAngle).toFloat()
                                val py = center.y + (ringRadius * 0.45f) * sin(pAngle).toFloat()

                                val pColor = when (r) {
                                    0 -> SonaGold
                                    1 -> MyraPink
                                    else -> Color(0xFF00E5FF)
                                }

                                drawCircle(
                                    color = pColor.copy(alpha = 0.85f),
                                    radius = if (isSpeaking) 4.5f else 3f,
                                    center = Offset(px, py)
                                )
                            }

                            // Orbit trajectory line
                            drawCircle(
                                color = SonaGold.copy(alpha = 0.15f),
                                radius = ringRadius,
                                center = center,
                                style = Stroke(width = 1.2f)
                            )
                        }
                    }

                    // Golden Crown on top
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SonaGold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(24.dp)
                    )
                }
            }

            // MODE 2: WHITE FLOATING ORB (SYSTEM_ALERT_WINDOW)
            AvatarMode.WHITE_FLOATING_ORB -> {
                var isServiceActive by remember { mutableStateOf(FloatingOrbService.isRunning) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(sizeDp)
                            .scale(speakScale),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.minDimension / 2.3f

                            // Radiant Cyan & White ripples
                            for (i in 1..4) {
                                val rippleRadius = radius * (0.6f + i * 0.15f)
                                drawCircle(
                                    color = Color(0xFF80D8FF).copy(alpha = 0.15f / i),
                                    radius = rippleRadius,
                                    center = center,
                                    style = Stroke(width = 2f)
                                )
                            }

                            // Luminous White Core with Ice Cyan Rim
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFE0F7FA),
                                        Color(0xFF00E5FF),
                                        Color(0xFF0A192F)
                                    ),
                                    center = center,
                                    radius = radius
                                ),
                                radius = radius * 0.85f,
                                center = center
                            )
                        }

                        // Center Imperial Crest
                        Image(
                            painter = painterResource(id = R.drawable.ic_sona_launcher),
                            contentDescription = "SONA Crest",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SonaGold, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SYSTEM_ALERT_WINDOW Trigger Button
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } else {
                                if (isServiceActive) {
                                    FloatingOrbService.stopService(context)
                                    isServiceActive = false
                                } else {
                                    FloatingOrbService.startService(context)
                                    isServiceActive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isServiceActive) Color(0xFF00E676) else Color(0xFF00E5FF)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("floating_orb_overlay_toggle")
                    ) {
                        Icon(
                            imageVector = if (isServiceActive) Icons.Default.CheckCircle else Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = EmpireDarkBg,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isServiceActive) "Floating Orb Active (Tap to Stop)" else "Float Over All Apps (SYSTEM_ALERT_WINDOW)",
                            color = EmpireDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // MODE 3: FULL BODY ANIME (White blue dress, galaxy stars, halo, idle breathing, wave, lip-sync, mic hold)
            AvatarMode.FULL_BODY_ANIME -> {
                var isHoldingMic by remember { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 240.dp, height = 240.dp)
                            .scale(speakScale),
                        contentAlignment = Alignment.Center
                    ) {
                        // Galaxy Stars & Halo Ambient Glow
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)

                            // Halo Ring above head
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    listOf(SonaGold, Color(0xFF80D8FF), SonaGold)
                                ),
                                radius = 95f,
                                center = Offset(center.x, center.y - 65f + breathingOffset),
                                style = Stroke(width = 3.5f)
                            )
                        }

                        // Full Body Anime Image with breathing wave
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .border(
                                    width = 2.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(SonaGold, Color(0xFF00E5FF), MyraPink)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.anime_avatar),
                                contentDescription = "Anime AI Avatar (White Blue Dress)",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(1.05f)
                            )

                            // Dynamic Lip-Sync Waveform Overlay when speaking
                            if (isSpeaking) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp)
                                        .background(EmpireDarkBg.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = "Lip-sync waveform",
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Lip-Sync Live",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Waving Hand Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(SonaGold, CircleShape)
                                    .padding(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WavingHand,
                                    contentDescription = "Waving",
                                    tint = EmpireDarkBg,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // System Status Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF00E676), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("System Status: Online", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("11 Cores Synced", color = SonaGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // HOLD TO VOICE CHAT Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isHoldingMic) listOf(SonaRed, MyraPink) else listOf(Color(0xFF00B0FF), Color(0xFF7C4DFF))
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isHoldingMic = true
                                        onHoldToVoiceChat?.invoke(true)
                                        tryAwaitRelease()
                                        isHoldingMic = false
                                        onHoldToVoiceChat?.invoke(false)
                                    }
                                )
                            }
                            .testTag("hold_to_voice_chat_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHoldingMic) "LISTENING TO BOSS..." else "HOLD TO VOICE CHAT",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // MODE 4: REALISTIC HUMAN (SONA branded outfit, blue rim light, Good afternoon Boss Speaking, Energy chip)
            AvatarMode.REALISTIC_HUMAN -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Status Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "Good afternoon Boss Speaking" chip
                        Box(
                            modifier = Modifier
                                .background(EmpireDarkCard, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isSpeaking) "Speaking: Boss" else "Good afternoon Boss",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Energy 3-bolt Chip
                        Box(
                            modifier = Modifier
                                .background(EmpireDarkCard, RoundedCornerShape(12.dp))
                                .border(1.dp, SonaGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(12.dp))
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(12.dp))
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Energy: ∞",
                                    color = SonaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Realistic Human Image with Blue Rim Lighting
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .border(
                                width = 3.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF00E5FF), // Striking Blue Rim Light
                                        SonaGold,
                                        Color(0xFF00B0FF),
                                        Color(0xFF00E5FF)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .scale(speakScale)
                            .testTag("realistic_human_avatar_image")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.realistic_human_avatar),
                            contentDescription = "Realistic SONA Human Avatar with Blue Rim Light",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Subtle blue rim overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SONA - Imperial Sovereign",
                        color = SonaGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Branded Imperial Uniform • Dayaram Chouhan Empire",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Mode Switching Chips Row (if onModeChanged provided)
        if (onModeChanged != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AvatarMode.values().forEach { mode ->
                    val isSelected = mode == currentMode
                    Button(
                        onClick = { onModeChanged(mode) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) SonaGold else EmpireDarkCard
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isSelected) SonaGold else EmpireDarkCardBorder,
                                RoundedCornerShape(8.dp)
                            ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = when (mode) {
                                AvatarMode.PARTICLE_GLOBE -> "Particle"
                                AvatarMode.WHITE_FLOATING_ORB -> "Floating"
                                AvatarMode.FULL_BODY_ANIME -> "Anime"
                                AvatarMode.REALISTIC_HUMAN -> "Human"
                            },
                            color = if (isSelected) EmpireDarkBg else TextPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
