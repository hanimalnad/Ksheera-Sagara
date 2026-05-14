package com.example.ksheerasagara.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.utils.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    padding: PaddingValues,
    onLogout: () -> Unit
) {
    val context    = LocalContext.current
    val session    = remember { SessionManager(context) }
    val keyboard   = LocalSoftwareKeyboardController.current
    val snackState = remember { SnackbarHostState() }
    val scope      = rememberCoroutineScope()

    var farmerName by remember { mutableStateOf(session.getFarmerName()) }
    var farmName   by remember { mutableStateOf(session.getFarmName()) }
    var village    by remember { mutableStateOf(session.getVillage()) }
    var phone      by remember { mutableStateOf(session.getPhone()) }

    var currentPin by remember { mutableStateOf("") }
    var newPin     by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinError   by remember { mutableStateOf("") }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text  = { Text("Are you sure you want to logout?", fontSize = 15.sp) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors  = ButtonDefaults.buttonColors(containerColor = RedAlert)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier     = Modifier.padding(padding),
        snackbarHost = { SnackbarHost(snackState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Farm Profile ─────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Farm Profile",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    HorizontalDivider(color = DividerColor)

                    OutlinedTextField(
                        value         = farmerName,
                        onValueChange = { farmerName = it },
                        label         = { Text("Farmer Name", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true
                    )
                    OutlinedTextField(
                        value         = farmName,
                        onValueChange = { farmName = it },
                        label         = { Text("Farm Name", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true
                    )
                    OutlinedTextField(
                        value         = village,
                        onValueChange = { village = it },
                        label         = { Text("Village", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true
                    )
                    OutlinedTextField(
                        value         = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        label         = { Text("Mobile Number", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Button(
                        onClick = {
                            if (farmerName.isNotBlank()) {
                                session.updateProfile(farmerName, farmName, village, phone)
                                keyboard?.hide()
                                scope.launch {
                                    snackState.showSnackbar("Profile updated!")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Profile", fontSize = 15.sp, color = Color.White)
                    }
                }
            }

            // ── Change PIN ────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Change PIN",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    HorizontalDivider(color = DividerColor)

                    OutlinedTextField(
                        value         = currentPin,
                        onValueChange = { if (it.length <= 4) currentPin = it },
                        label         = { Text("Current PIN", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value         = newPin,
                        onValueChange = { if (it.length <= 4) newPin = it },
                        label         = { Text("New PIN (4 digits)", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value         = confirmPin,
                        onValueChange = { if (it.length <= 4) confirmPin = it },
                        label         = { Text("Confirm New PIN", fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        isError       = pinError.isNotEmpty()
                    )

                    if (pinError.isNotEmpty()) {
                        Text(pinError, color = RedAlert, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            when {
                                !session.verifyPin(currentPin) ->
                                    pinError = "Current PIN is wrong"
                                newPin.length != 4 ->
                                    pinError = "New PIN must be 4 digits"
                                newPin != confirmPin ->
                                    pinError = "New PINs do not match"
                                else -> {
                                    session.updatePin(newPin)
                                    currentPin = ""; newPin = ""; confirmPin = ""
                                    pinError = ""
                                    keyboard?.hide()
                                    scope.launch {
                                        snackState.showSnackbar("PIN changed successfully!")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Update PIN", fontSize = 15.sp, color = Color.White)
                    }
                }
            }

            // ── App Info ──────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("App Info",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    HorizontalDivider(
                        color    = DividerColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    InfoRow("App",         "Ksheera-Sagara")
                    InfoRow("Version",     "1.0")
                    InfoRow("Purpose",     "Dairy Profit / Loss Tracker")
                    InfoRow("Data stored", "On this phone only")
                }
            }

            // ── Logout Button ─────────────────────────────
            Button(
                onClick  = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RedAlert)
            ) {
                Icon(Icons.Default.Logout,
                    contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Logout", fontSize = 16.sp, color = Color.White,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextMedium)
        Text(value, fontSize = 14.sp,
            fontWeight = FontWeight.Medium, color = TextDark)
    }
}
