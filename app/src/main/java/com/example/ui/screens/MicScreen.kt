package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoiceHistoryEntry
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
import com.example.viewmodel.VoiceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicScreen(
    activeAgent: AgentProfile,
    allAgents: List<AgentProfile>,
    voiceStatus: VoiceStatus,
    transcript: String,
    agentSpeakingText: String,
    isSpeakerMuted: Boolean,
    transcriptHistory: List<VoiceHistoryEntry>,
    orbSize: Float,
    auraBorderMode: Boolean,
    visualizerSpectrum: Boolean,
    onSelectAgent: (AgentProfile) -> Unit,
    onToggleVoiceListening: () -> Unit,
    onProcessVoiceCommand: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onToggleSpeakerMute: () -> Unit,
    onTestVoiceClone: () -> Unit,
    onClearHistory: () -> Unit,
    onCopyLastTranscript: (Context) -> Unit,
    onSaveTranscriptToMemory: () -> Unit,
    onNavigateHome: () -> Unit,
    onSwitchToChat: () -> Unit,
    onSetOrbSize: (Float) -> Unit,
    onToggleAuraBorder: (Boolean) -> Unit,
    onToggleSpectrum: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showHistorySection by remember { mutableStateOf(false) }
    var showMiniCustomizer by remember { mutableStateOf(false) }
    var customQueryInput by remember { mutableStateOf("") }

    val primaryColor = remember(activeAgent.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(activeAgent.colorHex))
        } catch (e: Exception) {
            SonaGold
        }
    }
    val secColor = remember(activeAgent.secondaryColorHex) {
        try {
            Color(android.graphics.Color.parseColor(activeAgent.secondaryColorHex))
        } catch (e: Exception) {
            SonaRed
        }
    }

    val isSpeaking = voiceStatus == VoiceStatus.SPEAKING
    val isListening = voiceStatus == VoiceStatus.LISTENING
    val isThinking = voiceStatus == VoiceStatus.THINKING

    // Pulsing animation for listening/speaking
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening || isSpeaking) 1.15f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmpireDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("mic_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. TOP HEADER WITH FUNCTIONAL RIGHT-SIDE MYRA-LIKE CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button & Home shortcut
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateHome,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmpireDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SonaGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onNavigateHome,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmpireDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Center Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NEURAL VOICE MODE",
                    color = SonaGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Voice: ${activeAgent.voiceName} • ${activeAgent.id.uppercase()}",
                    color = primaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Right-side functional Myra action controls: History, Speaker Mute, Volume/Clone, Tune
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showHistorySection = !showHistorySection },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (showHistorySection) primaryColor.copy(alpha = 0.2f) else EmpireDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = if (showHistorySection) primaryColor else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onToggleSpeakerMute,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSpeakerMuted) SonaRed.copy(alpha = 0.2f) else EmpireDarkCard)
                ) {
                    Icon(
                        imageVector = if (isSpeakerMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Speaker Mute",
                        tint = if (isSpeakerMuted) SonaRed else primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { showMiniCustomizer = !showMiniCustomizer },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (showMiniCustomizer) SonaGold.copy(alpha = 0.2f) else EmpireDarkCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Orb Tune",
                        tint = if (showMiniCustomizer) SonaGold else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. LIVE STATUS BADGE & WAKE WORD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wake Word Chip
            Box(
                modifier = Modifier
                    .background(EmpireDarkCard, RoundedCornerShape(12.dp))
                    .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Wake: ${activeAgent.wakeWord.take(24)}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status Pill (Listening / Thinking / Speaking / Idle)
            val statusColor = when (voiceStatus) {
                VoiceStatus.LISTENING -> Color(0xFF00E5FF)
                VoiceStatus.THINKING -> SonaGold
                VoiceStatus.SPEAKING -> Color(0xFF00E676)
                VoiceStatus.IDLE -> TextMuted
            }
            val statusText = when (voiceStatus) {
                VoiceStatus.LISTENING -> "● LISTENING..."
                VoiceStatus.THINKING -> "● THINKING (Groq 70B)..."
                VoiceStatus.SPEAKING -> "▲ AGENT SPEAKING"
                VoiceStatus.IDLE -> "IDLE (Ready)"
            }

            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(1.dp, statusColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // MINI ORB CUSTOMIZER ACCORDION (If toggled)
        AnimatedVisibility(visible = showMiniCustomizer) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(1.dp, SonaGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Mini Orb & Audio Customizer",
                        color = SonaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Orb Scale: ${(orbSize * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp)
                        Slider(
                            value = orbSize,
                            onValueChange = onSetOrbSize,
                            valueRange = 0.7f..1.4f,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                            colors = SliderDefaults.colors(thumbColor = primaryColor, activeTrackColor = primaryColor)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Aura Border", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = auraBorderMode,
                                onCheckedChange = onToggleAuraBorder,
                                colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f))
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Spectrum", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = visualizerSpectrum,
                                onCheckedChange = onToggleSpectrum,
                                colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }

        // 3. CENTER ORB WITH SPECTRUM & PULSE
        Box(
            modifier = Modifier
                .size((220 * orbSize).dp)
                .scale(if (isListening || isSpeaking) pulseScale else 1.0f),
            contentAlignment = Alignment.Center
        ) {
            OrbView(
                sizeDp = (180 * orbSize).dp,
                primaryColor = primaryColor,
                secondaryColor = secColor,
                hasRealAvatar = activeAgent.hasRealAvatar,
                isMaster = activeAgent.isMaster,
                isSpeaking = isSpeaking || isListening,
                orbType = "Neon",
                showSpectrum = visualizerSpectrum,
                showAuraBorder = auraBorderMode
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. ACTIVE AGENT NAME & PERSONALITY
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = activeAgent.name,
                color = primaryColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = activeAgent.tagline,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. AGENT SPEAKING DYNAMIC CARD (NEVER STATIC!)
        Card(
            colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, primaryColor.copy(alpha = if (isSpeaking) 0.9f else 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) primaryColor else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${activeAgent.name.uppercase()} SPEAKING (${activeAgent.voiceName})",
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (isSpeaking) {
                        IconButton(
                            onClick = onStopSpeaking,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = SonaRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = agentSpeakingText,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. LIVE TRANSCRIPT CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = EmpireDarkSurface),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isListening) Color(0xFF00E5FF) else EmpireDarkCardBorder, RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isListening) "LIVE TRANSCRIPTION STREAM (LISTENING...)" else "VOICE TRANSCRIPT",
                    color = if (isListening) Color(0xFF00E5FF) else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transcript,
                    color = if (isListening) Color(0xFFE0F7FA) else TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 7. MAIN MIC ACTION BAR (72dp Glowing Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Clone Sample Button
            Button(
                onClick = onTestVoiceClone,
                colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SonaGold.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = SonaGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test VC_7F3A9C", color = SonaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Main Central Mic Button
            val micContainerColor = if (isListening) Color(0xFF00E5FF) else if (isSpeaking) SonaGold else primaryColor
            IconButton(
                onClick = onToggleVoiceListening,
                modifier = Modifier
                    .size(72.dp)
                    .scale(if (isListening) pulseScale else 1.0f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(micContainerColor, EmpireDarkCard)
                        )
                    )
                    .border(2.5.dp, micContainerColor, CircleShape)
                    .testTag("mic_toggle_speak_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else if (isSpeaking) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Mode Action",
                    tint = EmpireDarkBg,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Stop / Mute Action Button
            IconButton(
                onClick = {
                    if (isSpeaking) onStopSpeaking()
                    else onToggleSpeakerMute()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EmpireDarkCard)
                    .border(1.dp, EmpireDarkCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else if (isSpeakerMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Audio Action",
                    tint = if (isSpeaking) SonaRed else primaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8. INTERACTIVE QUICK VOICE PROMPTS (Tap to test real voice query instantly!)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Instant Voice Prompts (Tap to trigger real LLM):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            val prompts = when (activeAgent.id) {
                "kayra" -> listOf("Hey Kayra, check Dubai OTP", "Dubai jobs ATS score", "Run WhatsApp automation", "Screen auto tap")
                "myra" -> listOf("Hello Boss with love", "MYRA activate Visual Lens", "Connect JARVIS PC", "Deep research Dubai")
                "maya" -> listOf("ACHHA JI Maya, GF drama", "Maya what is today's mood?", "Check Dubai weather", "Unlimited energy check")
                "sona" -> listOf("Hail Sona, empire status", "ATS Live 85/100 score", "Deploy all 10 sub-agents", "Master crown report")
                "stonic" -> listOf("Report zero cost telemetry", "Cache hit rate check", "Tokens processed count")
                "zoya" -> listOf("Zoya, call contact", "Zoya send WhatsApp", "Open app terminal")
                "vyron" -> listOf("Knox shield scan", "Biometric vault check", "OTP firewall status")
                "jarvis" -> listOf("JARVIS connect 81MB PC", "Aura RGB sync", "Run desktop shell")
                "archer" -> listOf("Dubai Silicon Oasis route", "Today headlines stream", "Milestone memory check")
                "nova" -> listOf("Synthesize research dossier", "Academic citations query", "Knowledge graph")
                "luna" -> listOf("Circadian sleep calibration", "Play starlight audio", "Subconscious dream journal")
                else -> listOf("Hello Boss", "Tell me your features", "Empire status")
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(prompts) { p ->
                    Box(
                        modifier = Modifier
                            .background(EmpireDarkCard, RoundedCornerShape(16.dp))
                            .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onProcessVoiceCommand(p) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = primaryColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = p, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DIRECT TEXT / VOICE INPUT BAR (Type or Dictate anytime)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmpireDarkCard, RoundedCornerShape(24.dp))
                .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customQueryInput,
                onValueChange = { customQueryInput = it },
                placeholder = { Text("Speak or type command for ${activeAgent.name}...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mic_command_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (customQueryInput.isNotBlank()) {
                        onProcessVoiceCommand(customQueryInput)
                        customQueryInput = ""
                    }
                })
            )
            IconButton(
                onClick = {
                    if (customQueryInput.isNotBlank()) {
                        onProcessVoiceCommand(customQueryInput)
                        customQueryInput = ""
                    } else {
                        onToggleVoiceListening()
                    }
                },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
            ) {
                Icon(
                    imageVector = if (customQueryInput.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                    contentDescription = "Send",
                    tint = EmpireDarkBg,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 9. QUICK ACTIONS ROW: [Clear Chat] [Copy Last] [Save to Memory] [Switch to Chat Tab]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onClearHistory,
                colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmpireDarkCardBorder),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear History", color = TextSecondary, fontSize = 10.sp)
            }

            Button(
                onClick = { onCopyLastTranscript(context) },
                colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmpireDarkCardBorder),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Last", color = primaryColor, fontSize = 10.sp)
            }

            Button(
                onClick = onSaveTranscriptToMemory,
                colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmpireDarkCardBorder),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = SonaGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Memory", color = SonaGold, fontSize = 10.sp)
            }

            Button(
                onClick = onSwitchToChat,
                colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmpireDarkCardBorder),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Chat Tab", color = Color(0xFF00E5FF), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 10. LIVE TRANSCRIPT HISTORY LIST (LAST 5 COMMANDS WITH TIMESTAMPS)
        AnimatedVisibility(visible = showHistorySection || transcriptHistory.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Transcript History (Recent Voice Logs)",
                            color = SonaGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${transcriptHistory.size} items",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (transcriptHistory.isEmpty()) {
                        Text("No recent voice commands. Tap mic above to start speaking!", color = TextMuted, fontSize = 11.sp)
                    } else {
                        transcriptHistory.take(5).forEach { entry ->
                            val agentCol = try {
                                Color(android.graphics.Color.parseColor(entry.agentColor))
                            } catch (e: Exception) {
                                SonaGold
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(EmpireDarkSurface, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Boss: \"${entry.userQuery}\"",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = entry.timestamp,
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${entry.agentName}: ${entry.agentResponse}",
                                    color = agentCol,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 11. PERSONA SWITCHER CHIPS (11 AGENTS - ONE BY ONE - MYRA SE START)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERSONA SWITCHER (11 AGENTS)",
                    color = SonaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = "Tap to test instantly",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allAgents) { agent ->
                    val isSelected = agent.id == activeAgent.id
                    val c = remember(agent.colorHex) {
                        try {
                            Color(android.graphics.Color.parseColor(agent.colorHex))
                        } catch (e: Exception) {
                            SonaGold
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) c.copy(alpha = 0.28f) else EmpireDarkCard,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) c else EmpireDarkCardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectAgent(agent) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("persona_chip_${agent.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(c)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = agent.name,
                                color = if (isSelected) c else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
