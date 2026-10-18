package com.team7.meetmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.team7.meetmind.navigation.MeetMindNavGraph
import com.team7.meetmind.ui.theme.MeetMindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetMindTheme {
                MeetMindNavGraph()
            }
        }
    }
}
