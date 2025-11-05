package com.clara.provider.services

import android.content.Context
import com.clara.provider.models.ProviderReviewRequestDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

/**
 * Supabase service for provider-specific API calls
 * Mirrors iOS ProviderSupabaseService
 */
class ProviderSupabaseService(context: Context) : SupabaseServiceBase(context) {

    private val gson = Gson()

    /**
     * Fetch all review requests with optional status filter
     */
    suspend fun fetchReviewRequests(
        status: String? = null
    ): Result<List<ProviderReviewRequestDetail>> {
        val endpoint = buildString {
            append("provider_review_requests?order=created_at.desc")
            if (status != null) {
                append("&status=eq.$status")
            }
        }

        return makeGetRequest(endpoint).mapCatching { responseBody ->
            val type = object : TypeToken<List<ProviderReviewRequestDetail>>() {}.type
            gson.fromJson(responseBody, type)
        }
    }

    /**
     * Fetch pending review requests
     */
    suspend fun fetchPendingReviews(): Result<List<ProviderReviewRequestDetail>> {
        return fetchReviewRequests(status = "pending")
    }

    /**
     * Fetch detail for a specific conversation
     * Includes retry logic for transient failures
     */
    suspend fun fetchConversationDetail(
        conversationId: String,
        retryCount: Int = 3
    ): Result<ProviderReviewRequestDetail> {
        val endpoint = "provider_review_requests?conversation_id=eq.$conversationId&limit=1"

        var lastException: Exception? = null
        repeat(retryCount) {
            makeGetRequest(endpoint).onSuccess { responseBody ->
                return try {
                    val type = object : TypeToken<List<ProviderReviewRequestDetail>>() {}.type
                    val requests: List<ProviderReviewRequestDetail> = gson.fromJson(responseBody, type)
                    if (requests.isNotEmpty()) {
                        return Result.success(requests.first())
                    } else {
                        Result.failure(Exception("Conversation not found"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }.onFailure {
                lastException = it as Exception
            }
        }

        return Result.failure(lastException ?: Exception("Failed after $retryCount retries"))
    }

    /**
     * Submit provider response to a review request
     */
    suspend fun submitProviderResponse(
        reviewRequestId: String,
        responseType: String,
        content: String,
        urgencyLevel: String? = null
    ): Result<ProviderReviewRequestDetail> {
        val requestBody = JSONObject().apply {
            put("review_request_id", reviewRequestId)
            put("response_type", responseType)
            put("content", content)
            if (urgencyLevel != null) {
                put("urgency_level", urgencyLevel)
            }
            put("status", "responded")
            put("responded_at", System.currentTimeMillis())
        }

        val endpoint = "provider_review_requests?id=eq.$reviewRequestId"

        return makePatchRequest(endpoint, requestBody).mapCatching { responseBody ->
            val type = object : TypeToken<List<ProviderReviewRequestDetail>>() {}.type
            val requests: List<ProviderReviewRequestDetail> = gson.fromJson(responseBody, type)
            if (requests.isNotEmpty()) requests.first() else throw Exception("Response not saved")
        }
    }

    /**
     * Update review request status
     */
    suspend fun updateReviewStatus(
        reviewRequestId: String,
        newStatus: String
    ): Result<Unit> {
        val requestBody = JSONObject().apply {
            put("status", newStatus)
        }

        val endpoint = "provider_review_requests?id=eq.$reviewRequestId"

        return makePatchRequest(endpoint, requestBody).map { }
    }

    /**
     * Flag a review request for escalation
     */
    suspend fun flagReview(
        reviewRequestId: String,
        reason: String? = null
    ): Result<Unit> {
        val requestBody = JSONObject().apply {
            put("status", "flagged")
            if (reason != null) {
                put("flag_reason", reason)
            }
        }

        val endpoint = "provider_review_requests?id=eq.$reviewRequestId"

        return makePatchRequest(endpoint, requestBody).map { }
    }

    /**
     * Search reviews by title or patient name
     */
    suspend fun searchReviews(query: String): Result<List<ProviderReviewRequestDetail>> {
        // Supabase full-text search would be done here
        // Fallback: fetch all and filter client-side
        return fetchReviewRequests().map { reviews ->
            reviews.filter { review ->
                review.conversationTitle.contains(query, ignoreCase = true) ||
                review.childName.contains(query, ignoreCase = true)
            }
        }
    }

    /**
     * Fetch patient list
     */
    suspend fun fetchPatientList(): Result<List<Map<String, Any>>> {
        return makeGetRequest("patients?order=name.asc").mapCatching { responseBody ->
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(responseBody, type)
        }
    }
}
