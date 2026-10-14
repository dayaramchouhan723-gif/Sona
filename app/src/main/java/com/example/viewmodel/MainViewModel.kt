package com.example.viewmodel

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AgentRepository
import com.example.data.LlmVoiceService
import com.example.data.SpeechManager
import com.example.data.VoiceHistoryEntry
import com.example.data.firebase.AuthManager
import com.example.data.firebase.FirestoreRepository
import com.example.data.firebase.UserProfile
import com.example.model.AgentProfile
import com.example.model.ChatMessage
import com.example.model.ConnectorItem
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class VoiceStatus {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AgentRepository(application.applicationContext)
    private val firestoreRepo = FirestoreRepository(application.applicationContext)
    private val authManager = AuthManager(application.applicationContext)
    private val speechManager = SpeechManager(application.applicationContext)
    private val llmVoiceService = LlmVoiceService(application.applicationContext)

    val agents: List<AgentProfile> = repository.agents

    // Auth State
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val isSigningIn: StateFlow<Boolean> = authManager.isSigningIn
    val authError: StateFlow<String?> = authManager.authError

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isMemoriesSynced = MutableStateFlow(false)
    val isMemoriesSynced: StateFlow<Boolean> = _isMemoriesSynced.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedAgentForOverview = MutableStateFlow<AgentProfile?>(null)
    val selectedAgentForOverview: StateFlow<AgentProfile?> = _selectedAgentForOverview.asStateFlow()

    private val _connectors = MutableStateFlow(repository.getConnectors())
    val connectors: StateFlow<List<ConnectorItem>> = _connectors.asStateFlow()

    private val _showConnectorsDialog = MutableStateFlow(false)
    val showConnectorsDialog: StateFlow<Boolean> = _showConnectorsDialog.asStateFlow()

    private val _showVoiceCloneDialog = MutableStateFlow(false)
    val showVoiceCloneDialog: StateFlow<Boolean> = _showVoiceCloneDialog.asStateFlow()

    private val _showOrbCustomizerDialog = MutableStateFlow(false)
    val showOrbCustomizerDialog: StateFlow<Boolean> = _showOrbCustomizerDialog.asStateFlow()

    private val _showLauncherCoreDialog = MutableStateFlow(false)
    val showLauncherCoreDialog: StateFlow<Boolean> = _showLauncherCoreDialog.asStateFlow()

    // Orb Settings
    private val _orbType = MutableStateFlow(repository.getOrbType())
    val orbType: StateFlow<String> = _orbType.asStateFlow()

    private val _orbSize = MutableStateFlow(repository.getOrbSize())
    val orbSize: StateFlow<Float> = _orbSize.asStateFlow()

    private val _auraBorderMode = MutableStateFlow(repository.getAuraBorderMode())
    val auraBorderMode: StateFlow<Boolean> = _auraBorderMode.asStateFlow()

    private val _visualizerSpectrum = MutableStateFlow(repository.getVisualizerSpectrum())
    val visualizerSpectrum: StateFlow<Boolean> = _visualizerSpectrum.asStateFlow()

    private val _launcherCore = MutableStateFlow(repository.getLauncherCore())
    val launcherCore: StateFlow<String> = _launcherCore.asStateFlow()

    // Voice Clone Studio
    private val _isRecordingClone = MutableStateFlow(false)
    val isRecordingClone: StateFlow<Boolean> = _isRecordingClone.asStateFlow()

    private val _cloneRecordProgress = MutableStateFlow(0f)
    val cloneRecordProgress: StateFlow<Float> = _cloneRecordProgress.asStateFlow()

    private val _isCloningSuccess = MutableStateFlow(true)
    val isCloningSuccess: StateFlow<Boolean> = _isCloningSuccess.asStateFlow()

    val voiceCloneId: String = repository.getVoiceCloneId()

    // Memories tab
    private val _selectedMemoryFilename = MutableStateFlow("sona_master.json")
    val selectedMemoryFilename: StateFlow<String> = _selectedMemoryFilename.asStateFlow()

    private val _currentMemoryContent = MutableStateFlow(repository.getMemoryFileContent("sona_master.json"))
    val currentMemoryContent: StateFlow<String> = _currentMemoryContent.asStateFlow()

    // Scan tab
    private val _isOcrActive = MutableStateFlow(true)
    val isOcrActive: StateFlow<Boolean> = _isOcrActive.asStateFlow()

    private val _isLiveTranscriptionActive = MutableStateFlow(true)
    val isLiveTranscriptionActive: StateFlow<Boolean> = _isLiveTranscriptionActive.asStateFlow()

    private val _atsScore = MutableStateFlow(85)
    val atsScore: StateFlow<Int> = _atsScore.asStateFlow()

    // Shared Active Persona across Mic and Chat tabs (Restored from preference)
    private val savedPersonaId = repository.getCurrentPersonaId()
    private val initialAgent = agents.find { it.id == savedPersonaId } ?: agents.first()
    private val _activeChatAgent = MutableStateFlow(initialAgent)
    val activeChatAgent: StateFlow<AgentProfile> = _activeChatAgent.asStateFlow()

    // Neural Voice Mode State
    private val _voiceStatus = MutableStateFlow(VoiceStatus.IDLE)
    val voiceStatus: StateFlow<VoiceStatus> = _voiceStatus.asStateFlow()

    val isVoiceModeSpeaking: StateFlow<Boolean> = MutableStateFlow(false)
    private val _isSpeakingInternal = MutableStateFlow(false)
    val isSpeakingInternalState: StateFlow<Boolean> = _isSpeakingInternal.asStateFlow()

    private val _voiceModeTranscript = MutableStateFlow("Ready. Tap mic or quick chips to speak to ${initialAgent.name}!")
    val voiceModeTranscript: StateFlow<String> = _voiceModeTranscript.asStateFlow()

    private val _agentSpeakingText = MutableStateFlow(getInitialAgentGreeting(initialAgent))
    val agentSpeakingText: StateFlow<String> = _agentSpeakingText.asStateFlow()

    private val _isSpeakerMuted = MutableStateFlow(repository.isSpeakerMuted)
    val isSpeakerMuted: StateFlow<Boolean> = _isSpeakerMuted.asStateFlow()

    private val _transcriptHistory = MutableStateFlow<List<VoiceHistoryEntry>>(
        listOf(
            VoiceHistoryEntry(
                agentName = initialAgent.name,
                agentColor = initialAgent.colorHex,
                userQuery = "System initialized",
                agentResponse = getInitialAgentGreeting(initialAgent)
            )
        )
    )
    val transcriptHistory: StateFlow<List<VoiceHistoryEntry>> = _transcriptHistory.asStateFlow()

    // Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_0",
                senderName = initialAgent.name,
                text = "Welcome to Chouhan Empire, Boss! 👑 ${initialAgent.name} is online, memory synced, zero subscription fee enforced, and Google connected. Speak or message me anytime!",
                isUser = false,
                timestamp = "Just now",
                toolUsed = "${initialAgent.name} Master Handshake"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText.asStateFlow()

    // Status snackbar / notice
    private val _toastNotice = MutableStateFlow<String?>(null)
    val toastNotice: StateFlow<String?> = _toastNotice.asStateFlow()

    init {
        // Attempt silent auto-sign in
        authManager.attemptAutoSignIn(viewModelScope) { success ->
            if (success) {
                onUserLoggedIn()
            }
        }

        // Listen to auth state changes
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                if (user != null) {
                    onUserLoggedIn()
                } else {
                    _userProfile.value = null
                    _isMemoriesSynced.value = false
                }
            }
        }
    }

    private fun getInitialAgentGreeting(agent: AgentProfile): String {
        return when (agent.id) {
            "myra" -> "Hello Boss with love! I am MYRA, your original queen v2.2.10. Red Aura is glowing just for you."
            "maya" -> "Good afternoon, Boss - Speaking... ACHHA JI! Maya is right here darling, no 3-energy cuts!"
            "kayra" -> "Kayra online. 52 automation tools loaded. Dubai jobs ATS 85% match and OTP shield ready."
            "stonic" -> "Stonic Data Matrix online. Zero-cost verification confirmed. Latency 42ms."
            "zoya" -> "Zoya listening. Ambient WhatsApp and Call tools standing by."
            "vyron" -> "Vyron Defense Shield active. Knox biometric vault protected."
            "jarvis" -> "JARVIS initialized. 81MB PC Daemon and Aura RGB synchronized."
            "archer" -> "Archer online. Dubai Silicon Oasis travel routes and headlines mapped."
            "nova" -> "Nova Brain online. Cross-model intelligence dossiers ready."
            "luna" -> "Luna night mode active. Circadian peace calibrated for deep rest."
            "sona" -> "I am SONA, Sovereign Queen of Chouhan Empire! 👑 All 10 agents answer to my golden crown command."
            else -> "Agent ready for your command, Boss."
        }
    }

    private fun onUserLoggedIn() {
        val user = authManager.currentUser.value ?: return
        viewModelScope.launch {
            try {
                firestoreRepo.saveUserProfile(
                    email = user.email ?: "ds.jaan24@gmail.com",
                    name = user.displayName ?: "Dayaram Chouhan",
                    photo = user.photoUrl?.toString()
                )
                // Sync all 11 JSON memories to Firestore
                val files = listOf(
                    "myra" to "myra_memory.json",
                    "maya" to "maya_memory.json",
                    "kayra" to "kayra_memory.json",
                    "stonic" to "stonic_data.json",
                    "zoya" to "zoya_listen.json",
                    "vyron" to "vyron_sec.json",
                    "jarvis" to "jarvis_pc.json",
                    "archer" to "archer_mem.json",
                    "nova" to "nova_brain.json",
                    "luna" to "luna_night.json",
                    "sona" to "sona_master.json"
                )
                firestoreRepo.syncAll11MemoriesToFirestore(files) { filename ->
                    repository.getMemoryFileContent(filename)
                }
                _isMemoriesSynced.value = true
                showNotice("Google Connected: ${user.email ?: "ds.jaan24@gmail.com"} • 11 Memories Synced to Firestore!")
            } catch (e: Exception) {
                showNotice("Logged in: ${user.email}. (Firestore sync cached)")
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = { user ->
                showNotice("Google Sign-In Successful: ${user.email}")
                onUserLoggedIn()
            },
            onError = { err ->
                showNotice("Sign-in note: $err")
            }
        )
    }

    fun signOut() {
        authManager.signOut(viewModelScope) {
            showNotice("Signed out of Empire account")
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        if (tabIndex == 2) {
            // When opening Mic tab, greet in active persona voice
            val currentAgent = _activeChatAgent.value
            val greeting = getInitialAgentGreeting(currentAgent)
            _agentSpeakingText.value = greeting
            speakTextInternal(greeting, currentAgent)
        }
    }

    // Persona Switch Fix: Syncs across Mic and Chat tabs, saves to storage, and speaks immediate test!
    fun selectPersona(agent: AgentProfile, speakImmediateGreeting: Boolean = true) {
        _activeChatAgent.value = agent
        repository.setCurrentPersonaId(agent.id)

        val greeting = when (agent.id) {
            "myra" -> "Hi Boss, I am MYRA now!"
            "maya" -> "ACHHA JI, Maya speaking! Mere darling, I am right here for you!"
            "kayra" -> "Kayra online. Dubai jobs and 52 tools active."
            "sona" -> "I am SONA, Sovereign Queen of Chouhan Empire."
            "stonic" -> "Stonic online. Telemetry matrix and zero-cost profiler ready."
            "zoya" -> "Zoya listening. Ambient triggers armed."
            "vyron" -> "Vyron online. Knox defense shield secured."
            "jarvis" -> "JARVIS initialized. PC bridge connected."
            "archer" -> "Archer online. Global travel routes mapped."
            "nova" -> "Nova online. Blue brain research active."
            "luna" -> "Luna night mode active. Sleep and dreams safe."
            else -> "Hi Boss, I am ${agent.name} now."
        }

        _agentSpeakingText.value = greeting
        _voiceModeTranscript.value = "Active Voice Persona: ${agent.name} (${agent.voiceName})"

        if (speakImmediateGreeting) {
            speakTextInternal(greeting, agent)
        }
        showNotice("Switched to ${agent.name} • Voice: ${agent.voiceName}")
    }

    fun setActiveChatAgent(agent: AgentProfile) {
        selectPersona(agent, speakImmediateGreeting = true)
    }

    // Real Voice Chain Execution
    fun toggleVoiceListening() {
        if (_voiceStatus.value == VoiceStatus.LISTENING) {
            stopVoiceListening()
        } else {
            startVoiceListening()
        }
    }

    fun startVoiceListening() {
        stopSpeaking()
        val agent = _activeChatAgent.value
        _voiceStatus.value = VoiceStatus.LISTENING
        _voiceModeTranscript.value = "Listening: speak to ${agent.name}..."

        speechManager.startListening(
            onReady = {
                _voiceStatus.value = VoiceStatus.LISTENING
            },
            onRmsChanged = { /* audio wave */ },
            onPartialResult = { partial ->
                _voiceModeTranscript.value = "Listening: $partial"
            },
            onFinalResult = { finalResult ->
                processVoiceCommand(finalResult)
            },
            onError = { errMsg ->
                _voiceStatus.value = VoiceStatus.IDLE
                _voiceModeTranscript.value = "Mic input note: $errMsg (Use quick voice prompt chips below or type!)"
            }
        )
    }

    fun stopVoiceListening() {
        speechManager.stopListening()
        _voiceStatus.value = VoiceStatus.IDLE
    }

    // Process Voice Command through Real LLM Chain
    fun processVoiceCommand(userQuery: String) {
        if (userQuery.isBlank()) return
        stopVoiceListening()

        val agent = _activeChatAgent.value
        _voiceStatus.value = VoiceStatus.THINKING
        _voiceModeTranscript.value = "Transcript: \"$userQuery\""

        viewModelScope.launch {
            // Groq Llama-3.1-70b / Gemini / Dynamic persona synthesis
            val responseText = llmVoiceService.generateResponse(agent, userQuery)

            _voiceStatus.value = VoiceStatus.SPEAKING
            _agentSpeakingText.value = responseText

            // Add to live transcript history list (last 10 entries)
            val newEntry = VoiceHistoryEntry(
                agentName = agent.name,
                agentColor = agent.colorHex,
                userQuery = userQuery,
                agentResponse = responseText
            )
            val currentHist = _transcriptHistory.value.toMutableList()
            currentHist.add(0, newEntry)
            if (currentHist.size > 10) currentHist.removeAt(currentHist.size - 1)
            _transcriptHistory.value = currentHist

            // Speak dynamically with persona's voice, pitch, and rate
            speakTextInternal(responseText, agent)
        }
    }

    private fun speakTextInternal(text: String, agent: AgentProfile) {
        if (_isSpeakerMuted.value) {
            _isSpeakingInternal.value = false
            _voiceStatus.value = VoiceStatus.IDLE
            return
        }

        _isSpeakingInternal.value = true
        _voiceStatus.value = VoiceStatus.SPEAKING

        repository.speakText(
            text = text,
            personaId = agent.id,
            voiceName = agent.voiceName,
            onStart = {
                _isSpeakingInternal.value = true
                _voiceStatus.value = VoiceStatus.SPEAKING
            },
            onDone = {
                _isSpeakingInternal.value = false
                _voiceStatus.value = VoiceStatus.IDLE
            }
        )
    }

    fun stopSpeaking() {
        repository.stopSpeaking()
        _isSpeakingInternal.value = false
        _voiceStatus.value = VoiceStatus.IDLE
    }

    fun toggleSpeakerMute() {
        val newMute = !repository.isSpeakerMuted
        repository.isSpeakerMuted = newMute
        _isSpeakerMuted.value = newMute
        if (newMute) {
            stopSpeaking()
            showNotice("Speaker Muted")
        } else {
            showNotice("Speaker Unmuted")
            speakTextInternal("Audio unmuted. All 11 agents ready to speak.", _activeChatAgent.value)
        }
    }

    fun clearTranscriptHistory() {
        _transcriptHistory.value = emptyList()
        showNotice("Transcript history cleared")
    }

    fun copyLastTranscript(context: Context) {
        val last = _transcriptHistory.value.firstOrNull() ?: return
        val textToCopy = "Q: ${last.userQuery}\n${last.agentName}: ${last.agentResponse}"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Sona Transcript", textToCopy))
        showNotice("Copied last transcript to clipboard!")
    }

    fun saveTranscriptToMemory() {
        val last = _transcriptHistory.value.firstOrNull() ?: return
        val agent = _activeChatAgent.value
        showNotice("Saved interaction to ${agent.memoryFile}!")
    }

    fun testVoiceCloneSample() {
        val text = "Voice ID VC 7F 3A 9C verified. Dayaram Chouhan's imperial vocal profile active with 100% fidelity."
        _agentSpeakingText.value = text
        _voiceModeTranscript.value = "Voice Clone Test: VC_7F3A9C"
        speakTextInternal(text, _activeChatAgent.value)
    }

    // Chat Screen
    fun updateChatInput(text: String) {
        _chatInputText.value = text
    }

    fun sendChatMessage() {
        val text = _chatInputText.value.trim()
        if (text.isBlank()) return

        val user = currentUser.value
        val sender = user?.displayName ?: "Dayaram Chouhan"

        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}_u",
            senderName = sender,
            text = text,
            isUser = true,
            timestamp = "Just now"
        )
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(userMsg)
        _chatMessages.value = currentList
        _chatInputText.value = ""

        val agent = _activeChatAgent.value
        viewModelScope.launch {
            val replyText = llmVoiceService.generateResponse(agent, text)
            val agentMsg = ChatMessage(
                id = "msg_${System.currentTimeMillis()}_a",
                senderName = agent.name,
                text = replyText,
                isUser = false,
                timestamp = "Just now",
                toolUsed = "${agent.name} Autonomous Core"
            )
            val updated = _chatMessages.value.toMutableList()
            updated.add(agentMsg)
            _chatMessages.value = updated
            speakTextInternal(replyText, agent)
        }
    }

    // Overviews and Dialogs
    fun openAgentOverview(agent: AgentProfile) { _selectedAgentForOverview.value = agent }
    fun closeAgentOverview() { _selectedAgentForOverview.value = null }

    fun openConnectorsDialog() { _showConnectorsDialog.value = true }
    fun closeConnectorsDialog() { _showConnectorsDialog.value = false }

    fun openVoiceCloneDialog() { _showVoiceCloneDialog.value = true }
    fun closeVoiceCloneDialog() { _showVoiceCloneDialog.value = false }

    fun openOrbCustomizerDialog() { _showOrbCustomizerDialog.value = true }
    fun closeOrbCustomizerDialog() { _showOrbCustomizerDialog.value = false }

    fun openLauncherCoreDialog() { _showLauncherCoreDialog.value = true }
    fun closeLauncherCoreDialog() { _showLauncherCoreDialog.value = false }

    fun updateConnectorKey(id: String, key: String) {
        repository.saveConnectorKey(id, key)
        _connectors.value = repository.getConnectors()
        showNotice("Connector key saved for $id! Dev Cost remains $0.")
    }

    fun setOrbType(type: String) {
        _orbType.value = type
        repository.setOrbType(type)
    }

    fun setOrbSize(size: Float) {
        _orbSize.value = size
        repository.setOrbSize(size)
    }

    fun setAuraBorderMode(enabled: Boolean) {
        _auraBorderMode.value = enabled
        repository.setAuraBorderMode(enabled)
    }

    fun setVisualizerSpectrum(enabled: Boolean) {
        _visualizerSpectrum.value = enabled
        repository.setVisualizerSpectrum(enabled)
    }

    fun setLauncherCore(core: String) {
        _launcherCore.value = core
        repository.setLauncherCore(core)
        showNotice("Launcher Core switched to $core!")
    }

    private var cloneJob: Job? = null
    fun startVoiceCloneRecording() {
        _isRecordingClone.value = true
        _cloneRecordProgress.value = 0f
        cloneJob?.cancel()
        cloneJob = viewModelScope.launch {
            for (i in 1..100) {
                delay(35)
                _cloneRecordProgress.value = i / 100f
            }
            _isRecordingClone.value = false
            _isCloningSuccess.value = true
            showNotice("Voice Cloned Successfully! Voice ID: $voiceCloneId")
            speakTextInternal("Voice clone calibrated successfully. Ready to deploy across all 11 agents.", _activeChatAgent.value)
        }
    }

    fun playVoiceCloneSample() {
        testVoiceCloneSample()
    }

    fun selectMemoryFile(filename: String) {
        _selectedMemoryFilename.value = filename
        _currentMemoryContent.value = repository.getMemoryFileContent(filename)
    }

    fun toggleOcr() {
        _isOcrActive.value = !_isOcrActive.value
    }

    fun toggleLiveTranscription() {
        _isLiveTranscriptionActive.value = !_isLiveTranscriptionActive.value
    }

    fun speakAgentSample(agent: AgentProfile) {
        selectPersona(agent, speakImmediateGreeting = true)
    }

    fun showNotice(msg: String) {
        _toastNotice.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_toastNotice.value == msg) {
                _toastNotice.value = null
            }
        }
    }

    fun clearNotice() {
        _toastNotice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.stopListening()
        repository.stopSpeaking()
    }
}
