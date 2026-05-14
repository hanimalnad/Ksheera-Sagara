package com.example.ksheerasagara.ui.screens

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
import com.example.ksheerasagara.data.model.MilkEntry
import com.example.ksheerasagara.ui.components.DeleteConfirmDialog
import com.example.ksheerasagara.ui.components.EmptyState
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.viewmodel.DairyViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkEntryScreen(vm: DairyViewModel, padding: PaddingValues) {
    val entries       by vm.allMilkEntries.collectAsState()
    val month         by vm.selectedMonth.collectAsState()
    val keyboard      = LocalSoftwareKeyboardController.current
    val snackState    = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()

    var showDialog    by remember { mutableStateOf(false) }
    var editingEntry  by remember { mutableStateOf<MilkEntry?>(null) }
    var deleteTarget  by remember { mutableStateOf<MilkEntry?>(null) }

    var date     by remember { mutableStateOf(LocalDate.now().toString()) }
    var cowName  by remember { mutableStateOf("General") }
    var liters   by remember { mutableStateOf("") }
    var fat      by remember { mutableStateOf("") }
    var snf      by remember { mutableStateOf("") }
    var rate     by remember { mutableStateOf("") }

    // Month filtered entries
    val monthEntries = entries.filter { it.date.startsWith(month) }

    fun openAdd() {
        editingEntry = null
        date    = LocalDate.now().toString()
        cowName = "General"
        liters = ""; fat = ""; snf = ""; rate = ""
        showDialog = true
    }

    fun openEdit(entry: MilkEntry) {
        editingEntry = entry
        date    = entry.date
        cowName = entry.cowName
        liters  = entry.liters.toString()
        fat     = entry.fatPercent.toString()
        snf     = entry.snfPercent.toString()
        rate    = entry.ratePerLiter.toString()
        showDialog = true
    }

    // Delete confirmation dialog
    deleteTarget?.let { target ->
        DeleteConfirmDialog(
            title   = "Delete Milk Entry",
            message = "Delete entry for ${target.cowName} on ${target.date}?",
            onConfirm = {
                vm.deleteMilkEntry(target)
                deleteTarget = null
            },
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
                        Text("Milk Income Log",
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${monthEntries.size} entries • $month",
                            fontSize = 12.sp, color = TextLight)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { openAdd() },
                containerColor = GreenPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White) }
        }
    ) { inner ->
        if (entries.isEmpty()) {
            EmptyState(
                emoji    = "🥛",
                title    = "No milk entries yet",
                subtitle = "Tap + to add your first milk slip entry",
                modifier = Modifier.padding(inner)
            )
        } else {
            LazyColumn(
                modifier       = Modifier.padding(inner).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                // ── FAB overlap fix ──────────────────────
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(containerColor = CardWhite),
                        // ── shadow / elevation ───────────
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(entry.date,
                                    fontSize = 12.sp, color = TextLight)
                                Spacer(Modifier.height(2.dp))
                                Text(entry.cowName,
                                    fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${entry.liters} L  •  Fat ${entry.fatPercent}%  •  SNF ${entry.snfPercent}%",
                                    fontSize = 13.sp, color = TextMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Rs. ${"%.2f".format(entry.totalIncome)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 17.sp,
                                    color      = GreenPrimary
                                )
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    IconButton(
                                        onClick  = { openEdit(entry) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint     = BlueMed,
                                            modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick  = { deleteTarget = entry },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint     = RedAlert,
                                            modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                keyboard?.hide()
            },
            title = {
                Text(
                    if (editingEntry == null) "Add Milk Entry" else "Edit Milk Entry",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = date, onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = cowName, onValueChange = { cowName = it },
                        label = { Text("Cow Name", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = liters, onValueChange = { liters = it },
                        label = { Text("Liters", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = fat, onValueChange = { fat = it },
                        label = { Text("Fat %", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = snf, onValueChange = { snf = it },
                        label = { Text("SNF %", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = rate, onValueChange = { rate = it },
                        label = { Text("Rate per Liter (Rs.)", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val l = liters.toDoubleOrNull() ?: 0.0
                        val r = rate.toDoubleOrNull()   ?: 0.0
                        vm.addMilkEntry(
                            MilkEntry(
                                id           = editingEntry?.id ?: 0,
                                date         = date,
                                cowName      = cowName,
                                liters       = l,
                                fatPercent   = fat.toDoubleOrNull() ?: 0.0,
                                snfPercent   = snf.toDoubleOrNull() ?: 0.0,
                                ratePerLiter = r,
                                totalIncome  = l * r
                            )
                        )
                        keyboard?.hide()
                        showDialog = false
                        // Snackbar feedback
                        val msg = if (editingEntry == null) "Entry saved!" else "Entry updated!"
                        scope.launch {
                            snackState.showSnackbar(msg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(
                        if (editingEntry == null) "Save" else "Update",
                        fontSize = 15.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    keyboard?.hide()
                }) { Text("Cancel", fontSize = 15.sp) }
            }
        )
    }
}
