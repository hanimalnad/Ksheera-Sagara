package com.example.ksheerasagara.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUpComplete: () -> Unit) {
    val context    = LocalContext.current
    val session    = remember { SessionManager(context) }

    var farmerName  by remember { mutableStateOf("") }
    var farmName    by remember { mutableStateOf("") }
    var village     by remember { mutableStateOf("") }
    var phone       by remember { mutableStateOf("") }
    var pin         by remember { mutableStateOf("") }
    var confirmPin  by remember { mutableStateOf("") }
    var errorMsg    by remember { mutableStateOf("") }
    var step        by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── App Header ──
            Text("🐄", fontSize = 60.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Ksheera-Sagara",
                fontSize   = 32.sp,
                fontWeight = FontWeight.Bold,
                color      = GreenPrimary,
                textAlign  = TextAlign.Center
            )
            Text(
                "Dairy Profit / Loss Tracker",
                fontSize  = 15.sp,
                color     = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            // ── Step Indicator ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StepDot(active = step == 0, done = step > 0, label = "1")
                HorizontalDivider(
                    modifier = Modifier.width(48.dp).padding(horizontal = 4.dp),
                    color    = if (step > 0) GreenPrimary else Color.LightGray,
                    thickness = 2.dp
                )
                StepDot(active = step == 1, done = false, label = "2")
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (step == 0) "Personal Info" else "Set PIN",
                fontSize  = 14.sp,
                color     = GreenPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(20.dp))

            // ── STEP 0: Personal Info ──
            if (step == 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value         = farmerName,
                            onValueChange = { farmerName = it },
                            label         = { Text("Farmer Name", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            isError       = errorMsg.isNotEmpty() && farmerName.isBlank()
                        )
                        OutlinedTextField(
                            value         = farmName,
                            onValueChange = { farmName = it },
                            label         = { Text("Farm Name", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = village,
                            onValueChange = { village = it },
                            label         = { Text("Village", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = phone,
                            onValueChange = { if (it.length <= 10) phone = it },
                            label         = { Text("Mobile Number", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        if (errorMsg.isNotEmpty()) {
                            Text(errorMsg, color = RedAlert, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                if (farmerName.isBlank()) {
                                    errorMsg = "Name is required."
                                } else {
                                    errorMsg = ""
                                    step = 1
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Next →", fontSize = 17.sp, color = Color.White)
                        }
                    }
                }
            }

            // ── STEP 1: Set PIN ──
            if (step == 1) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier                = Modifier.padding(24.dp),
                        horizontalAlignment     = Alignment.CenterHorizontally,
                        verticalArrangement     = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "This 4-digit PIN protects your farm data. You'll enter it every time you open the app.",
                            fontSize  = 14.sp,
                            color     = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        // PIN progress dots
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            repeat(4) { i ->
                                Box(
                                    Modifier
                                        .size(16.dp)
                                        .background(
                                            if (i < pin.length) GreenPrimary else Color.LightGray,
                                            RoundedCornerShape(50)
                                        )
                                )
                            }
                        }

                        OutlinedTextField(
                            value         = pin,
                            onValueChange = { if (it.length <= 4) pin = it },
                            label         = { Text("New PIN (4 digits)", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation()
                        )
                        OutlinedTextField(
                            value         = confirmPin,
                            onValueChange = { if (it.length <= 4) confirmPin = it },
                            label         = { Text("Confirm PIN", fontSize = 15.sp) },
                            textStyle     = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        if (errorMsg.isNotEmpty()) {
                            Text(errorMsg, color = RedAlert, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }

                        Button(
                            onClick = {
                                when {
                                    pin.length != 4    -> errorMsg = "PIN must be exactly 4 digits."
                                    pin != confirmPin  -> errorMsg = "PINs do not match. Try again."
                                    else -> {
                                        session.register(
                                            farmerName = farmerName.trim(),
                                            farmName   = farmName.trim().ifBlank { "My Farm" },
                                            pin        = pin,
                                            village    = village.trim(),
                                            phone      = phone.trim()
                                        )
                                        onSignUpComplete()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Register", fontSize = 17.sp, color = Color.White)
                        }

                        TextButton(onClick = { step = 0; errorMsg = "" }) {
                            Text("← Back", color = Color.Gray, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Data is private — stored on this phone only", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StepDot(active: Boolean, done: Boolean, label: String) {
    val bgColor = when {
        done -> GreenPrimary
        active -> GreenPrimary
        else -> Color.LightGray
    }
    val contentColor = if (done || active) Color.White else Color.Gray

    Surface(
        shape = CircleShape,
        color = bgColor,
        modifier = Modifier.size(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (done) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
            } else {
                Text(label, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
