package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ConnectorItem
import com.example.ui.theme.EmpireDarkBg
import com.example.ui.theme.EmpireDarkCard
import com.example.ui.theme.EmpireDarkCardBorder
import com.example.ui.theme.EmpireDarkSurface
import com.example.ui.theme.SonaGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectorsDialog(
    connectors: List<ConnectorItem>,
    onDismiss: () -> Unit,
    onSaveKey: (id: String, key: String) -> Unit
) {
    val connectedCount = connectors.count { it.isConnected }
    var selectedConnectorForEdit by remember { mutableStateOf<ConnectorItem?>(null) }
    var inputKey by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(640.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, SonaGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("connectors_dialog"),
            color = EmpireDarkSurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Connectors",
                                tint = SonaGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONNECTORS HUB",
                                color = SonaGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "$connectedCount of ${connectors.size} Connected • User Keys = Dev Cost $0",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Info banner explaining Free Lifetime policy
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmpireDarkCard, RoundedCornerShape(12.dp))
                        .border(1.dp, EmpireDarkCardBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "🔒 No payment required. Direct BYOK (Bring Your Own Key) architecture. Gemini 2.5 Pro & Groq free tiers work out-of-the-box. Keys stored securely in private SharedPreferences.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // If editing key
                if (selectedConnectorForEdit != null) {
                    val conn = selectedConnectorForEdit!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SonaGold, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Enter API Key for ${conn.name}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = inputKey,
                                onValueChange = { inputKey = it },
                                placeholder = { Text("Paste your API key here...", color = TextMuted) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("connector_api_key_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SonaGold,
                                    unfocusedBorderColor = EmpireDarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { selectedConnectorForEdit = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmpireDarkBg)
                                ) {
                                    Text("Cancel", color = TextSecondary)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onSaveKey(conn.id, inputKey)
                                        selectedConnectorForEdit = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SonaGold)
                                ) {
                                    Text("Save Key", color = EmpireDarkBg, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // List of 20 Connectors
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("connectors_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(connectors) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmpireDarkCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (item.isConnected) Color(0xFF00E676).copy(alpha = 0.5f) else EmpireDarkCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (item.isConnected) Icons.Default.CheckCircle else Icons.Default.Key,
                                            contentDescription = null,
                                            tint = if (item.isConnected) Color(0xFF00E676) else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.name,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedConnectorForEdit = item
                                        inputKey = item.apiKey
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (item.isConnected) Color(0xFF1E3A2B) else EmpireDarkBg
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = if (item.isConnected) "Configured" else "Connect",
                                        color = if (item.isConnected) Color(0xFF00E676) else SonaGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
