package com.elinext.thomeassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.elinext.thomeassignment.ui.HomeScreen
import com.elinext.thomeassignment.ui.theme.ElinextHomeAssignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElinextHomeAssignmentTheme {
                HomeScreen()
            }
        }
    }
}
