package com.example.shelffit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelffit.ui.ShelfFitApp
import com.example.shelffit.viewmodel.ShelfViewModel
import com.example.shelffit.viewmodel.ShelfViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ShelfViewModel = viewModel(
                factory = ShelfViewModelFactory(application)
            )
            ShelfFitApp(viewModel = viewModel)
        }
    }
}
