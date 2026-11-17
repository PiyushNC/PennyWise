package com.pennywise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.pennywise.app.ui.PennyWiseApp
import com.pennywise.app.ui.theme.PennyWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PennyWiseTheme {
                Surface {
                    PennyWiseApp()
                }
            }
        }
    }
}
