package com.example.ksheerasagara.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.data.model.Cow
import com.example.ksheerasagara.ui.components.DeleteConfirmDialog
import com.example.ksheerasagara.ui.components.EmptyState
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.viewmodel.DairyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowScreen(vm: DairyViewModel, padding: PaddingValues) {
    val cows        by vm.allCows.collectAsState()
    val milkEntries by vm.allMilkEntries.collectAsState()
    val month       by vm.selectedMonth.collectAsState()
    val keyboard    = LocalSoftwareKeyboardController.current
    val snackState  = remember { SnackbarHostState() }
    val scope       = rememberCoroutineScope()

    var showDialog  by remember { mutableStateOf(false) }
    var editingCow  by remember { mutableStateOf<Cow?>(null) }
    var deleteTarget by remember { mutableStateOf<Cow?>(null) }

    var name  by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age   by remember { mutableStateOf("") }

    fun openAdd() {
        editingCow = null
        name = ""; breed = ""; age = ""
        showDialog = true
    }

    fun openEdit(cow: Cow) {
        editingCow = cow
        name  = cow.name
        breed = cow.breed
        age   = cow.age.toString()
        showDialog = true
    }

    deleteTarget?.let { target ->
        DeleteConfirmDialog(
            title   = "Remove Cow",
            message = "Remove ${target.name} from your farm? Milk records linked to this cow will remain.",
            onConfirm = { vm.deleteCow(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }

    Scaffold(
        modifier     = Modifier.padding(padding),
        snackbarHost = { SnackbarHost(snackState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Cows",
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${cows.size} cows registered",
                            fontSize = 12.sp, color = TextLight)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { openAdd() },
                containerColor = GreenPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Add Cow", tint = Color.White) }
        }
    ) { inner ->
        if (cows.isEmpty()) {
            EmptyState(
                emoji    = "🐄",
                title    = "No cows added yet",
                subtitle = "Tap + to register your first cow",
                modifier = Modifier.padding(inner)
            )
        } else {
            LazyColumn(
                modifier       = Modifier.padding(inner).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            ) {
                items(cows, key = { it.id }) { cow ->
                    // All-time totals
                    val allTimeLiters = milkEntries
                        .filter { it.cowName == cow.name }.sumOf { it.liters }
                    val allTimeIncome = milkEntries
                        .filter { it.cowName == cow.name }.sumOf { it.totalIncome }
                    // This month totals
                    val monthLiters = milkEntries
                        .filter { it.cowName == cow.name && it.date.startsWith(month) }
                        .sumOf { it.liters }
                    val monthIncome = milkEntries
                        .filter { it.cowName == cow.name && it.date.startsWith(month) }
                        .sumOf { it.totalIncome }

                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.Top
                            ) {
                                Column {
                                    Text(cow.name,
                                        fontWeight = FontWeight.Bold, fontSize = 19.sp)
                                    Text("${cow.breed}  •  Age ${cow.age} yrs",
                                        fontSize = 13.sp, color = TextLight)
                                }
                                Row {
                                    IconButton(
                                        onClick  = { openEdit(cow) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint     = BlueMed,
                                            modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick  = { deleteTarget = cow },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint     = RedAlert,
                                            modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = DividerColor)
                            Spacer(Modifier.height(10.dp))

                            // This month stats
                            Text("This month ($month)",
                                fontSize = 12.sp, color = TextLight,
                                fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatChip("${"%.1f".format(monthLiters)} L",
                                    "Liters", GreenBg, GreenPrimary)
                                StatChip("Rs.${"%.0f".format(monthIncome)}",
                                    "Income", GreenBg, GreenPrimary)
                            }

                            Spacer(Modifier.height(8.dp))

                            // All-time stats
                            Text("All time",
                                fontSize = 12.sp, color = TextLight,
                                fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatChip("${"%.1f".format(allTimeLiters)} L",
                                    "Liters", BlueBg, BlueMed)
                                StatChip("Rs.${"%.0f".format(allTimeIncome)}",
                                    "Income", BlueBg, BlueMed)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; keyboard?.hide() },
            title = {
                Text(
                    if (editingCow == null) "Add Cow" else "Edit Cow",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Cow Name", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = breed, onValueChange = { breed = it },
                        label = { Text("Breed (e.g. HF, Jersey)", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = age, onValueChange = { age = it },
                        label = { Text("Age (years)", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            vm.addCow(Cow(
                                id    = editingCow?.id ?: 0,
                                name  = name,
                                breed = breed,
                                age   = age.toIntOrNull() ?: 0
                            ))
                        }
                        keyboard?.hide()
                        showDialog = false
                        val msg = if (editingCow == null) "Cow added!" else "Cow updated!"
                        scope.launch {
                            snackState.showSnackbar(msg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(if (editingCow == null) "Save" else "Update", fontSize = 15.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; keyboard?.hide() }) {
                    Text("Cancel", fontSize = 15.sp)
                }
            }
        )
    }
}

@Composable
fun StatChip(value: String, label: String, bg: Color, textColor: Color) {
    Column(
        modifier            = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(label, fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
    }
}