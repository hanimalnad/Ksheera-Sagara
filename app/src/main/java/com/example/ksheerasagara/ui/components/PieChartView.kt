package com.example.ksheerasagara.ui.components

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ksheerasagara.data.model.ExpenseCategory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@Composable
fun ExpensePieChart(
    breakdown: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    if (breakdown.isEmpty()) return

    val categoryColors = mapOf(
        ExpenseCategory.FODDER      to Color.parseColor("#43A047"),
        ExpenseCategory.MEDICAL     to Color.parseColor("#E53935"),
        ExpenseCategory.LABOR       to Color.parseColor("#1E88E5"),
        ExpenseCategory.ELECTRICITY to Color.parseColor("#FB8C00"),
        ExpenseCategory.OTHER       to Color.parseColor("#8E24AA")
    )

    val totalAmount = breakdown.values.sum()

    val chartRef = remember { mutableStateOf<PieChart?>(null) }

    fun updateHole(chart: PieChart, cat: ExpenseCategory?, amount: Double, percent: Double) {
        if (cat == null) {
            chart.centerText = "Total\nRs. ${"%.0f".format(totalAmount)}"
            chart.setCenterTextColor(Color.DKGRAY)
            chart.setCenterTextSize(13f)
        } else {
            chart.centerText = "${cat.name}\n${"%.1f".format(percent)}%\nRs. ${"%.0f".format(amount)}"
            chart.setCenterTextColor(categoryColors[cat] ?: Color.DKGRAY)
            chart.setCenterTextSize(12f)
        }
        chart.invalidate()
    }

    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled   = false
                    isDrawHoleEnabled       = true
                    holeRadius              = 52f
                    transparentCircleRadius = 56f
                    setHoleColor(Color.WHITE)
                    setTransparentCircleColor(Color.WHITE)
                    setTransparentCircleAlpha(80)
                    isRotationEnabled       = true
                    legend.isEnabled        = false
                    setDrawEntryLabels(false)
                    setUsePercentValues(false)
                    animateY(900)

                    centerText = "Total\nRs. ${"%.0f".format(totalAmount)}"
                    setCenterTextSize(13f)
                    setCenterTextColor(Color.DKGRAY)
                    setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                    setDrawCenterText(true)

                    chartRef.value = this

                    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            if (e is PieEntry) {
                                val cat = ExpenseCategory.values()
                                    .firstOrNull { it.name == e.label } ?: return
                                val amt = breakdown[cat] ?: 0.0
                                val pct = (amt / totalAmount) * 100
                                updateHole(this@apply, cat, amt, pct)
                            }
                        }
                        override fun onNothingSelected() {
                            updateHole(this@apply, null, 0.0, 0.0)
                        }
                    })
                }
            },
            update = { chart ->
                chartRef.value = chart

                val entries = breakdown.map { (cat, amount) ->
                    PieEntry(amount.toFloat(), cat.name)
                }
                val colors = breakdown.keys.map {
                    categoryColors[it] ?: Color.GRAY
                }
                val dataSet = PieDataSet(entries, "Expenses").apply {
                    this.colors    = colors
                    sliceSpace     = 3f
                    selectionShift = 10f
                    setDrawValues(false)
                }

                chart.data = PieData(dataSet)
                updateHole(chart, null, 0.0, 0.0)
            }
        )
    }
}
