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
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.data.model.ExpenseCategory
import com.example.ksheerasagara.ui.components.DeleteConfirmDialog
import com.example.ksheerasagara.ui.components.EmptyState
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.viewmodel.DairyViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(vm: DairyViewModel, padding: PaddingValues) {
    val expenses      by vm.allExpenses.collectAsState()
    val month         by vm.selectedMonth.collectAsState()
    val keyboard      = LocalSoftwareKeyboardController.current
    val snackState    = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()

    var showDialog   by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<Expense?>(null) }
    var deleteTarget by remember { mutableStateOf<Expense?>(null) }

    var date        by remember { mutableStateOf(LocalDate.now().toString()) }
    var category    by remember { mutableStateOf(ExpenseCategory.FODDER) }
    var description by remember { mutableStateOf("") }
    var amount      by remember { mutableStateOf("") }
    var expanded    by remember { mutableStateOf(false) }

    val catColor = mapOf(
        ExpenseCategory.FODDER      to ColorFodder,
        ExpenseCategory.MEDICAL     to ColorMedical,
        ExpenseCategory.LABOR       to ColorLabor,
        ExpenseCategory.ELECTRICITY to ColorElectricity,
        ExpenseCategory.OTHER       to ColorOther
    )

    val monthExpenses = expenses.filter { it.date.startsWith(month) }

    fun openAdd() {
        editingEntry = null
        date = LocalDate.now().toString()
        category = ExpenseCategory.FODDER
        description = ""; amount = ""
        showDialog = true
    }

    fun openEdit(exp: Expense) {
        editingEntry = exp
        date        = exp.date
        category    = exp.category
        description = exp.description
        amount      = exp.amount.toString()
        showDialog  = true
    }

    deleteTarget?.let { target ->
        DeleteConfirmDialog(
            title   = "Delete Expense",
            message = "Delete ${target.category.name} expense of Rs.${target.amount.toInt()} on ${target.date}?",
            onConfirm = { vm.deleteExpense(target); deleteTarget = null },
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
                        Text("Expense Log",
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${monthExpenses.size} entries • $month",
                            fontSize = 12.sp, color = TextLight)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { openAdd() },
                containerColor = RedAlert
            ) { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White) }
        }
    ) { inner ->
        if (expenses.isEmpty()) {
            EmptyState(
                emoji    = "💸",
                title    = "No expenses recorded",
                subtitle = "Tap + to log your first expense",
                modifier = Modifier.padding(inner)
            )
        } else {
            LazyColumn(
                modifier       = Modifier.padding(inner).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            ) {
                items(expenses, key = { it.id }) { exp ->
                    val color = catColor[exp.category] ?: Color.Gray
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(containerColor = CardWhite),
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
                                Text(exp.date, fontSize = 12.sp, color = TextLight)
                                Spacer(Modifier.height(4.dp))
                                Badge(containerColor = color) {
                                    Text(
                                        exp.category.name,
                                        color    = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(exp.description,
                                    fontSize = 15.sp, color = TextDark)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Rs. ${"%.0f".format(exp.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 17.sp,
                                    color      = RedAlert
                                )
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    IconButton(
                                        onClick  = { openEdit(exp) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint     = BlueMed,
                                            modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick  = { deleteTarget = exp },
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
            onDismissRequest = { showDialog = false; keyboard?.hide() },
            title = {
                Text(
                    if (editingEntry == null) "Add Expense" else "Edit Expense",
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
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category.name, onValueChange = {}, readOnly = true,
                            label = { Text("Category", fontSize = 14.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            ExpenseCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text    = { Text(cat.name, fontSize = 15.sp) },
                                    onClick = { category = cat; expanded = false }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Description", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = amount, onValueChange = { amount = it },
                        label = { Text("Amount (Rs.)", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.addExpense(
                            Expense(
                                id          = editingEntry?.id ?: 0,
                                date        = date,
                                category    = category,
                                description = description,
                                amount      = amount.toDoubleOrNull() ?: 0.0
                            )
                        )
                        keyboard?.hide()
                        showDialog = false
                        val msg = if (editingEntry == null) "Expense saved!" else "Expense updated!"
                        scope.launch {
                            snackState.showSnackbar(msg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(if (editingEntry == null) "Save" else "Update", fontSize = 15.sp)
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
