package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentProfile
import com.example.ui.dialogs.AgentOverviewDialog
import com.example.ui.dialogs.ConnectorsDialog
import com.example.ui.dialogs.LauncherCoreDialog
import com.example.ui.dialogs.OrbCustomizerDialog
import com.example.ui.dialogs.VoiceCloneStudioDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.MicScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SonaGold
import com.example.ui.theme.SonaRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SonaEmpireApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SonaEmpireApp(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedAgentForOverview by viewModel.selectedAgentForOverview.collectAsState()
    val showConnectorsDialog by viewModel.showConnectorsDialog.collectAsState()
    val showVoiceCloneDialog by viewModel.showVoiceCloneDialog.collectAsState()
    val showOrbCustomizerDialog by viewModel.showOrbCustomizerDialog.collectAsState()
    val showLauncherCoreDialog by viewModel.showLauncherCoreDialog.collectAsState()

    val connectors by viewModel.connectors.collectAsState()
    val connectedCount = remember(connectors) { connectors.count { it.isConnected } }
    val voiceCloneId = viewModel.voiceCloneId
    val isRecordingClone by viewModel.isRecordingClone.collectAsState()
    val cloneRecordProgress by viewModel.cloneRecordProgress.collectAsState()
    val isCloningSuccess by viewModel.isCloningSuccess.collectAsState()

    val currentOrbType by viewModel.orbType.collectAsState()
    val currentOrbSize by viewModel.orbSize.collectAsState()
    val auraBorderMode by viewModel.auraBorderMode.collectAsState()
    val visualizerSpectrum by viewModel.visualizerSpectrum.collectAsState()
    val launcherCore by viewModel.launcherCore.collectAsState()

    val selectedMemoryFilename by viewModel.selectedMemoryFilename.collectAsState()
    val currentMemoryContent by viewModel.currentMemoryContent.collectAsState()

    val isOcrActive by viewModel.isOcrActive.collectAsState()
    val isLiveTranscriptionActive by viewModel.isLiveTranscriptionActive.collectAsState()
    val atsScore by viewModel.atsScore.collectAsState()

    val activeChatAgent by viewModel.activeChatAgent.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatInputText by viewModel.chatInputText.collectAsState()

    val voiceStatus by viewModel.voiceStatus.collectAsState()
    val isVoiceModeSpeaking = voiceStatus == com.example.viewmodel.VoiceStatus.SPEAKING
    val voiceModeTranscript by viewModel.voiceModeTranscript.collectAsState()
    val agentSpeakingText by viewModel.agentSpeakingText.collectAsState()
    val isSpeakerMuted by viewModel.isSpeakerMuted.collectAsState()
    val transcriptHistory by viewModel.transcriptHistory.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val isSigningIn by viewModel.isSigningIn.collectAsState()
    val context = LocalContext.current

    val toastNotice by viewModel.toastNotice.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmpireDarkBg)
    ) {
        // Main Screen Area with Inset Padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    agents = viewModel.agents,
                    connectedCount = connectedCount,
                    voiceCloneId = voiceCloneId,
                    launcherCore = launcherCore,
                    currentUser = currentUser,
                    isSigningIn = isSigningIn,
                    onSelectAgent = { viewModel.openAgentOverview(it) },
                    onTalkAgent = {
                        viewModel.setActiveChatAgent(it)
                        viewModel.selectTab(4)
                    },
                    onOpenConnectors = { viewModel.openConnectorsDialog() },
                    onOpenVoiceClone = { viewModel.openVoiceCloneDialog() },
                    onOpenOrbCustomizer = { viewModel.openOrbCustomizerDialog() },
                    onOpenLauncherCore = { viewModel.openLauncherCoreDialog() },
                    onGoogleSignInClicked = {
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.signInWithGoogle(activity)
                        }
                    },
                    onSignOutClicked = { viewModel.signOut() }
                )
                1 -> ScanScreen(
                    isOcrActive = isOcrActive,
                    isLiveTranscriptionActive = isLiveTranscriptionActive,
                    atsScore = atsScore,
                    onToggleOcr = { viewModel.toggleOcr() },
                    onToggleTranscription = { viewModel.toggleLiveTranscription() },
                    onShowNotice = { viewModel.showNotice(it) }
                )
                2 -> MicScreen(
                    activeAgent = activeChatAgent,
                    allAgents = viewModel.agents,
                    voiceStatus = voiceStatus,
                    transcript = voiceModeTranscript,
                    agentSpeakingText = agentSpeakingText,
                    isSpeakerMuted = isSpeakerMuted,
                    transcriptHistory = transcriptHistory,
                    orbSize = currentOrbSize,
                    auraBorderMode = auraBorderMode,
                    visualizerSpectrum = visualizerSpectrum,
                    onSelectAgent = { viewModel.selectPersona(it, speakImmediateGreeting = true) },
                    onToggleVoiceListening = { viewModel.toggleVoiceListening() },
                    onProcessVoiceCommand = { viewModel.processVoiceCommand(it) },
                    onStopSpeaking = { viewModel.stopSpeaking() },
                    onToggleSpeakerMute = { viewModel.toggleSpeakerMute() },
                    onTestVoiceClone = { viewModel.testVoiceCloneSample() },
                    onClearHistory = { viewModel.clearTranscriptHistory() },
                    onCopyLastTranscript = { viewModel.copyLastTranscript(it) },
                    onSaveTranscriptToMemory = { viewModel.saveTranscriptToMemory() },
                    onNavigateHome = { viewModel.selectTab(0) },
                    onSwitchToChat = { viewModel.selectTab(4) },
                    onSetOrbSize = { viewModel.setOrbSize(it) },
                    onToggleAuraBorder = { viewModel.setAuraBorderMode(it) },
                    onToggleSpectrum = { viewModel.setVisualizerSpectrum(it) }
                )
                3 -> MemoriesScreen(
                    selectedFilename = selectedMemoryFilename,
                    fileContent = currentMemoryContent,
                    onSelectFile = { viewModel.selectMemoryFile(it) },
                    onShowNotice = { viewModel.showNotice(it) }
                )
                4 -> ChatScreen(
                    activeAgent = activeChatAgent,
                    allAgents = viewModel.agents,
                    messages = chatMessages,
                    inputText = chatInputText,
                    onSelectAgent = { viewModel.setActiveChatAgent(it) },
                    onInputChanged = { viewModel.updateChatInput(it) },
                    onSendMessage = { viewModel.sendChatMessage() }
                )
            }
        }

        // Custom Bottom Navigation Bar with Center Blue Mic FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(EmpireDarkCardBorder, SonaGold.copy(alpha = 0.5f), EmpireDarkCardBorder)
                        ),
                        RoundedCornerShape(32.dp)
                    ),
                color = EmpireDarkSurface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 0: Home
                    BottomNavItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isSelected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) }
                    )

                    // Tab 1: Scan
                    BottomNavItem(
                        icon = Icons.Default.DocumentScanner,
                        label = "Scan",
                        isSelected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) }
                    )

                    // Spacer for center elevated Mic button
                    Spacer(modifier = Modifier.size(56.dp))

                    // Tab 3: Memories
                    BottomNavItem(
                        icon = Icons.Default.FolderShared,
                        label = "Memories",
                        isSelected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) }
                    )

                    // Tab 4: Chat
                    BottomNavItem(
                        icon = Icons.Default.Chat,
                        label = "Chat",
                        isSelected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) }
                    )
                }
            }

            // Center Blue Mic FAB (Elevated)
            FloatingActionButton(
                onClick = { viewModel.selectTab(2) },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(58.dp)
                    .testTag("center_blue_mic_button"),
                shape = CircleShape,
                containerColor = Color(0xFF00B0FF),
                contentColor = TextPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Mode",
                    modifier = Modifier.size(30.dp),
                    tint = EmpireDarkBg
                )
            }
        }

        // Notification Snackbar Banner
        val currentNotice = toastNotice
        AnimatedVisibility(
            visible = currentNotice != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            if (currentNotice != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SonaGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentNotice,
                            color = EmpireDarkBg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearNotice() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EmpireDarkBg)
                        }
                    }
                }
            }
        }

        // OVERVIEW DIALOG (When user taps any agent)
        selectedAgentForOverview?.let { agent ->
            AgentOverviewDialog(
                agent = agent,
                isSpeaking = isVoiceModeSpeaking,
                onDismiss = { viewModel.closeAgentOverview() },
                onPlayVoiceSample = { viewModel.speakAgentSample(agent) },
                onStopVoiceSample = { viewModel.stopSpeaking() },
                onTalkNow = {
                    viewModel.setActiveChatAgent(agent)
                    viewModel.closeAgentOverview()
                    viewModel.selectTab(4)
                },
                onOpenVoiceClone = {
                    viewModel.closeAgentOverview()
                    viewModel.openVoiceCloneDialog()
                },
                onInspectMemory = {
                    viewModel.closeAgentOverview()
                    viewModel.selectMemoryFile(agent.memoryFile)
                    viewModel.selectTab(3)
                }
            )
        }

        // 20 CONNECTORS DIALOG
        if (showConnectorsDialog) {
            ConnectorsDialog(
                connectors = connectors,
                onDismiss = { viewModel.closeConnectorsDialog() },
                onSaveKey = { id, key -> viewModel.updateConnectorKey(id, key) }
            )
        }

        // VOICE CLONE STUDIO DIALOG
        if (showVoiceCloneDialog) {
            VoiceCloneStudioDialog(
                voiceCloneId = voiceCloneId,
                isRecording = isRecordingClone,
                progress = cloneRecordProgress,
                isCloned = isCloningSuccess,
                onDismiss = { viewModel.closeVoiceCloneDialog() },
                onStartRecording = { viewModel.startVoiceCloneRecording() },
                onPlaySample = { viewModel.playVoiceCloneSample() }
            )
        }

        // ORB CUSTOMIZER DIALOG
        if (showOrbCustomizerDialog) {
            OrbCustomizerDialog(
                currentOrbType = currentOrbType,
                currentOrbSize = currentOrbSize,
                auraBorderMode = auraBorderMode,
                visualizerSpectrum = visualizerSpectrum,
                onDismiss = { viewModel.closeOrbCustomizerDialog() },
                onSetOrbType = { viewModel.setOrbType(it) },
                onSetOrbSize = { viewModel.setOrbSize(it) },
                onToggleAuraBorder = { viewModel.setAuraBorderMode(it) },
                onToggleVisualizerSpectrum = { viewModel.setVisualizerSpectrum(it) }
            )
        }

        // LAUNCHER CORE DIALOG
        if (showLauncherCoreDialog) {
            LauncherCoreDialog(
                currentCore = launcherCore,
                onDismiss = { viewModel.closeLauncherCoreDialog() },
                onSelectCore = { viewModel.setLauncherCore(it) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("nav_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) SonaGold else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) SonaGold else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
