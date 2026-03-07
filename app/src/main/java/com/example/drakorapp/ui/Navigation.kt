package com.example.drakorapp.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.drakorapp.ui.screens.*
import com.example.drakorapp.viewmodel.DrakorViewModel
import com.example.drakorapp.viewmodel.UiState

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val vm: DrakorViewModel = viewModel()

    NavHost(nav, startDestination = "home") {

        composable("home") {
            HomeScreen(vm,
                onDetail   = { nav.navigate("detail/$it") },
                onAdd      = { nav.navigate("add") },
                onProfile  = { nav.navigate("profile") },
                onSettings = { nav.navigate("settings") }
            )
        }

        composable("detail/{id}", listOf(navArgument("id") { type = NavType.StringType })) {
            val id = it.arguments?.getString("id") ?: ""
            DetailScreen(vm, id,
                onBack = { nav.popBackStack() },
                onEdit = { editId -> nav.navigate("edit/$editId") }
            )
        }

        composable("add") {
            FormScreen(vm, existing = null, onBack = { nav.popBackStack() })
        }

        composable("edit/{id}", listOf(navArgument("id") { type = NavType.StringType })) {
            val detailState by vm.detailState.collectAsState()
            val drakor = (detailState as? UiState.Success)?.data
            FormScreen(vm, existing = drakor, onBack = { nav.popBackStack() })
        }

        composable("profile") {
            ProfileScreen(vm, onBack = { nav.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(vm, onBack = { nav.popBackStack() })
        }
    }
}
