package com.clara.provider.models

import java.util.UUID

/**
 * Core model for provider review requests
 * Mirrors iOS ProviderReviewRequestDetail
 */
data class ProviderReviewRequestDetail(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val conversationTitle: String,
    val childName: String,
    val childAge: Int,
    val childDOB: String,
    val triageOutcome: String, // er_911, er_drive, urgent_visit, routine_visit, home_care
    val status: String, // pending, responded, flagged, escalated
    val conversationMessages: List<Message> = emptyList(),
    val providerResponse: ProviderResponse? = null,
    val respondedAt: String? = null,
    val createdAt: String,
    val updatedAt: String
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: String,
    val triageOutcome: String? = null,
    val providerName: String? = null
)

data class ProviderResponse(
    val id: String = UUID.randomUUID().toString(),
    val responseType: String, // "agree", "agree_with_thoughts", "disagree_with_thoughts", "escalation"
    val content: String,
    val urgencyLevel: String? = null, // "low", "medium", "high"
    val createdAt: String,
    val updatedAt: String
)

data class ChildProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dateOfBirth: String,
    val age: Int,
    val medications: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val pastMedicalHistory: List<String> = emptyList(),
    val clinicalNotes: String? = null,
    val createdAt: String,
    val updatedAt: String
)

// Status enum for type safety
enum class ReviewStatus(val value: String) {
    PENDING("pending"),
    RESPONDED("responded"),
    FLAGGED("flagged"),
    ESCALATED("escalated");

    companion object {
        fun fromString(value: String): ReviewStatus {
            return entries.firstOrNull { it.value == value } ?: PENDING
        }
    }
}

// Triage outcome enum
enum class TriageOutcome(val value: String) {
    ER_911("er_911"),
    ER_DRIVE("er_drive"),
    URGENT_VISIT("urgent_visit"),
    ROUTINE_VISIT("routine_visit"),
    HOME_CARE("home_care");

    companion object {
        fun fromString(value: String): TriageOutcome {
            return entries.firstOrNull { it.value == value } ?: HOME_CARE
        }
    }
}
