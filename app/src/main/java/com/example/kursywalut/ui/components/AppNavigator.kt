package com.example.kursywalut.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kursywalut.ui.CurrencyViewModel

@Composable
fun AppNavigation(vm: CurrencyViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by vm.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "currency") {

        // Ekran główny
        composable("currency") {
            CurrencyScreen(vm = vm, onShowChart = { code ->
                navController.navigate("chart/$code")
            })
        }

        // Ekran wykresu
        composable(
            route = "chart/{code}",
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            ChartScreen(
                uiState = uiState,
                code = code,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


