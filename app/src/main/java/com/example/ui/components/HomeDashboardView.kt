package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.util.Calendar

enum class BackgroundThemeMode(val label: String) {
    GALAXY("Galaxy"),
    WARM_CANDLE("Warm Candle"),
    DARK("Dark")
}

@Composable
fun HomeDashboardView(
    isSpeaking: Boolean,
    currentThemeMode: BackgroundThemeMode,
    onThemeModeChanged: (BackgroundThemeMode) -> Unit,
    onMusicClicked: () -> Unit,
    onStudyClicked: () -> Unit,
    onJournalClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = remember(currentHour) {
        when {
            currentHour in 5..11 -> "Good morning Boss"
            currentHour in 12..16 -> "Good afternoon Boss"
            currentHour in 17..21 -> "Good evening Boss"
            else -> "Good night Boss"
        }
    }

    val cardBorderColor by animateColorAsState(
        targetValue = when (currentThemeMode) {
            BackgroundThemeMode.GALAXY -> Color(0xFF00E5FF).copy(alpha = 0.5f)
            BackgroundThemeMode.WARM_CANDLE -> SonaGold.copy(alpha = 0.6f)
            BackgroundThemeMode.DARK -> EmpireDarkCardBorder
        },
        label = "borderColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = EmpireDarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    when (currentThemeMode) {
                        BackgroundThemeMode.GALAXY -> listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF), MyraPink)
                        BackgroundThemeMode.WARM_CANDLE -> listOf(SonaGold, Color(0xFFFF9100), SonaGold)
                        BackgroundThemeMode.DARK -> listOf(SonaGold, EmpireDarkCardBorder, SonaGold)
                    }
                ),
                RoundedCornerShape(20.dp)
            )
            .testTag("home_dashboard_view")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP GREETING + SPEAKING STATUS + ENERGY 3 BOLT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting & Speaking Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (isSpeaking) Color(0xFF00E676) else SonaGold, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = greeting,
                            color = SonaGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = if (isSpeaking) Color(0xFF00E676) else TextMuted,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isSpeaking) "Speaking live..." else "Standby Ready",
                                color = if (isSpeaking) Color(0xFF00E676) else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 3 Energy Bolts Badge
                Box(
                    modifier = Modifier
                        .background(EmpireDarkBg, RoundedCornerShape(12.dp))
                        .border(1.dp, SonaGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("energy_3bolt_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(13.dp))
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(13.dp))
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = SonaGold, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "3-Energy ∞",
                            color = SonaGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 3 CHIPS: Music, Study, Journal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Music Chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmpireDarkCard)
                        .border(1.dp, MyraPink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onMusicClicked() }
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .testTag("chip_music"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = MyraPink, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Music", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Study Chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmpireDarkCard)
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onStudyClicked() }
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .testTag("chip_study"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Study", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Journal Chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmpireDarkCard)
                        .border(1.dp, SonaGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onJournalClicked() }
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .testTag("chip_journal"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = SonaGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Journal", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 3 CARDS: Weather 29° 70% live + Today 5 Mon Oct + Mood Happy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weather Card (29° 70% live)
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                        .testTag("card_weather")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = SonaGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("29°C", color = SonaGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("70% live", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Partly Cloudy", color = TextMuted, fontSize = 8.sp, maxLines = 1)
                    }
                }

                // Today Card (5 Mon Oct)
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                        .testTag("card_today")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MyraPink, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mon 5 Oct", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Day 278", color = MyraPink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("Empire Calibrated", color = TextMuted, fontSize = 8.sp, maxLines = 1)
                    }
                }

                // Mood Card (Happy)
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                        .testTag("card_mood")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Happy", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Upbeat", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        Text("Resonance 98%", color = TextMuted, fontSize = 8.sp, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. BACKGROUND THEME TOGGLE: Galaxy / Warm Candle / Dark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmpireDarkBg, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BackgroundThemeMode.values().forEach { mode ->
                    val isSelected = currentThemeMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) {
                                    when (mode) {
                                        BackgroundThemeMode.GALAXY -> Brush.horizontalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF)))
                                        BackgroundThemeMode.WARM_CANDLE -> Brush.horizontalGradient(listOf(SonaGold, Color(0xFFFF9100)))
                                        BackgroundThemeMode.DARK -> Brush.horizontalGradient(listOf(Color(0xFF2A2338), Color(0xFF1E1929)))
                                    }
                                } else {
                                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                            .clickable { onThemeModeChanged(mode) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (isSelected) {
                                if (mode == BackgroundThemeMode.DARK) SonaGold else EmpireDarkBg
                            } else {
                                TextMuted
                            },
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
