package com.clara.provider.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
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
import com.clara.provider.models.ProviderReviewRequestDetail
import com.clara.provider.ui.theme.ClaraProviderAppTheme

/**
 * Individual conversation item in the list
 * Displays review request summary with status indicator
 */
@Composable
fun ConversationItem(
    review: ProviderReviewRequestDetail,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (review.status) {
        "pending" -> MaterialTheme.colorScheme.error
        "responded" -> MaterialTheme.colorScheme.primary
        "flagged" -> MaterialTheme.colorScheme.error
        "escalated" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val statusLabel = when (review.status) {
        "pending" -> "PENDING"
        "responded" -> "RESPONDED"
        "flagged" -> "FLAGGED"
        "escalated" -> "ESCALATED"
        else -> review.status.uppercase()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(review.conversationId) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: conversation details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title
            Text(
                text = review.conversationTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Patient info
            Text(
                text = "${review.childName}, ${review.childAge}y",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Triage outcome
            Text(
                text = triageOutcomeLabel(review.triageOutcome),
                style = MaterialTheme.typography.labelSmall,
                color = getTriageColor(review.triageOutcome),
                fontWeight = FontWeight.Medium
            )
        }

        // Right side: status badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status indicator
            Row(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (review.status == "flagged") {
                    Icon(
                        imageVector = Icons.Filled.Flag,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier
                    )
                }
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Get display label for triage outcome
 */
fun triageOutcomeLabel(outcome: String): String {
    return when (outcome) {
        "er_911" -> "ER - 911"
        "er_drive" -> "ER - Drive"
        "urgent_visit" -> "Urgent Visit"
        "routine_visit" -> "Routine Visit"
        "home_care" -> "Home Care"
        else -> outcome.replace("_", " ").uppercase()
    }
}

/**
 * Get color for triage outcome
 */
fun getTriageColor(outcome: String): Color {
    return when (outcome) {
        "er_911" -> Color(0xFFD32F2F)      // Red
        "er_drive" -> Color(0xFFF57C00)    // Orange
        "urgent_visit" -> Color(0xFFFBC02D) // Yellow
        "routine_visit" -> Color(0xFF388E3C) // Green
        "home_care" -> Color(0xFF1976D2)   // Blue
        else -> Color.Gray
    }
}

/**
 * Preview for ConversationItem
 */
@Preview(showBackground = true)
@Composable
fun ConversationItemPreview() {
    ClaraProviderAppTheme {
        ConversationItem(
            review = ProviderReviewRequestDetail(
                id = "1",
                conversationId = "conv-1",
                conversationTitle = "Persistent Cough in Toddler",
                childName = "Emma Johnson",
                childAge = 3,
                childDOB = "2021-05-15",
                triageOutcome = "er_drive",
                status = "pending",
                createdAt = "2024-11-05T10:00:00Z",
                updatedAt = "2024-11-05T10:00:00Z"
            ),
            onItemClick = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}
