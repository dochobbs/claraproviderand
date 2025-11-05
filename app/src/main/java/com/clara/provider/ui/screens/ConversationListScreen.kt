package com.clara.provider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clara.provider.models.ProviderReviewRequestDetail
import com.clara.provider.store.ProviderConversationStore
import com.clara.provider.ui.components.ConversationItem
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Main conversation list screen
 * Displays all review requests with filtering and search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    store: ProviderConversationStore = viewModel(),
    onConversationSelect: (String) -> Unit = {}
) {
    val reviewRequests by store.reviewRequests.collectAsState()
    val isLoading by store.isLoading.collectAsState()
    val error by store.error.collectAsState()
    val searchQuery by store.searchQuery.collectAsState()
    val badgeCount by store.badgeCount.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchActive by remember { mutableStateOf(false) }

    val tabs = listOf("All", "Pending", "Responded", "Flagged")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Clara Provider",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (badgeCount > 0) {
                    Text(
                        text = "$badgeCount pending reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Search Bar
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            query = searchQuery,
            onQueryChange = { store.searchConversations(it) },
            onSearch = { store.searchConversations(it) },
            active = searchActive,
            onActiveChange = { searchActive = it },
            placeholder = { Text("Search by title or patient name") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchActive) {
                    IconButton(onClick = {
                        store.searchConversations("")
                        searchActive = false
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            },
            content = { }
        )

        // Status Filter Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        val status = when (index) {
                            0 -> null
                            1 -> "pending"
                            2 -> "responded"
                            3 -> "flagged"
                            else -> null
                        }
                        store.setStatusFilter(status)
                    }
                )
            }
        }

        // Error State
        if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Error Loading Reviews",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = error ?: "Unknown error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                isLoading && reviewRequests.isEmpty() -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                "Loading reviews...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                reviewRequests.isEmpty() -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                "No reviews found",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                when (selectedTabIndex) {
                                    1 -> "No pending reviews at this time"
                                    2 -> "No responded reviews yet"
                                    3 -> "No flagged reviews"
                                    else -> "No reviews to display"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                else -> {
                    // Review list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = reviewRequests,
                            key = { it.id }
                        ) { review ->
                            ConversationItem(
                                review = review,
                                onItemClick = { conversationId ->
                                    store.loadConversationDetail(conversationId)
                                    onConversationSelect(conversationId)
                                }
                            )
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

