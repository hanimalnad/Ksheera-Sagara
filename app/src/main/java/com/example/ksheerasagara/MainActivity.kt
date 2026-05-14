package com.example.ksheerasagara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.ksheerasagara.ui.screens.AppNavigation
import com.example.ksheerasagara.ui.screens.LoginScreen
import com.example.ksheerasagara.ui.screens.SignUpScreen
import com.example.ksheerasagara.ui.theme.KsheeraSagaraTheme
import com.example.ksheerasagara.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed enableEdgeToEdge() — this was causing the gap

        val session = SessionManager(this)

        setContent {
            KsheeraSagaraTheme {
                var screen by remember {
                    mutableStateOf(
                        when {
                            !session.isRegistered() -> "signup"
                            !session.isLoggedIn()   -> "login"
                            else                    -> "app"
                        }
                    )
                }

                when (screen) {
                    "signup" -> SignUpScreen(onSignUpComplete = { screen = "app" })
                    "login" -> LoginScreen(
                        onLoginSuccess = { screen = "app" },
                        onRegisterNew  = { screen = "signup" }   // ← pass this
                    )
                    "app"    -> AppNavigation(onLogout = {
                        session.logout()
                        screen = "login"
                    })
                }
            }
        }
    }
}