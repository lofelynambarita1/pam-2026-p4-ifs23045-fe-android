package com.example.drakorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.drakorapp.ui.AppNavigation
import com.example.drakorapp.ui.theme.DrakorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrakorTheme {
                AppNavigation()
            }
        }
    }
}
