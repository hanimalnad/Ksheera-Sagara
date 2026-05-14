package com.example.ksheerasagara.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ksheerasagara.ui.theme.*
import com.example.ksheerasagara.viewmodel.DairyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DairyViewModel, padding: PaddingValues) {
    val income      by vm.monthlyIncome.collectAsState()
    val expenses    by vm.monthlyExpenses.collectAsState()
    val profit      by vm.netProfit.collectAsState()
    val month       by vm.selectedMonth.collectAsState()
    val milkEntries by vm.allMilkEntries.collectAsState()

    val isProfitable = profit >= 0
    val healthColor  = if (isProfitable) GreenPrimary else RedAlert
    val healthBg     = if (isProfitable) GreenBg      else RedBg
    val statusText   = if (isProfitable) "PROFIT ✓"   else "LOSS ✗"

    val monthMilk   = milkEntries.filter { it.date.startsWith(month) }
    val totalLiters = monthMilk.sumOf { it.liters }
    val profitPerL  = if (totalLiters > 0) profit / totalLiters else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Month Selector
        OutlinedTextField(
            value         = month,
            onValueChange = { if (it.length <= 7) vm.setMonth(it) },
            label         = { Text("Month (YYYY-MM)", fontSize = 14.sp) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp)
        )

        // Financial Health Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(containerColor = healthColor)
        ) {
            Column(
                modifier            = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(statusText, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Net Profit / Loss", fontSize = 13.sp, color = Color.White.copy(0.8f))
                Text(
                    "Rs. ${"%.2f".format(profit)}",
                    fontSize   = 36.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Income / Expense Row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MoneyCard("Total Income",   income,   GreenPrimary, GreenBg, Modifier.weight(1f))
            MoneyCard("Total Expenses", expenses, RedAlert,     RedBg,   Modifier.weight(1f))
        }

        // Key Metrics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(14.dp),
            colors   = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Key Metrics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))
                MetricRow("Total Liters Sold",
                    "${"%.1f".format(totalLiters)} L")
                MetricRow("Profit Per Liter",
                    "Rs. ${"%.2f".format(profitPerL)}",
                    if (profitPerL >= 0) GreenPrimary else RedAlert)
                MetricRow("Days Recorded",
                    "${monthMilk.map { it.date }.distinct().size} days")
            }
        }

        // Tips (only when loss)
        if (!isProfitable && expenses > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = AmberBg)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Cost Reduction Tips",
                        fontWeight = FontWeight.Bold,
                        color      = AmberWarning,
                        fontSize   = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("• Switch to home-grown fodder to cut feed costs",
                        fontSize = 14.sp)
                    Text("• Review non-productive cows — consider selling",
                        fontSize = 14.sp)
                    Text("• Group vet visits to reduce per-trip charges",
                        fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun MoneyCard(
    title: String,
    amount: Double,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 13.sp, color = TextMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Rs. ${"%.0f".format(amount)}",
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = textColor
            )
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, valueColor: Color = TextDark) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextMedium)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
    HorizontalDivider(color = Color(0xFFEEEEEE))
}