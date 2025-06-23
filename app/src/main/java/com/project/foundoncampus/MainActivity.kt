package com.project.foundoncampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import com.project.foundoncampus.views.screens.MainScreen
import com.project.foundoncampus.views.theme.FoundOnCampusTheme

@OptIn(ExperimentalMaterial3Api::class)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoundOnCampusTheme {
                MainScreen()
            }
        }
    }
}
