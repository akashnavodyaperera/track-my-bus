package com.wycherley.trackmybus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.wycherley.trackmybus.ui.theme.TrackMyBusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test Firebase Connection
        testFirebaseConnection()

        enableEdgeToEdge()
        setContent {
            TrackMyBusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Track My Bus\n\nCheck Logcat for Firebase test!",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }

    private fun testFirebaseConnection() {
        try {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("test_message")
            myRef.setValue("Hello from Track My Bus!")
            Log.d("FirebaseTest", "✅ Firebase connection successful! Data written.")
        } catch (e: Exception) {
            Log.e("FirebaseTest", "❌ Firebase error: ${e.message}")
        }
    }
}