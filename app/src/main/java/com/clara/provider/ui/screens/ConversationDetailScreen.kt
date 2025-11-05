package com.clara.provider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clara.provider.models.ProviderReviewRequestDetail
import com.clara.provider.store.ProviderConversationStore
import com.clara.provider.ui.components.MessageBubble
import com.clara.provider.ui.components.getTriageColor
import com.clara.provider.ui.components.triageOutcomeLabel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

/**
 * Conversation detail screen
 * Shows full conversation history and allows provider to respond
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationDetailScreen(
    conversationId: String,
    store: ProviderConversationStore = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val conversationCache by store.conversationDetailsCache.collectAsState()
    val isLoading by store.isLoading.collectAsState()
    val error by store.error.collectAsState()

    val conversation = conversationCache[conversationId]
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var responseText by remember { mutableStateOf("") }
    var responseType by remember { mutableStateOf("agree") }
    var showResponseOptions by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(conversation?.conversationMessages?.size) {
        if (conversation?.conversationMessages?.isNotEmpty() == true) {
            scope.launch {
                listState.animateScrollToItem(conversation.conversationMessages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = conversation?.conversationTitle ?: "Loading...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (conversation != null) {
                        Text(
                            text = "${conversation.childName}, ${conversation.childAge}y",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (conversation?.status == "pending") {
                    IconButton(onClick = {
                        store.flagReview(conversation.id)
                    }) {
                        Icon(Icons.Filled.Flag, contentDescription = "Flag review")
                    }
                }
            }
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                isLoading && conversation == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                conversation == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Conversation not found")
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Triage outcome card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = getTriageColor(conversation.triageOutcome).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Triage Outcome",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = triageOutcomeLabel(conversation.triageOutcome),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = getTriageColor(conversation.triageOutcome)
                                    )
                                }
                                Text(
                                    text = conversation.status.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (conversation.status) {
                                        "pending" -> MaterialTheme.colorScheme.error
                                        "responded" -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        // Messages list
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                        ) {
                            items(
                                items = conversation.conversationMessages,
                                key = { it.id }
                            ) { message ->
                                MessageBubble(message = message)
                            }
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        // Response input
                        if (conversation.status == "pending") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Response type selector
                                if (showResponseOptions) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val responseOptions = listOf(
                                            "agree" to "Agree with Triage",
                                            "agree_with_thoughts" to "Agree with Thoughts",
                                            "disagree_with_thoughts" to "Disagree with Thoughts",
                                            "escalation" to "Escalation Required"
                                        )

                                        responseOptions.forEach { (type, label) ->
                                            OutlinedButton(
                                                onClick = {
                                                    responseType = type
                                                    showResponseOptions = false
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(label)
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { showResponseOptions = !showResponseOptions },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Response Type: $responseType")
                                    }
                                }

                                // Text input
                                OutlinedTextField(
                                    value = responseText,
                                    onValueChange = { responseText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Type your response...") },
                                    minLines = 3,
                                    maxLines = 5,
                                    enabled = !isSending,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                // Submit button
                                Button(
                                    onClick = {
                                        if (responseText.isNotBlank()) {
                                            isSending = true
                                            store.submitProviderResponse(
                                                reviewRequestId = conversation.id,
                                                responseType = responseType,
                                                content = responseText
                                            )
                                            responseText = ""
                                            isSending = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isSending && responseText.isNotBlank()
                                ) {
                                    if (isSending) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(end = 8.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                    Icon(
                                        Icons.Filled.Send,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text("Send Response")
                                }
                            }
                        } else {
                            // Responded state
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Response submitted on ${conversation.respondedAt ?: "Unknown date"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Error state
        if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp)
            ) {
                Text(
                    text = error ?: "Unknown error",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
