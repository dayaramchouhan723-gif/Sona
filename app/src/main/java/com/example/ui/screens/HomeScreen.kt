package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AgentProfile
import com.example.ui.components.OrbView
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
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    agents: List<AgentProfile>,
    connectedCount: Int,
    voiceCloneId: String,
    launcherCore: String,
    currentUser: FirebaseUser?,
    isSigningIn: Boolean,
    onSelectAgent: (AgentProfile) -> Unit,
    onTalkAgent: (AgentProfile) -> Unit,
    onOpenConnectors: () -> Unit,
    onOpenVoiceClone: () -> Unit,
    onOpenOrbCustomizer: () -> Unit,
    onOpenLauncherCore: () -> Unit,
    onGoogleSignInClicked: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    val masterSona = remember(agents) { agents.firstOrNull { it.isMaster } ?: agents.last() }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(EmpireDarkBg)
            .testTag("home_screen_grid"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. ROYAL BANNER & GOOGLE LOGIN STATUS (Full Width)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmpireDarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(SonaGold, SonaRed, SonaGold)),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column {
                    // Header Banner Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.empire_banner),
                            contentDescription = "Chouhan Empire Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, EmpireDarkSurface)
                                    )
                                )
                        )

                        // Floating Crown Title Badge with Custom Launcher Icon visible on Home!
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Custom Launcher Icon (Golden Crown with Red Orbital Rings)
                            Image(
                                painter = painterResource(id = R.drawable.ic_sona_launcher),
                                contentDescription = "SONA Imperial Icon",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SonaGold, CircleShape)
                                    .testTag("home_launcher_icon")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "SONA - CHOUHAN EMPIRE",
                                    color = SonaGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Intelligence that understands. Empire that cares.",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Developer Subtitle & Google Sign-In Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Design by: Dayaram Chouhan",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Develop by: Dayaram Chouhan",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        // Google Sign-In or Active Profile with Free Lifetime Badge
                        if (currentUser != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF132B1F), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFF00E676).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Column {
                                            Text(
                                                text = currentUser.email ?: "ds.jaan24@gmail.com",
                                                color = Color(0xFF00E676),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Free Lifetime ∞ • Synced",
                                                color = SonaGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = onSignOutClicked,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Sign Out",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else {
                            // Sign-In with Google Button
                            Button(
                                onClick = onGoogleSignInClicked,
                                colors = ButtonDefaults.buttonColors(containerColor = SonaGold),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isSigningIn,
                                modifier = Modifier.testTag("google_sign_in_button")
                            ) {
                                if (isSigningIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = EmpireDarkBg,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = EmpireDarkBg,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Google Sign In",
                                        color = EmpireDarkBg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. FEATURED QUEEN SONA MASTER CARD (Full Width)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(SonaGold, SonaRed, Color(0xFF7C4DFF))),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectAgent(masterSona) }
                    .testTag("featured_sona_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrbView(
                        sizeDp = 84.dp,
                        primaryColor = SonaGold,
                        secondaryColor = SonaRed,
                        isMaster = true,
                        orbType = "Neon"
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = masterSona.name,
                                color = SonaGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(SonaRed, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ATS LIVE 85/100",
                                    color = TextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = masterSona.tagline,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(EmpireDarkBg, RoundedCornerShape(6.dp))
                                    .border(1.dp, SonaGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "20 Master Tools",
                                    color = SonaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(EmpireDarkBg, RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Controls All 10",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSelectAgent(masterSona) },
                        colors = ButtonDefaults.buttonColors(containerColor = SonaGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Command", color = EmpireDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // 3. QUICK SYSTEM CONTROLS ROW (Full Width)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Connectors Button
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenConnectors() }
                        .testTag("quick_connectors_button")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = SonaGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Connectors", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$connectedCount of 20", color = Color(0xFF00E676), fontSize = 9.sp)
                    }
                }

                // Voice Clone Studio
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenVoiceClone() }
                        .testTag("quick_voice_clone_button")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MyraPink, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Voice Clone", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(voiceCloneId, color = TextMuted, fontSize = 9.sp)
                    }
                }

                // Orb Customizer
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenOrbCustomizer() }
                        .testTag("quick_orb_customizer_button")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Orb Custom", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Size & Aura", color = TextMuted, fontSize = 9.sp)
                    }
                }

                // Launcher Core
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onOpenLauncherCore() }
                        .testTag("quick_launcher_core_button")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DashboardCustomize, contentDescription = null, tint = SonaRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Launcher", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(launcherCore, color = SonaRed, fontSize = 9.sp, maxLines = 1)
                    }
                }
            }
        }

        // 4. MULTI-AGENT HUB HEADER
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "11-IN-1 MULTI-AGENT HUB",
                    color = SonaGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "MYRA se start • All 100% Free",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // 5. 11 AGENTS GRID ITEMS - All Clickable with Breathing Orb Animation
        items(agents) { agent ->
            val color = remember(agent.colorHex) {
                try {
                    Color(android.graphics.Color.parseColor(agent.colorHex))
                } catch (e: Exception) {
                    SonaGold
                }
            }
            val secColor = remember(agent.secondaryColorHex) {
                try {
                    Color(android.graphics.Color.parseColor(agent.secondaryColorHex))
                } catch (e: Exception) {
                    SonaRed
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (agent.isMaster) SonaGold else color.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectAgent(agent) }
                    .testTag("agent_card_${agent.id}")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Orb or Real Girl Avatar
                    OrbView(
                        sizeDp = 76.dp,
                        primaryColor = color,
                        secondaryColor = secColor,
                        hasRealAvatar = agent.hasRealAvatar,
                        isMaster = agent.isMaster,
                        orbType = "Neon"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = agent.name,
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = agent.tagline,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tools count & Memory badge
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(EmpireDarkBg, RoundedCornerShape(6.dp))
                                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${agent.toolsCount} Tools",
                                color = color,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(EmpireDarkBg, RoundedCornerShape(6.dp))
                                .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = agent.memoryFile.replace(".json", ""),
                                color = TextMuted,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons: Overview / Talk
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSelectAgent(agent) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, color, RoundedCornerShape(8.dp)),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text("Overview", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onTalkAgent(agent) },
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = EmpireDarkBg, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Talk", color = EmpireDarkBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Bottom space padding
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
