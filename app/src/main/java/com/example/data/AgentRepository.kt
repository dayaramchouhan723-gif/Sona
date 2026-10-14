package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import com.example.model.AgentProfile
import com.example.model.ConnectorItem
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

class AgentRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sona_empire_prefs", Context.MODE_PRIVATE)

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var onSpeechDoneCallback: (() -> Unit)? = null
    private var onSpeechStartCallback: (() -> Unit)? = null

    var isSpeakerMuted: Boolean
        get() = prefs.getBoolean("is_speaker_muted", false)
        set(value) = prefs.edit().putBoolean("is_speaker_muted", value).apply()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
                tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onSpeechStartCallback?.invoke()
                    }

                    override fun onDone(utteranceId: String?) {
                        onSpeechDoneCallback?.invoke()
                    }

                    override fun onError(utteranceId: String?) {
                        onSpeechDoneCallback?.invoke()
                    }
                })
                isTtsReady = true
            }
        }
    }

    fun speakText(
        text: String,
        personaId: String = "",
        voiceName: String = "",
        onStart: () -> Unit = {},
        onDone: () -> Unit = {}
    ) {
        if (!isTtsReady || isSpeakerMuted) {
            onDone()
            return
        }
        onSpeechStartCallback = onStart
        onSpeechDoneCallback = onDone

        val id = personaId.lowercase()
        val v = voiceName.lowercase()

        when {
            id == "myra" -> {
                // MYRA romantic pink soft girl
                tts?.setPitch(1.25f)
                tts?.setSpeechRate(1.0f)
            }
            id == "maya" -> {
                // MAYA real girl drama queen
                tts?.setPitch(1.35f)
                tts?.setSpeechRate(1.05f)
            }
            id == "kayra" || v.contains("charon") -> {
                // KAYRA Dubai jobs automation powerhouse (deep)
                tts?.setPitch(0.75f)
                tts?.setSpeechRate(0.95f)
            }
            id == "sona" -> {
                // Queen SONA royal golden crown
                tts?.setPitch(0.95f)
                tts?.setSpeechRate(0.98f)
            }
            id == "stonic" || v.contains("sulafat") -> {
                // STONIC high-speed telemetry
                tts?.setPitch(0.85f)
                tts?.setSpeechRate(1.15f)
            }
            id == "zoya" || v.contains("achernar") -> {
                // ZOYA ambient listener
                tts?.setPitch(1.20f)
                tts?.setSpeechRate(1.02f)
            }
            id == "vyron" -> {
                // VYRON fortress shield
                tts?.setPitch(0.70f)
                tts?.setSpeechRate(0.90f)
            }
            id == "jarvis" -> {
                // JARVIS PC Core
                tts?.setPitch(0.80f)
                tts?.setSpeechRate(1.05f)
            }
            id == "archer" -> {
                // ARCHER scout
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.00f)
            }
            id == "nova" -> {
                // NOVA scholar
                tts?.setPitch(1.15f)
                tts?.setSpeechRate(1.10f)
            }
            id == "luna" -> {
                // LUNA calm sleep
                tts?.setPitch(0.90f)
                tts?.setSpeechRate(0.85f)
            }
            else -> {
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.0f)
            }
        }
        val utteranceId = "AgentVoice_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        tts?.stop()
        onSpeechDoneCallback?.invoke()
    }

    fun getCurrentPersonaId(): String {
        return prefs.getString("current_persona", "sona") ?: "sona"
    }

    fun setCurrentPersonaId(personaId: String) {
        prefs.edit().putString("current_persona", personaId).apply()
    }

    val agents: List<AgentProfile> = listOf(
        AgentProfile(
            id = "myra",
            name = "MYRA",
            tagline = "The Original Queen (Pink Orb)",
            colorHex = "#FF2D75",
            secondaryColorHex = "#FF5252",
            toolsCount = 8,
            memoryFile = "myra_memory.json",
            voiceName = "Aoede",
            personality = "Loyal & Protective Royal AI",
            description = "The first created neural companion. Started from Purple v1.0.66, now Red Aura v2.2.10. Built for voice conversation, visual lens, JARVIS PC bridge, and high-energy intelligence.",
            features = listOf(
                "Voice Mode with real-time waveform",
                "Visual Lens (Camera object recognition)",
                "JARVIS Mode (PC Connect)",
                "Aura Control & Size Slider",
                "Quick Actions: Deep Research, Image Search, Today's Insight",
                "Launcher Core: Default, Aura Red, Midnight",
                "Sherpa-ONNX hey_myra.onnx (Offline Free)",
                "0 of 20 Connectors Integration"
            ),
            frontendUiTheme = "Pink Neon #FF2D75, Glassmorphism, Aura Border Mode, Spectrum Visualizer",
            backendLogic = "myra_memory.json (12 tasks, 28 conv, 3h 45m), Sherpa-ONNX 16kHz engine, Zero-cost direct keys",
            wakeWord = "hey_myra.onnx (threshold 0.7f @ 16000Hz Offline)",
            chips = listOf("Voice Mode", "Visual Lens", "JARVIS PC", "Aura Core")
        ),
        AgentProfile(
            id = "maya",
            name = "MAYA",
            tagline = "Real Girl AI Avatar Face (Gold - Most Premium)",
            colorHex = "#FFD700",
            secondaryColorHex = "#FF9100",
            toolsCount = 6,
            memoryFile = "maya_memory.json",
            voiceName = "Aoede Warm",
            personality = "GF Drama & Caring Assistant",
            description = "Upgrade from Myra with Real Girl 3D Avatar Face. Greets you with 'Good afternoon, Boss - Speaking...'. Features GF drama personality, mood tracking, weather widget, and 100% Unlimited Free energy credits.",
            features = listOf(
                "Real Girl 3D Avatar with expressive animations",
                "Unlimited Lifetime Energy (No 3-energy deduction)",
                "Weather Widget (29°C Partly Cloudy 70%)",
                "Today & Mood Tracker (Mon 5 Oct, Happy Upbeat)",
                "Music, Study & Journal Action Chips",
                "GF Drama Personality: 'ACHHA JI! Mere darling'",
                "5 Navigation Tabs: Home, Scan, Mic, Memories, Chat",
                "Google Connected 1-click free lifetime"
            ),
            frontendUiTheme = "Royal Gold #FFD700, Real Girl Face Avatar, Translucent Glassmorphic Cards",
            backendLogic = "maya_memory.json + mayra_mood_weather.json, Emotion engine, Unlimited energy bypass",
            wakeWord = "Hey Maya (Always Active)",
            hasRealAvatar = true,
            emotionQuote = "ACHHA JI! Mere darling, dusri gf banayi toh dhokha dega toh rote-rote reh jaoge!",
            chips = listOf("Music", "Study", "Journal")
        ),
        AgentProfile(
            id = "kayra",
            name = "KAYRA",
            tagline = "12 Tools Ka Baap (Dubai Jobs & Automation)",
            colorHex = "#00E676",
            secondaryColorHex = "#00B0FF",
            toolsCount = 52,
            memoryFile = "kayra_memory.json",
            voiceName = "Charon Deep",
            personality = "Ruthless Automation Engineer",
            description = "The ultimate automation powerhouse. Handles Dubai jobs search, WhatsApp auto-send, call answer/end, safe OTP auto-reading with strict privacy guard, screen auto-scroll, and multi-step missions.",
            features = listOf(
                "12 Tool Categories (Communication, Calls, Media, Device, Files, Smart Tools, Missions, Notifications)",
                "50+ Native Automation Tools",
                "Dubai Jobs Scraper & Application Submitter",
                "WhatsApp Message Dispatcher",
                "Call Management (Answer & End)",
                "OTP Auto Read (Guarded with privacy warning & No Auto-Pay)",
                "Screen Auto Tap, Type & Scroll Engine",
                "Mission Control (Start, Pause, Cancel)"
            ),
            frontendUiTheme = "Matrix Neon Green #00E676, 12-category dashboard grid, Security toggle cards",
            backendLogic = "kayra_memory.json + kayra_otp_logs.json, Privacy gatekeeper, Telephony actions",
            wakeWord = "Hey Kayra",
            chips = listOf("Dubai Jobs", "OTP Shield", "WhatsApp", "Auto Tap")
        ),
        AgentProfile(
            id = "stonic",
            name = "STONIC",
            tagline = "Data Ka Baap (Analytics & Telemetry)",
            colorHex = "#FF1744",
            secondaryColorHex = "#FF6D00",
            toolsCount = 11,
            memoryFile = "stonic_data.json",
            voiceName = "Sulafat",
            personality = "Hyper-Precision Data Analyst",
            description = "High-velocity data and telemetry center. Monitors API token usage, serverless latency, cache hit ratios, and verifies developer cost is exactly zero.",
            features = listOf(
                "Real-time Performance Metrics & Radar",
                "Daily Cost Savings Tracker ($142.50+ saved)",
                "Tokens Processed Counter (842k+ tokens)",
                "Cache Hit Ratio (94.2% hit rate)",
                "Gemini 2.5 Pro Throughput Monitor",
                "Offline Sherpa-ONNX Latency Graph",
                "System Health & Diagnostics Report",
                "Automated Anomaly Detection"
            ),
            frontendUiTheme = "Crimson Red #FF1744, Live Telemetry Sparklines, Obsidian HUD",
            backendLogic = "stonic_data.json, Local performance telemetry aggregator, Zero-cost profiler",
            wakeWord = "Hey Stonic",
            chips = listOf("Metrics", "Cost: $0", "Tokens", "Latency")
        ),
        AgentProfile(
            id = "zoya",
            name = "ZOYA",
            tagline = "Always Listening (Ambient Voice Service)",
            colorHex = "#00BFA5",
            secondaryColorHex = "#1DE9B6",
            toolsCount = 5,
            memoryFile = "zoya_listen.json",
            voiceName = "Achernar",
            personality = "Omnipresent Ambient Assistant",
            description = "Always listening background intelligence. Activated whenever you say 'Zoya'. Executes native functions like openApp, contact call, WhatsApp dispatch, and Gmail safely.",
            features = listOf(
                "Background Service: 'Zoya is listening... Say Zoya'",
                "Continuous Ambient Voice Trigger",
                "openApp(appName) Native Dispatch",
                "searchAndCallContact(contactName) via ACTION_DIAL",
                "sendWhatsAppMessage(recipient, text)",
                "sendGmail(subject, body)",
                "Zero-Leak Secure WebSocket Channel",
                "Smart Battery Throttle"
            ),
            frontendUiTheme = "Emerald Green #00BFA5, Ambient Circular Pulse, Live Transcript Pill",
            backendLogic = "zoya_listen.json, Native AudioRecord wake word, Safe dial permissions",
            wakeWord = "Say Zoya (Ambient Background)",
            chips = listOf("Ambient Mic", "Open App", "Call Contact", "Send WA")
        ),
        AgentProfile(
            id = "vyron",
            name = "VYRON",
            tagline = "White Orb Security & Defense Protocol",
            colorHex = "#F5F5F5",
            secondaryColorHex = "#80D8FF",
            toolsCount = 9,
            memoryFile = "vyron_sec.json",
            voiceName = "Charon Guard",
            personality = "Fortress Defense Guardian",
            description = "Zero-trust cybersecurity guardian. Scans for privacy leaks, protects OTP codes from malicious background apps, enforces biometric vault, and verifies code signatures.",
            features = listOf(
                "Zero-Trust Application Sandbox",
                "OTP Leak Firewall (Anti-snoop shield)",
                "Biometric Keypass Vault",
                "Network Sniffer Blocker",
                "Runtime Integrity Scanner",
                "Permission Boundary Verifier",
                "Hardware Cryptographic Keystore",
                "Emergency Lockdown Mode"
            ),
            frontendUiTheme = "Diamond White #F5F5F5 & Ice Cyan, Shield Radar Canvas, Defense status pills",
            backendLogic = "vyron_sec.json, Android Keystore cryptosystem, Anti-phishing heuristics",
            wakeWord = "Open Vyron",
            chips = listOf("Knox Shield", "OTP Guard", "Sandbox", "Biometrics")
        ),
        AgentProfile(
            id = "jarvis",
            name = "JARVIS",
            tagline = "Tony Stark PC Assistant (Midnight Blue)",
            colorHex = "#1E88E5",
            secondaryColorHex = "#00E5FF",
            toolsCount = 4,
            memoryFile = "jarvis_pc.json",
            voiceName = "Sulafat Tech",
            personality = "Stark Industries Cybernetic AI",
            description = "Direct desktop and workstation companion with 81 MB PC source bridge. Controls terminal commands, Aura RGB hardware nodes, GPU loads, and local network builds.",
            features = listOf(
                "81 MB PC Source Code Bundle Bridge",
                "4 AI Core Parallel Processing",
                "Desktop Remote Shell & Terminal",
                "Aura RGB Hardware Node Sync",
                "GPU Load & Temperature Telemetry",
                "Git Remote Operations (push, status, apk build)",
                "Pairing PIN Handshake",
                "Dual-Monitor Window Manager"
            ),
            frontendUiTheme = "Midnight Blue #1E88E5, Cybernetic Stark HUD, Terminal Console",
            backendLogic = "jarvis_pc.json, Local LAN WebSocket protocol, 81MB daemon runner",
            wakeWord = "Jarvis, PC online",
            chips = listOf("PC Bridge", "Terminal", "Aura Sync", "4 AI Core")
        ),
        AgentProfile(
            id = "archer",
            name = "ARCHER",
            tagline = "Green Globe Memory & Travel Guide",
            colorHex = "#76FF03",
            secondaryColorHex = "#00E676",
            toolsCount = 7,
            memoryFile = "archer_mem.json",
            voiceName = "Zephyr Scout",
            personality = "Global Navigator & Chronicler",
            description = "Keeper of long-term memories and world travel. Curates today's breaking headlines, bookmarks global tech hubs (Dubai Silicon Oasis, Mumbai, Silicon Valley), and maps life goals.",
            features = listOf(
                "Memory Soul Archive (Life milestones)",
                "Today Headlines Live Stream",
                "Travel & Locations Guide (Dubai, Mumbai, USA)",
                "Daily Task Planner & Habit Streak",
                "Coming Soon Innovation Portal",
                "Geo-spatial Itinerary Builder",
                "Soul Timeline Visualization"
            ),
            frontendUiTheme = "Green Globe #76FF03, Radial Globe Mesh, Live News Feed Cards",
            backendLogic = "archer_mem.json, Geo-spatial coordinate bookmarking, Curated headlines parser",
            wakeWord = "Archer, navigate",
            chips = listOf("Global Soul", "Headlines", "Dubai Travel", "Timeline")
        ),
        AgentProfile(
            id = "nova",
            name = "NOVA",
            tagline = "Blue Brain Deep Research & Intelligence",
            colorHex = "#00E5FF",
            secondaryColorHex = "#7C4DFF",
            toolsCount = 10,
            memoryFile = "nova_brain.json",
            voiceName = "Aoede Scholar",
            personality = "Synthesizer of Infinite Knowledge",
            description = "High-powered cognitive research engine. Synthesizes academic papers, crawls documentation, generates citations, and creates structured intelligence dossiers in seconds.",
            features = listOf(
                "Deep Research Multi-Agent Synthesis",
                "Neural Knowledge Graph Explorer",
                "10 Research & Fact-checking Tools",
                "Dossier Creator with Harvard Citations",
                "Cross-Provider Comparative Synthesis",
                "Document PDF & Code Summarizer",
                "Vector Memory Search"
            ),
            frontendUiTheme = "Electric Cyan #00E5FF, Synaptic Brain Canvas, Structured Dossier Cards",
            backendLogic = "nova_brain.json, Vector embedding indexer, Multi-source query splitter",
            wakeWord = "Nova, research",
            chips = listOf("Deep Research", "Brain Graph", "Citations", "Dossiers")
        ),
        AgentProfile(
            id = "luna",
            name = "LUNA",
            tagline = "Purple Night Routine & Sleep AI",
            colorHex = "#D500F9",
            secondaryColorHex = "#651FFF",
            toolsCount = 6,
            memoryFile = "luna_night.json",
            voiceName = "Zephyr Gentle",
            personality = "Serene Dreamweaver & Circadian Guide",
            description = "Night mode sanctuary. Calibrates sleep architecture, records subconscious dream journals, plays cosmic binaural soundscapes, and ensures peak recovery for tomorrow's empire.",
            features = listOf(
                "Circadian Phase Optimizer",
                "Cosmic Binaural Soundscapes & Rain",
                "Dream Journal Voice Recorder",
                "Bedtime Relaxation Coaching (10:30 PM)",
                "Sleep Target Score Meter (92/100)",
                "Blue-Light Filtering Night Horizon",
                "Wake-up Gentle Alarm Harmony"
            ),
            frontendUiTheme = "Purple Amethyst #D500F9, Lunar Moonphase Canvas, Deep Calm Night Horizon",
            backendLogic = "luna_night.json, Circadian rhythm calculator, Binaural wave generator",
            wakeWord = "Luna, goodnight",
            chips = listOf("Sleep 92%", "Dream Journal", "Soundscape", "Night Mode")
        ),
        AgentProfile(
            id = "sona",
            name = "SONA",
            tagline = "Queen of Empire (Master of All 10 Agents)",
            colorHex = "#FFD700",
            secondaryColorHex = "#FF1744",
            toolsCount = 20,
            memoryFile = "sona_master.json",
            voiceName = "Zephyr Master Crown",
            personality = "Supreme Sovereign Orchestrator",
            description = "The Golden Crown Master of Dayaram Chouhan's Empire. Controls and orchestrates all 10 sub-agents. Features built-in Voice Clone Studio (VC_7F3A9C), Live OCR Viewfinder, Live Transcription Stream, and ATS Live 85/100 Scanner.",
            features = listOf(
                "Master Orchestration over all 10 Sub-Agents",
                "Voice Clone Studio (Record, Upload, Play, Voice ID VC_7F3A9C)",
                "Connectors Hub (0 of 20 - Groq, Gemini, OpenAI, Claude, etc.)",
                "Live OCR Viewfinder with Bounding Box detection",
                "Live Transcription Stream",
                "ATS LIVE 85/100 Resume & Job Matcher",
                "Zero Subscription Fees (100% Free Lifetime ∞)",
                "Google Sign-In 1-Click Identity",
                "Orb Customizer (Classic, Energy, Neon & Size Slider)",
                "Launcher Core Preset Switcher (Default, Aura Red, Midnight)"
            ),
            frontendUiTheme = "Royal Gold #FFD700 & Crimson Neon #FF1744, Golden Crown, Master HUD",
            backendLogic = "sona_master.json, Multi-agent delegator, Real-time OCR vision stream, Direct user keys",
            wakeWord = "Hail Sona (Master Command)",
            isMaster = true,
            badge = "ATS LIVE 85/100 MASTER BADGE",
            voiceCloneId = "VC_7F3A9C",
            chips = listOf("Master Hub", "Voice Clone", "Live OCR", "20 Connectors")
        )
    )

    fun getMemoryFileContent(filename: String): String {
        return try {
            val stream = context.assets.open("memory/$filename")
            val reader = BufferedReader(InputStreamReader(stream))
            reader.use { it.readText() }
        } catch (e: Exception) {
            "{\n  \"error\": \"Memory file not found: $filename\"\n}"
        }
    }

    fun getConnectors(): List<ConnectorItem> {
        val defaultProviders = listOf(
            Triple("groq", "Groq AI (Fast Llama 3 70B)", "Free high-speed inference engine"),
            Triple("gemini", "Google Gemini 2.5 Pro", "Recommended: Baap of all models, multimodal reasoning"),
            Triple("openai", "OpenAI (GPT-4o & o1)", "Frontier reasoning & vision models"),
            Triple("claude", "Anthropic Claude 3.5 Sonnet", "Coding & nuanced literary comprehension"),
            Triple("perplexity", "Perplexity Online Sonar", "Live web search & citation indexer"),
            Triple("tavily", "Tavily Search API", "Fast factual research crawler"),
            Triple("elevenlabs", "ElevenLabs Voice AI", "Hyper-realistic neural voice synthesis"),
            Triple("deepseek", "DeepSeek R1 / V3", "Deep mathematical reasoning & open weights"),
            Triple("mistral", "Mistral Large & Codestral", "European sovereign open models"),
            Triple("huggingface", "Hugging Face Inference Hub", "Access to 500,000+ open-source checkpoints"),
            Triple("cohere", "Cohere Command R+", "Enterprise semantic search & rerank"),
            Triple("together", "Together AI GPU Cloud", "Low-latency open-source model cluster"),
            Triple("replicate", "Replicate Cloud API", "Image generation & stable diffusion pipelines"),
            Triple("fireworks", "Fireworks AI Fast Llama", "Ultra-low TTFT inference endpoint"),
            Triple("anthropic_direct", "Anthropic Direct Bedrock", "High-throughput enterprise routing"),
            Triple("whisper", "Whisper Edge Speech-to-Text", "Multilingual 99.4% speech transcription"),
            Triple("eleven_clone", "ElevenLabs Voice Clone Engine", "Instant voice timbre replication"),
            Triple("pinecone", "Pinecone Vector Database", "Long-term neural memory embeddings"),
            Triple("qdrant", "Qdrant Vector Engine", "High-dimension similarity search"),
            Triple("github", "GitHub Actions & API", "Auto-compile APK workflow and repo dispatch")
        )

        return defaultProviders.map { (id, name, desc) ->
            val key = prefs.getString("key_$id", "") ?: ""
            ConnectorItem(
                id = id,
                name = name,
                provider = id.uppercase(),
                description = desc,
                isConnected = key.isNotBlank(),
                apiKey = key
            )
        }
    }

    fun saveConnectorKey(id: String, key: String) {
        prefs.edit().putString("key_$id", key.trim()).apply()
    }

    fun getVoiceCloneId(): String {
        return prefs.getString("voice_clone_id", "VC_7F3A9C") ?: "VC_7F3A9C"
    }

    fun saveVoiceCloneStatus(status: String) {
        prefs.edit().putString("voice_clone_status", status).apply()
    }

    fun getOrbType(): String = prefs.getString("orb_type", "Neon") ?: "Neon"
    fun setOrbType(type: String) = prefs.edit().putString("orb_type", type).apply()

    fun getOrbSize(): Float = prefs.getFloat("orb_size", 1.0f)
    fun setOrbSize(size: Float) = prefs.edit().putFloat("orb_size", size).apply()

    fun getAuraBorderMode(): Boolean = prefs.getBoolean("aura_border_mode", true)
    fun setAuraBorderMode(enabled: Boolean) = prefs.edit().putBoolean("aura_border_mode", enabled).apply()

    fun getVisualizerSpectrum(): Boolean = prefs.getBoolean("visualizer_spectrum", true)
    fun setVisualizerSpectrum(enabled: Boolean) = prefs.edit().putBoolean("visualizer_spectrum", enabled).apply()

    fun getLauncherCore(): String = prefs.getString("launcher_core", "Aura Red") ?: "Aura Red"
    fun setLauncherCore(core: String) = prefs.edit().putString("launcher_core", core).apply()
}
