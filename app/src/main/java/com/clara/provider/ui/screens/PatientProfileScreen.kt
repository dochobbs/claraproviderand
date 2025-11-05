package com.clara.provider.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clara.provider.models.ChildProfile

/**
 * Patient profile screen
 * Displays comprehensive patient medical information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patient: ChildProfile,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = "Patient Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Demographics card
            PatientInfoCard(title = "Demographics") {
                InfoRow(label = "Name", value = patient.name)
                InfoRow(label = "Age", value = "${calculateAge(patient.dateOfBirth)} years")
                InfoRow(label = "Date of Birth", value = patient.dateOfBirth)
            }

            // Medications card
            if (patient.medications.isNotEmpty()) {
                PatientInfoCard(title = "Current Medications") {
                    patient.medications.forEach { medication ->
                        MedicationItem(medication)
                    }
                }
            } else {
                PatientInfoCard(title = "Current Medications") {
                    Text(
                        text = "No medications listed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Allergies card
            if (patient.allergies.isNotEmpty()) {
                PatientInfoCard(title = "Known Allergies", isAlert = true) {
                    patient.allergies.forEach { allergy ->
                        AllergyItem(allergy)
                    }
                }
            } else {
                PatientInfoCard(title = "Known Allergies") {
                    Text(
                        text = "No allergies listed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Past medical history card
            if (patient.pastMedicalHistory.isNotEmpty()) {
                PatientInfoCard(title = "Past Medical History") {
                    patient.pastMedicalHistory.forEach { history ->
                        HistoryItem(history)
                    }
                }
            } else {
                PatientInfoCard(title = "Past Medical History") {
                    Text(
                        text = "No medical history listed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Clinical notes card
            if (!patient.clinicalNotes.isNullOrBlank()) {
                PatientInfoCard(title = "Clinical Notes") {
                    Text(
                        text = patient.clinicalNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Metadata footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Created: ${formatDate(patient.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Updated: ${formatDate(patient.updatedAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Reusable card for patient information sections
 */
@Composable
fun PatientInfoCard(
    title: String,
    isAlert: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isAlert)
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            content()
        }
    }
}

/**
 * Information row component
 */
@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Medication item component
 */
@Composable
fun MedicationItem(medication: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(6.dp)
                .align(Alignment.Top)
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = medication,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * Allergy item component
 */
@Composable
fun AllergyItem(allergy: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp)
        ) {
            Text(
                text = "!",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onError,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = allergy,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Medical history item component
 */
@Composable
fun HistoryItem(history: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(6.dp)
                .align(Alignment.Top)
        ) {
            Text(
                text = "→",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = history,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * Helper functions
 */

fun calculateAge(dateOfBirth: String): Int {
    // Simple calculation - in production would use proper date parsing
    return try {
        val year = dateOfBirth.substring(0, 4).toInt()
        java.time.LocalDate.now().year - year
    } catch (e: Exception) {
        0
    }
}

fun formatDate(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        parts[0]
    } catch (e: Exception) {
        "Unknown"
    }
}
