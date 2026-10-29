package com.example.miniproyecto3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miniproyecto3.presentation.FeedScreen
import com.example.miniproyecto3.presentation.NuevaPublicacionScreen
import com.example.miniproyecto3.presentation.PublicacionViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PublicacionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "feed") {
                composable("feed") {
                    FeedScreen(
                        viewModel = viewModel,
                        onIrANuevaPublicacion = {
                            navController.navigate("nueva_publicacion")
                        }
                    )
                }
                composable("nueva_publicacion") {
                    NuevaPublicacionScreen(
                        viewModel = viewModel,
                        onRegresar = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}