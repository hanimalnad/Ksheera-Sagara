package com.example.ksheerasagara.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ksheerasagara.data.model.ExpenseCategory
import com.example.ksheerasagara.ui.components.ExpensePieChart
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.utils.PdfGenerator
import com.example.ksheerasagara.utils.SessionManager
import com.example.ksheerasagara.viewmodel.DairyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(vm: DairyViewModel, padding: PaddingValues) {
    val context     = LocalContext.current
    val session     = remember { SessionManager(context) }
    val scope       = rememberCoroutineScope()

    val breakdown   by vm.expenseCategoryBreakdown.collectAsState()
    val cowSummaries by vm.cowSummaries.collectAsState()
    val month       by vm.selectedMonth.collectAsState()
    val milkEntries by vm.allMilkEntries.collectAsState()
    val allExpenses by vm.allExpenses.collectAsState()
    val netProfit   by vm.netProfit.collectAsState()

    var isGenerating by remember { mutableStateOf(false) }
    var snackMsg     by remember { mutableStateOf("") }
    val snackState   = remember { SnackbarHostState() }

    val categoryColors = mapOf(
        ExpenseCategory.FODDER      to ColorFodder,
        ExpenseCategory.MEDICAL     to ColorMedical,
        ExpenseCategory.LABOR       to ColorLabor,
        ExpenseCategory.ELECTRICITY to ColorElectricity,
        ExpenseCategory.OTHER       to ColorOther
    )

    val totalExpenses = breakdown.values.sum()

    Scaffold(
        snackbarHost = { SnackbarHost(snackState) }
    ) { innerPad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCream)
                .padding(padding)
                .padding(innerPad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Section: Expense Pie Chart ─────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Where is your money going?",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Expense breakdown — $month",
                        fontSize = 13.sp, color = TextLight)
                    Spacer(Modifier.height(12.dp))

                    if (breakdown.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No expenses recorded for this month",
                                color = TextLight, fontSize = 14.sp)
                        }
                    } else {
                        // Pie Chart
                        ExpensePieChart(
                            breakdown = breakdown,
                            modifier  = Modifier.fillMaxWidth().height(200.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Legend with bars
                        breakdown.toList()
                            .sortedByDescending { it.second }
                            .forEach { (cat, amount) ->
                                val pct   = if (totalExpenses > 0) amount / totalExpenses else 0.0
                                val color = categoryColors[cat] ?: Color.Gray
                                Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                Modifier
                                                    .size(12.dp)
                                                    .background(color, RoundedCornerShape(3.dp))
                                            )
                                            Text(cat.name, fontSize = 14.sp, color = TextDark)
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                "${"%.1f".format(pct * 100)}%",
                                                fontSize = 13.sp,
                                                color    = TextLight
                                            )
                                            Text(
                                                "Rs. ${"%.0f".format(amount)}",
                                                fontSize   = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color      = TextDark
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    // Progress bar
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .background(Color(0xFFF0F0F0), RoundedCornerShape(3.dp))
                                    ) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(pct.toFloat())
                                                .height(6.dp)
                                                .background(color, RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }
                    }
                }
            }

            // ── Section: Cow-wise Analysis ─────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Cow-wise Profitability",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Which cow earns the most — $month",
                        fontSize = 13.sp, color = TextLight)
                    Spacer(Modifier.height(12.dp))

                    if (cowSummaries.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No milk entries for this month",
                                color = TextLight, fontSize = 14.sp)
                        }
                    } else {
                        val maxIncome = cowSummaries.maxOf { it.totalIncome }

                        cowSummaries.forEachIndexed { index, cow ->
                            val isTop    = index == 0
                            val barRatio = if (maxIncome > 0)
                                (cow.totalIncome / maxIncome).toFloat() else 0f
                            val profitColor = if (cow.profitEstimate >= 0) GreenPrimary else RedAlert

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = CardDefaults.cardColors(
                                    containerColor = if (isTop) GreenBg else Color(0xFFF8F8F8)
                                )
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment     = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (isTop) {
                                                Text("🏆", fontSize = 18.sp)
                                            } else {
                                                Text(
                                                    "${index + 1}",
                                                    fontSize   = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color      = TextLight,
                                                    modifier   = Modifier.width(20.dp)
                                                )
                                            }
                                            Column {
                                                Text(cow.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize   = 15.sp)
                                                Text("${"%.1f".format(cow.totalLiters)} L",
                                                    fontSize = 13.sp, color = TextLight)
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "Rs. ${"%.0f".format(cow.totalIncome)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize   = 15.sp,
                                                color      = GreenPrimary
                                            )
                                            Text(
                                                "Est. profit: Rs. ${"%.0f".format(cow.profitEstimate)}",
                                                fontSize = 12.sp,
                                                color    = profitColor
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    // Income bar
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
                                    ) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth(barRatio)
                                                .height(6.dp)
                                                .background(GreenPrimary, RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Section: Generate PDF ──────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Monthly Financial Summary",
                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Generate a PDF report for $month to share or print",
                        fontSize = 13.sp, color = TextLight,
                        modifier = Modifier.padding(vertical = 4.dp))

                    Spacer(Modifier.height(12.dp))

                    // Preview stats
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Milk entries",
                                fontSize = 12.sp, color = TextLight)
                            Text(
                                "${milkEntries.count { it.date.startsWith(month) }}",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = GreenPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Expense items",
                                fontSize = 12.sp, color = TextLight)
                            Text(
                                "${allExpenses.count { it.date.startsWith(month) }}",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = RedAlert
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cows tracked",
                                fontSize = 12.sp, color = TextLight)
                            Text(
                                "${cowSummaries.size}",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = BlueMed
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isGenerating = true
                            scope.launch {
                                try {
                                    val monthMilk = milkEntries.filter { it.date.startsWith(month) }
                                    val monthExp  = allExpenses.filter { it.date.startsWith(month) }

                                    val file = PdfGenerator.generateMonthlySummary(
                                        context      = context,
                                        month        = month,
                                        farmerName   = session.getFarmerName(),
                                        farmName     = session.getFarmName(),
                                        milkEntries  = monthMilk,
                                        expenses     = monthExp,
                                        netProfit    = netProfit
                                    )

                                    // Share the PDF
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type      = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(intent, "Share PDF Report")
                                    )
                                } catch (e: Exception) {
                                    snackState.showSnackbar("Error generating PDF: ${e.message}")
                                } finally {
                                    isGenerating = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        enabled  = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color    = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Generating...", fontSize = 16.sp, color = Color.White)
                        } else {
                            Icon(Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint               = Color.White)
                            Spacer(Modifier.width(10.dp))
                            Text("Generate & Share PDF",
                                fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}