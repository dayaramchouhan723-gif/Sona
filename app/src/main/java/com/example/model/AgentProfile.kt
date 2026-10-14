package com.example.model

data class AgentProfile(
    val id: String,
    val name: String,
    val tagline: String,
    val colorHex: String,
    val secondaryColorHex: String,
    val toolsCount: Int,
    val memoryFile: String,
    val voiceName: String,
    val personality: String,
    val description: String,
    val features: List<String>,
    val frontendUiTheme: String,
    val backendLogic: String,
    val wakeWord: String,
    val isMaster: Boolean = false,
    val hasRealAvatar: Boolean = false,
    val badge: String? = null,
    val voiceCloneId: String? = null,
    val chips: List<String> = listOf("Music", "Study", "Journal"),
    val weatherTemp: String = "29°C",
    val weatherCondition: String = "Partly cloudy",
    val weatherHumidity: String = "70%",
    val todayDate: String = "Mon 5 Oct",
    val todayMood: String = "Happy Upbeat",
    val emotionQuote: String? = null
)

data class ConnectorItem(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val isConnected: Boolean,
    val apiKey: String = ""
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val toolUsed: String? = null
)
