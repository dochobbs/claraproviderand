package com.clara.provider.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clara.provider.models.ChildProfile

/**
 * Main app entry point for navigation
 * Sets up navigation between screens
 */
@Composable
fun MainApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "conversation_list"
    ) {
        // Conversation list screen
        composable("conversation_list") {
            ConversationListScreen(
                onConversationSelect = { conversationId ->
                    navController.navigate("conversation_detail/$conversationId")
                }
            )
        }

        // Conversation detail screen
        composable(
            route = "conversation_detail/{conversationId}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ConversationDetailScreen(
                conversationId = conversationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Patient profile screen
        // Note: In a real app, this would receive patient data through a different mechanism
        // For now, we'll create a minimal profile for demonstration
        composable(
            route = "patient_profile/{patientName}",
            arguments = listOf(
                navArgument("patientName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val patientName = backStackEntry.arguments?.getString("patientName") ?: "Unknown"
            PatientProfileScreen(
                patient = ChildProfile(
                    name = patientName,
                    dateOfBirth = "2020-01-01",
                    age = 4,
                    medications = listOf("Acetaminophen as needed"),
                    allergies = listOf("Penicillin"),
                    pastMedicalHistory = listOf("Ear infections (recurring)", "RSV bronchiolitis"),
                    clinicalNotes = "Generally healthy, good appetite and sleep patterns."
                ),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
