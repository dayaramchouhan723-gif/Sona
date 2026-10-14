package com.example.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.Avatar4ModesView
import com.example.ui.components.AvatarMode
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.MyraPink
import com.example.ui.theme.SonaGold
import com.example.ui.theme.SonaRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OrbCustomizerDialog(
    currentOrbType: String,
    currentOrbSize: Float,
    auraBorderMode: Boolean,
    visualizerSpectrum: Boolean,
    currentAvatarMode: AvatarMode = AvatarMode.PARTICLE_GLOBE,
    onDismiss: () -> Unit,
    onSetOrbType: (String) -> Unit,
    onSetOrbSize: (Float) -> Unit,
    onToggleAuraBorder: (Boolean) -> Unit,
    onToggleVisualizerSpectrum: (Boolean) -> Unit,
    onSetAvatarMode: (AvatarMode) -> Unit = {}
) {
    var selectedAvatarMode by remember { mutableStateOf(currentAvatarMode) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, MyraPink, RoundedCornerShape(24.dp))
                .testTag("orb_customizer_dialog"),
            color = EmpireDarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(scrollState),
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
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Customize",
                            tint = MyraPink,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AVATAR & ORB SETTINGS",
                            color = MyraPink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Preview with Selected Avatar Mode
                Avatar4ModesView(
                    currentMode = selectedAvatarMode,
                    isSpeaking = false,
                    sizeDp = (160 * currentOrbSize).dp,
                    onModeChanged = {
                        selectedAvatarMode = it
                        onSetAvatarMode(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // AVATAR 4 MODES SELECTOR
                Text(
                    text = "Avatar 4 Modes Toggle",
                    color = SonaGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AvatarMode.values().forEach { mode ->
                        val isSelected = mode == selectedAvatarMode
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EmpireDarkBg else EmpireDarkCard
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isSelected) SonaGold else EmpireDarkCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.title,
                                        color = if (isSelected) SonaGold else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = mode.subtitle,
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedAvatarMode = mode
                                        onSetAvatarMode(mode)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) SonaGold else EmpireDarkSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "Active" else "Select",
                                        color = if (isSelected) EmpireDarkBg else TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Orb Core Style Selector: Classic / Energy / Neon
                Text(
                    text = "Orb Core Style",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Classic", "Energy", "Neon").forEach { type ->
                        val isSelected = currentOrbType == type
                        Button(
                            onClick = { onSetOrbType(type) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MyraPink else EmpireDarkCard
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isSelected) MyraPink else EmpireDarkCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) EmpireDarkBg else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Avatar & Orb Scale", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${(currentOrbSize * 100).toInt()}%", color = SonaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = currentOrbSize,
                    onValueChange = onSetOrbSize,
                    valueRange = 0.6f..1.4f,
                    colors = SliderDefaults.colors(
                        thumbColor = SonaGold,
                        activeTrackColor = MyraPink,
                        inactiveTrackColor = EmpireDarkCard
                    ),
                    modifier = Modifier.testTag("orb_size_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggles: Aura Border Mode & Spectrum Visualizer
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Aura Border Glow Ring", color = TextPrimary, fontSize = 12.sp)
                            Switch(
                                checked = auraBorderMode,
                                onCheckedChange = onToggleAuraBorder,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MyraPink,
                                    checkedTrackColor = EmpireDarkBg
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Audio Visualizer Spectrum Bars", color = TextPrimary, fontSize = 12.sp)
                            Switch(
                                checked = visualizerSpectrum,
                                onCheckedChange = onToggleVisualizerSpectrum,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SonaGold,
                                    checkedTrackColor = EmpireDarkBg
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MyraPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply to Empire", color = EmpireDarkBg, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
