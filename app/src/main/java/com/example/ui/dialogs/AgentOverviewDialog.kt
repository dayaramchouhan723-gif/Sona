package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AgentProfile
import com.example.ui.components.OrbView
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.SonaGold
import com.example.ui.theme.SonaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentOverviewDialog(
    agent: AgentProfile,
    isSpeaking: Boolean,
    onDismiss: () -> Unit,
    onPlayVoiceSample: () -> Unit,
    onStopVoiceSample: () -> Unit,
    onTalkNow: () -> Unit,
    onOpenVoiceClone: () -> Unit,
    onInspectMemory: () -> Unit
) {
    val primaryColor = remember(agent.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(agent.colorHex))
        } catch (e: Exception) {
            SonaGold
        }
    }

    val secondaryColor = remember(agent.secondaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(agent.secondaryColorHex))
        } catch (e: Exception) {
            SonaRed
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(primaryColor, EmpireDarkCardBorder, SonaGold)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("agent_overview_dialog"),
            color = EmpireDarkSurface
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google Connected Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(EmpireDarkCard, RoundedCornerShape(12.dp))
                            .border(1.dp, SonaGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google Connected ✅ - Free Lifetime ∞",
                            color = SonaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TOP: Avatar / Real Girl Face / Orb breathing animation
                OrbView(
                    sizeDp = 130.dp,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    hasRealAvatar = agent.hasRealAvatar,
                    isMaster = agent.isMaster,
                    isSpeaking = isSpeaking,
                    orbType = "Neon"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Name & Tagline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = agent.name,
                        color = primaryColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (agent.badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SonaGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = agent.badge,
                                color = EmpireDarkBg,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = agent.tagline,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // VOICE SAMPLE PLAY / STOP BUTTON
                Button(
                    onClick = {
                        if (isSpeaking) onStopVoiceSample() else onPlayVoiceSample()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) SonaRed else EmpireDarkCard
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .border(1.dp, primaryColor, RoundedCornerShape(20.dp))
                        .testTag("play_voice_sample_button")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Voice Sample",
                        tint = if (isSpeaking) TextPrimary else primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpeaking) "Speaking... [Stop]" else "Play Voice Sample (${agent.voiceName})",
                        color = if (isSpeaking) TextPrimary else primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // GF DRAMA QUOTE (for Maya or special personality)
                if (agent.emotionQuote != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1530)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFFF4081).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "GF Drama",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "\"${agent.emotionQuote}\"",
                                color = Color(0xFFFF80AB),
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // LIVE WIDGETS ROW: Weather, Today, Mood
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Weather
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Weather",
                                tint = Color(0xFF80D8FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = agent.weatherTemp,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = agent.weatherCondition,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Today Date
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Today",
                                tint = SonaGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = agent.todayDate,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Today",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Mood
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = "Mood",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = agent.todayMood,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mood",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // CHIPS ROW: Music, Study, Journal
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    agent.chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .background(EmpireDarkCard, RoundedCornerShape(16.dp))
                                .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "# $chip",
                                color = primaryColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FRONTEND UI & BACKEND LOGIC SEPARATION (Requested explicitly by user)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "⚡ FRONTEND UI SPECIFICATION",
                            color = SonaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = agent.frontendUiTheme,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = EmpireDarkCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "⚙️ BACKEND ARCHITECTURE & LOGIC",
                            color = Color(0xFF00E5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = agent.backendLogic,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FULL FEATURES LIST
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🛠️ FULL CAPABILITIES & TOOLS (${agent.toolsCount} TOOLS)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        agent.features.forEach { feat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "•", color = primaryColor, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feat,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ACTION BUTTONS: [Talk Now] [Clone Voice] [Inspect Memory JSON]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onTalkNow,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("overview_talk_now_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Talk",
                            tint = EmpireDarkBg,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Talk Now",
                            color = EmpireDarkBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenVoiceClone,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, SonaGold, RoundedCornerShape(12.dp))
                            .testTag("overview_clone_voice_button")
                    ) {
                        Text(
                            text = "Clone Voice",
                            color = SonaGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onInspectMemory,
                    colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .testTag("overview_inspect_memory_button")
                ) {
                    Text(
                        text = "Inspect ${agent.memoryFile}",
                        color = Color(0xFF00E5FF),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Signature
                Text(
                    text = "Design by: Dayaram Chouhan | Develop by: Dayaram Chouhan",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
