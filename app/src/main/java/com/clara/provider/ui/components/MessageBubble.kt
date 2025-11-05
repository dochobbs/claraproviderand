package com.clara.provider.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clara.provider.models.Message
import com.clara.provider.ui.theme.ClaraProviderAppTheme

/**
 * Message bubble for conversation display
 */
@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isFromUser = message.isFromUser
    val backgroundColor = if (isFromUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isFromUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .fillMaxWidth(0.8f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Sender name (if available)
                if (!isFromUser && message.providerName != null) {
                    Text(
                        text = message.providerName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }

                // Message content
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                // Triage outcome badge (if present)
                if (message.triageOutcome != null) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = getTriageColor(message.triageOutcome).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = triageOutcomeLabel(message.triageOutcome),
                            style = MaterialTheme.typography.labelSmall,
                            color = getTriageColor(message.triageOutcome),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Timestamp
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Format timestamp for display
 */
fun formatTimestamp(timestamp: String): String {
    // Simple formatting - in production would use proper date formatting
    return try {
        val parts = timestamp.split("T")
        if (parts.size >= 2) {
            val timeParts = parts[1].split(":")
            if (timeParts.size >= 2) {
                "${timeParts[0]}:${timeParts[1]}"
            } else {
                "Time unknown"
            }
        } else {
            "Time unknown"
        }
    } catch (e: Exception) {
        "Time unknown"
    }
}

/**
 * Preview for MessageBubble
 */
@Preview(showBackground = true)
@Composable
fun MessageBubblePreview() {
    ClaraProviderAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // User message
            MessageBubble(
                message = Message(
                    id = "1",
                    content = "Hi, my child has been coughing for 3 days",
                    isFromUser = true,
                    timestamp = "2024-11-05T10:30:00Z"
                )
            )

            // Provider message
            MessageBubble(
                message = Message(
                    id = "2",
                    content = "Thank you for reaching out. Can you tell me more about the cough?",
                    isFromUser = false,
                    timestamp = "2024-11-05T10:35:00Z",
                    providerName = "Clara AI"
                )
            )

            // Message with triage outcome
            MessageBubble(
                message = Message(
                    id = "3",
                    content = "Based on the information provided, your child should visit an urgent care clinic.",
                    isFromUser = false,
                    timestamp = "2024-11-05T10:40:00Z",
                    triageOutcome = "urgent_visit",
                    providerName = "Clara AI"
                )
            )
        }
    }
}
