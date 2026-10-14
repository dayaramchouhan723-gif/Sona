package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun Jarvis4CoreView(
    onNodeClicked: (nodeName: String, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jarvis_network")

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    val coreScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = EmpireDarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.linearGradient(listOf(SonaGold, MyraPink, SonaGold)),
                RoundedCornerShape(24.dp)
            )
            .testTag("jarvis_4core_container")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = SonaGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "JARVIS 4-CORE NEURAL MATRIX",
                        color = SonaGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .background(EmpireDarkBg, RoundedCornerShape(8.dp))
                        .border(1.dp, MyraPink.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "SONA GOLD-PINK CORE",
                        color = MyraPink,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // The 4-Core Layout: TOP, CENTER (with LEFT & RIGHT), BOTTOM
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Circuitry Network Canvas connecting Core to 4 sides
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    val topPt = Offset(cx, 38.dp.toPx())
                    val bottomPt = Offset(cx, size.height - 38.dp.toPx())
                    val leftPt = Offset(50.dp.toPx(), cy)
                    val rightPt = Offset(size.width - 50.dp.toPx(), cy)

                    val pts = listOf(topPt, bottomPt, leftPt, rightPt)

                    // Draw connecting lines with gold-pink gradient
                    pts.forEach { pt ->
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(SonaGold, MyraPink)
                            ),
                            start = Offset(cx, cy),
                            end = pt,
                            strokeWidth = 2.5f
                        )

                        // Traveling data pulse dot
                        val packetX = cx + (pt.x - cx) * pulsePhase
                        val packetY = cy + (pt.y - cy) * pulsePhase
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = Offset(packetX, packetY)
                        )
                    }

                    // Circular radar grid rings
                    for (r in 1..3) {
                        drawCircle(
                            color = SonaGold.copy(alpha = 0.12f),
                            radius = 35.dp.toPx() * r,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.2f)
                        )
                    }
                }

                // TOP NODE: JARVIS (Tasks + Calendar)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .clickable {
                            onNodeClicked("JARVIS", "Tasks + Calendar Sync • 12 Active Missions • Daily Schedule")
                        }
                        .testTag("node_top_jarvis")
                ) {
                    JarvisNodeCard(
                        title = "JARVIS",
                        subtitle = "Tasks + Calendar",
                        icon = Icons.Default.Task,
                        accentColor = Color(0xFF00E5FF)
                    )
                }

                // BOTTOM NODE: FRIDAY (Communication WhatsApp Call Email)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clickable {
                            onNodeClicked("FRIDAY", "Communication Hub • WhatsApp Auto-Send • Dial Call • SMS • Gmail")
                        }
                        .testTag("node_bottom_friday")
                ) {
                    JarvisNodeCard(
                        title = "FRIDAY",
                        subtitle = "WhatsApp • Call • Email",
                        icon = Icons.Default.Message,
                        accentColor = Color(0xFF00E676)
                    )
                }

                // LEFT NODE: EDITH (Files Media Search Browser)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            onNodeClicked("EDITH", "Files • Media • Search • Web Browser • Visual Lens OCR")
                        }
                        .testTag("node_left_edith")
                ) {
                    JarvisNodeCard(
                        title = "EDITH",
                        subtitle = "Files • Media • Search",
                        icon = Icons.Default.Search,
                        accentColor = Color(0xFFFF9100)
                    )
                }

                // RIGHT NODE: ULTRON (Device Control + PC Connect)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            onNodeClicked("ULTRON", "Device Control • PC Connect (81MB Bridge) • WiFi Beta ON • Screen Auto-Control")
                        }
                        .testTag("node_right_ultron")
                ) {
                    JarvisNodeCard(
                        title = "ULTRON",
                        subtitle = "Device Control • PC Connect",
                        icon = Icons.Default.Devices,
                        accentColor = SonaRed
                    )
                }

                // CENTER NODE: GLOWING SONA CORE
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color.White,
                                    SonaGold,
                                    MyraPink,
                                    EmpireDarkBg
                                )
                            )
                        )
                        .border(
                            2.5.dp,
                            Brush.sweepGradient(listOf(SonaGold, MyraPink, SonaGold)),
                            CircleShape
                        )
                        .clickable {
                            onNodeClicked("SONA CORE", "Supreme Sovereign Master Core • Unites all 4 Sub-Nodes & 11 Agents")
                        }
                        .testTag("node_center_sona_core"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Core Crown",
                            tint = EmpireDarkBg,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "SONA",
                            color = EmpireDarkBg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "CORE",
                            color = EmpireDarkBg,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Telemetry Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmpireDarkBg, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quantum Bridge: 81MB PC Linked", color = TextSecondary, fontSize = 10.sp)
                Text("WiFi Beta: ON (v1.0.66)", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun JarvisNodeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .border(1.5.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = subtitle,
                    color = TextPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
