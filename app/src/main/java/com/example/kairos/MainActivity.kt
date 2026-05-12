package com.example.kairos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kairos.ui.theme.KairosTheme
import com.example.kairos.view.MainScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KairosTheme {
                MainScreen()
            }
        }
    }
}