package com.example.flashcards

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen { cardset,cardsetID, isRandom, isLoopEnabled,typeorguess ->
                navController.navigate("questions/$cardset/$cardsetID?isRandom=$isRandom&isLoopEnabled=$isLoopEnabled&typeorguess=$typeorguess")
            }
        }
        composable(
            "questions/{cardset}/{cardsetID}?isRandom={isRandom}&isLoopEnabled={isLoopEnabled}&typeorguess={typeorguess}",
            arguments = listOf(
                navArgument("cardset") { type = NavType.StringType },
                navArgument("cardsetID") { type = NavType.IntType },
                navArgument("isRandom") { type = NavType.BoolType },
                navArgument("isLoopEnabled") { type = NavType.BoolType },
                navArgument("typeorguess") { type = NavType.StringType }

            )
        ) { backStackEntry ->
            val cardset = backStackEntry.arguments?.getString("cardset") ?: "Unknown"
            val cardsetID = backStackEntry.arguments?.getInt("cardsetID") ?: 86
            val isRandom = backStackEntry.arguments?.getBoolean("isRandom") ?: false
            val isLoopEnabled = backStackEntry.arguments?.getBoolean("isLoopEnabled") ?: false
            val typeorguess = backStackEntry.arguments?.getString("typeorguess") ?: "Type"
            QuestionsPage(cardset, cardsetID, isRandom, isLoopEnabled, navController,typeorguess)
        }
    }
}