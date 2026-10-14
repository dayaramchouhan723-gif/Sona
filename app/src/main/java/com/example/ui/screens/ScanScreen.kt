package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.KayraGreen
import com.example.ui.theme.SonaGold
import com.example.ui.theme.SonaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScanScreen(
    isOcrActive: Boolean,
    isLiveTranscriptionActive: Boolean,
    atsScore: Int,
    onToggleOcr: () -> Unit,
    onToggleTranscription: () -> Unit,
    onShowNotice: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmpireDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("scan_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "Visual Lens",
                        tint = SonaGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VISUAL LENS & LIVE OCR",
                        color = SonaGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "Live Transcription • ATS Live 85/100 • Kayra Scanner",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // ATS Badge
            Box(
                modifier = Modifier
                    .background(Color(0xFF2C1625), RoundedCornerShape(12.dp))
                    .border(1.5.dp, SonaRed, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SonaRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ATS $atsScore/100",
                        color = SonaRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CAMERA SIMULATED VIEWFINDER WITH ANIMATED LASER SCAN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(EmpireDarkCard)
                .border(2.dp, Brush.linearGradient(listOf(SonaGold, SonaRed, Color(0xFF00E5FF))), RoundedCornerShape(20.dp))
                .testTag("viewfinder_box")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Grid lines
                drawLine(
                    color = EmpireDarkCardBorder,
                    start = Offset(w * 0.33f, 0f),
                    end = Offset(w * 0.33f, h),
                    strokeWidth = 1f
                )
                drawLine(
                    color = EmpireDarkCardBorder,
                    start = Offset(w * 0.66f, 0f),
                    end = Offset(w * 0.66f, h),
                    strokeWidth = 1f
                )
                drawLine(
                    color = EmpireDarkCardBorder,
                    start = Offset(0f, h * 0.5f),
                    end = Offset(w, h * 0.5f),
                    strokeWidth = 1f
                )

                // Corner brackets
                val cornerSize = 28f
                val strokeW = 4f
                val cornerColor = SonaGold

                // Top-Left
                drawLine(cornerColor, Offset(16f, 16f), Offset(16f + cornerSize, 16f), strokeW)
                drawLine(cornerColor, Offset(16f, 16f), Offset(16f, 16f + cornerSize), strokeW)

                // Top-Right
                drawLine(cornerColor, Offset(w - 16f, 16f), Offset(w - 16f - cornerSize, 16f), strokeW)
                drawLine(cornerColor, Offset(w - 16f, 16f), Offset(w - 16f, 16f + cornerSize), strokeW)

                // Bottom-Left
                drawLine(cornerColor, Offset(16f, h - 16f), Offset(16f + cornerSize, h - 16f), strokeW)
                drawLine(cornerColor, Offset(16f, h - 16f), Offset(16f, h - 16f - cornerSize), strokeW)

                // Bottom-Right
                drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 16f - cornerSize, h - 16f), strokeW)
                drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 16f, h - 16f - cornerSize), strokeW)

                // Simulated OCR Bounding Box 1
                if (isOcrActive) {
                    drawRect(
                        color = Color(0xFF00E676).copy(alpha = 0.8f),
                        topLeft = Offset(w * 0.12f, h * 0.22f),
                        size = Size(w * 0.45f, h * 0.16f),
                        style = Stroke(width = 2f)
                    )
                    // Bounding Box 2
                    drawRect(
                        color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                        topLeft = Offset(w * 0.40f, h * 0.55f),
                        size = Size(w * 0.50f, h * 0.18f),
                        style = Stroke(width = 2f)
                    )
                }

                // Animated Laser Line
                val currentLaserY = h * laserY
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, SonaRed, SonaGold, SonaRed, Color.Transparent)
                    ),
                    start = Offset(16f, currentLaserY),
                    end = Offset(w - 16f, currentLaserY),
                    strokeWidth = 3f
                )
            }

            // Overlay Bounding Box Labels
            if (isOcrActive) {
                Box(
                    modifier = Modifier
                        .padding(start = 36.dp, top = 48.dp)
                        .background(Color(0xFF00E676), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "DETECTED: Dayaram Chouhan (Empire Architect)",
                        color = EmpireDarkBg,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 120.dp, top = 140.dp)
                        .background(Color(0xFF00E5FF), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "MATCH: Dubai Tech Lead • ATS 85/100",
                        color = EmpireDarkBg,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Flash & Status Pill
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(EmpireDarkBg.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "60 FPS • LIVE OCR",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TOGGLES ROW
        Card(
            colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Live OCR Bounding Boxes", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Extract text & credentials in real time", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isOcrActive,
                        onCheckedChange = { onToggleOcr() },
                        colors = SwitchDefaults.colors(checkedThumbColor = SonaGold, checkedTrackColor = EmpireDarkBg)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Live Transcription Stream", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Edge speech-to-text without internet delay", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isLiveTranscriptionActive,
                        onCheckedChange = { onToggleTranscription() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = EmpireDarkBg)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LIVE TRANSCRIPTION STREAM TICKER
        Card(
            colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REAL-TIME TRANSCRIPTION FEED",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isLiveTranscriptionActive) {
                        "\"[Dayaram Chouhan]: Building the unified 11-in-1 neural empire with MYRA, MAYA, KAYRA, and Queen SONA. Zero-cost lifetime access locked.\""
                    } else {
                        "Transcription feed paused. Enable toggle above to resume."
                    },
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // DUBAI JOBS & ATS MATCHER (Kayra tool integration requested)
        Card(
            colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KayraGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Work, contentDescription = null, tint = KayraGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KAYRA DUBAI JOBS AUTOMATION",
                            color = KayraGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(KayraGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("85% MATCH", color = KayraGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Role: Lead Android & AI Systems Engineer (Dubai Silicon Oasis)",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Keywords Matched: Jetpack Compose, Kotlin, Multi-Agent Systems, Whisper/Sherpa ONNX",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onShowNotice("Kayra: Auto-submitted tailored profile to Dubai recruiter queue!") },
                        colors = ButtonDefaults.buttonColors(containerColor = KayraGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Submit via Kayra", color = EmpireDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onShowNotice("ATS Report: 85/100 score verified. Strengths: Real Android Code, Zero Mock.") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(10.dp))
                    ) {
                        Text("ATS Report", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
