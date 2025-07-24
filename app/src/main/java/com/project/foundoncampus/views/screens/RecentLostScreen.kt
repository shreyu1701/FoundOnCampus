package com.project.foundoncampus.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.foundoncampus.R
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentLostScreen(navController: NavController) {
    val lostItems = remember { listOf("cupcake", "donut", "eclair", "chocolate") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Lost") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            lostItems.forEach { label ->
                SimpleCard(label, R.drawable.ic_launcher_foreground)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}