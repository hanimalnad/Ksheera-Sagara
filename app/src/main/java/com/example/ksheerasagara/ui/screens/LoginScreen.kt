package com.example.ksheerasagara.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.utils.SessionManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterNew: () -> Unit
) {
    val context  = LocalContext.current
    val session  = remember { SessionManager(context) }

    var pin      by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var attempts by remember { mutableIntStateOf(0) }
    var showConfirmNewAccount by remember { mutableStateOf(false) }

    if (showConfirmNewAccount) {
        AlertDialog(
            onDismissRequest = { showConfirmNewAccount = false },
            title = {
                Text("Register New Account?",
                    fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Text(
                    "A brand new account will be created.\n\n" +
                            "Your previous account's data (milk, expenses, cows) will remain " +
                            "saved and will be visible if you log back into that account.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        session.clearSession()
                        showConfirmNewAccount = false
                        onRegisterNew()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("Create New Account", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmNewAccount = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BgCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("🐄", fontSize = 52.sp)

                    Text(
                        "Ksheera-Sagara",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GreenPrimary
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Welcome back,", fontSize = 15.sp, color = TextLight)
                        Text(
                            session.getFarmerName(),
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = GreenPrimary
                        )
                        if (session.getFarmName().isNotBlank()) {
                            Text(session.getFarmName(),
                                fontSize = 14.sp, color = TextLight)
                        }
                    }

                    HorizontalDivider()

                    // PIN dots
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(4) { i ->
                            Box(
                                Modifier.size(16.dp).background(
                                    if (i < pin.length) GreenPrimary else Color(0xFFE0E0E0),
                                    RoundedCornerShape(50)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value         = pin,
                        onValueChange = { if (it.length <= 4) { pin = it; errorMsg = "" } },
                        label         = { Text("Enter PIN", fontSize = 15.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = errorMsg.isNotEmpty()
                    )

                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, color = RedAlert, fontSize = 14.sp,
                            textAlign = TextAlign.Center)
                    }

                    Button(
                        onClick = {
                            if (session.login(pin)) {
                                onLoginSuccess()
                            } else {
                                attempts++
                                errorMsg = "Wrong PIN. Please try again."
                                pin = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        enabled  = pin.length == 4
                    ) {
                        Text("Login", fontSize = 17.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick  = { showConfirmNewAccount = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary)
                    ) {
                        Text("Register New Account", fontSize = 15.sp)
                    }

                    Text(
                        "Data is private — stored on this phone only",
                        fontSize = 12.sp, color = TextLight, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}