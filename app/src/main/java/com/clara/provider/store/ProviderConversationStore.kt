package com.clara.provider.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clara.provider.models.ProviderReviewRequestDetail
import com.clara.provider.services.ProviderSupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Central state management for provider conversations
 * Mirrors iOS ProviderConversationStore
 * Uses ViewModel + StateFlow for reactive updates
 */
class ProviderConversationStore(
    private val supabaseService: ProviderSupabaseService
) : ViewModel() {

    // Review requests state
    private val _reviewRequests = MutableStateFlow<List<ProviderReviewRequestDetail>>(emptyList())
    val reviewRequests: StateFlow<List<ProviderReviewRequestDetail>> = _reviewRequests.asStateFlow()

    // Selected status filter
    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    // Conversation detail cache
    private val _conversationDetailsCache = MutableStateFlow<Map<String, ProviderReviewRequestDetail>>(emptyMap())
    val conversationDetailsCache: StateFlow<Map<String, ProviderReviewRequestDetail>> = _conversationDetailsCache.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Badge count (pending reviews)
    private val _badgeCount = MutableStateFlow(0)
    val badgeCount: StateFlow<Int> = _badgeCount.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadReviewRequests()
        startAutoRefresh()
    }

    /**
     * Load review requests from API
     */
    fun loadReviewRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val status = _selectedStatus.value
            val result = if (status != null) {
                supabaseService.fetchReviewRequests(status)
            } else {
                supabaseService.fetchReviewRequests()
            }

            result
                .onSuccess { reviews ->
                    _reviewRequests.value = reviews
                    updateBadgeCount()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load review requests"
                }

            _isLoading.value = false
        }
    }

    /**
     * Load conversation detail with caching
     */
    fun loadConversationDetail(conversationId: String) {
        // Check cache first
        _conversationDetailsCache.value[conversationId]?.let { cached ->
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            supabaseService.fetchConversationDetail(conversationId)
                .onSuccess { detail ->
                    val updatedCache = _conversationDetailsCache.value.toMutableMap()
                    updatedCache[conversationId] = detail
                    _conversationDetailsCache.value = updatedCache
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load conversation"
                }

            _isLoading.value = false
        }
    }

    /**
     * Submit provider response
     */
    fun submitProviderResponse(
        reviewRequestId: String,
        responseType: String,
        content: String,
        urgencyLevel: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            supabaseService.submitProviderResponse(
                reviewRequestId,
                responseType,
                content,
                urgencyLevel
            )
                .onSuccess { updatedRequest ->
                    // Update in cache
                    val updatedCache = _conversationDetailsCache.value.toMutableMap()
                    updatedCache[updatedRequest.conversationId] = updatedRequest

                    // Update in list
                    val updatedList = _reviewRequests.value.map {
                        if (it.id == reviewRequestId) updatedRequest else it
                    }

                    _conversationDetailsCache.value = updatedCache
                    _reviewRequests.value = updatedList
                    _error.value = null
                    updateBadgeCount()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to submit response"
                }

            _isLoading.value = false
        }
    }

    /**
     * Update status filter
     */
    fun setStatusFilter(status: String?) {
        _selectedStatus.value = status
        loadReviewRequests()
    }

    /**
     * Search conversations
     */
    fun searchConversations(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadReviewRequests()
                return@launch
            }

            _isLoading.value = true

            supabaseService.searchReviews(query)
                .onSuccess { results ->
                    _reviewRequests.value = results
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Search failed"
                }

            _isLoading.value = false
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Update badge count based on pending reviews
     */
    private fun updateBadgeCount() {
        _badgeCount.value = _reviewRequests.value.count { it.status == "pending" }
    }

    /**
     * Auto-refresh every 60 seconds (mirrors iOS 60s timer)
     */
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60.seconds.inWholeMilliseconds)
                loadReviewRequests()
            }
        }
    }
}
