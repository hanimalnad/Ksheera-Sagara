package com.example.ksheerasagara.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.ksheerasagara.data.model.Expense
import com.example.ksheerasagara.data.model.ExpenseCategory
import com.example.ksheerasagara.data.model.MilkEntry
import java.io.File

object PdfGenerator {

    fun generateMonthlySummary(
        context: Context,
        month: String,
        farmerName: String,
        farmName: String,
        milkEntries: List<MilkEntry>,
        expenses: List<Expense>,
        netProfit: Double
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page     = document.startPage(pageInfo)
        val canvas   = page.canvas

        val margin  = 40f
        var y       = 60f
        val width   = 595f

        // ── Paints ──────────────────────────────────────────
        val paintTitle = Paint().apply {
            textSize  = 22f
            typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color     = Color.parseColor("#2E7D32")
        }
        val paintHeader = Paint().apply {
            textSize  = 14f
            typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color     = Color.parseColor("#333333")
        }
        val paintBody = Paint().apply {
            textSize = 13f
            color    = Color.parseColor("#444444")
        }
        val paintSmall = Paint().apply {
            textSize = 11f
            color    = Color.parseColor("#777777")
        }
        val paintGreen = Paint().apply {
            textSize  = 16f
            typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color     = Color.parseColor("#2E7D32")
        }
        val paintRed = Paint().apply {
            textSize  = 16f
            typeface  = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color     = Color.parseColor("#D32F2F")
        }
        val paintLine = Paint().apply {
            color       = Color.parseColor("#E0E0E0")
            strokeWidth = 1f
        }
        val paintBgGreen = Paint().apply {
            color = Color.parseColor("#E8F5E9")
            style = Paint.Style.FILL
        }
        val paintBgRed = Paint().apply {
            color = Color.parseColor("#FFEBEE")
            style = Paint.Style.FILL
        }

        // ── Title ────────────────────────────────────────────
        canvas.drawText("Ksheera-Sagara", margin, y, paintTitle)
        y += 26f
        canvas.drawText("Monthly Financial Summary — $month", margin, y, paintBody)
        y += 16f
        canvas.drawText("Farmer: $farmerName   |   Farm: $farmName", margin, y, paintSmall)
        y += 8f
        canvas.drawLine(margin, y, width - margin, y, paintLine)
        y += 20f

        // ── Key Numbers ──────────────────────────────────────
        val totalIncome   = milkEntries.sumOf { it.totalIncome }
        val totalExpenses = expenses.sumOf { it.amount }
        val totalLiters   = milkEntries.sumOf { it.liters }
        val profitPerLitr = if (totalLiters > 0) netProfit / totalLiters else 0.0

        canvas.drawText("KEY NUMBERS", margin, y, paintHeader)
        y += 18f

        fun drawKeyRow(label: String, value: String, paint: Paint) {
            canvas.drawText(label, margin, y, paintBody)
            canvas.drawText(value, width - margin - paint.measureText(value), y, paint)
            y += 20f
        }

        drawKeyRow("Total Milk Income",    "Rs. ${"%.2f".format(totalIncome)}",   paintGreen)
        drawKeyRow("Total Expenses",       "Rs. ${"%.2f".format(totalExpenses)}", paintRed)
        drawKeyRow("Total Liters Sold",    "${"%.1f".format(totalLiters)} L",     paintBody)
        drawKeyRow("Profit Per Liter",     "Rs. ${"%.2f".format(profitPerLitr)}", if(profitPerLitr >= 0) paintGreen else paintRed)

        // Net Profit Box
        y += 6f
        val boxColor = if (netProfit >= 0) paintBgGreen else paintBgRed
        canvas.drawRoundRect(margin, y, width - margin, y + 44f, 8f, 8f, boxColor)
        val profitLabel = if (netProfit >= 0) "NET PROFIT" else "NET LOSS"
        val profitPaint = if (netProfit >= 0) paintGreen else paintRed
        canvas.drawText(profitLabel, margin + 14f, y + 27f, profitPaint)
        val profitStr = "Rs. ${"%.2f".format(Math.abs(netProfit))}"
        canvas.drawText(profitStr, width - margin - profitPaint.measureText(profitStr) - 14f, y + 27f, profitPaint)
        y += 56f

        canvas.drawLine(margin, y, width - margin, y, paintLine)
        y += 20f

        // ── Expense Breakdown ────────────────────────────────
        canvas.drawText("EXPENSE BREAKDOWN", margin, y, paintHeader)
        y += 18f

        val categoryTotals = expenses
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        categoryTotals.forEach { (cat, amount) ->
            val pct = if (totalExpenses > 0) (amount / totalExpenses * 100) else 0.0
            canvas.drawText(cat.name, margin, y, paintBody)
            val valStr = "Rs. ${"%.0f".format(amount)}  (${"%.1f".format(pct)}%)"
            canvas.drawText(valStr, width - margin - paintBody.measureText(valStr), y, paintBody)
            y += 18f

            // Mini bar
            val barWidth = ((width - margin * 2) * (amount / totalExpenses)).toFloat().coerceAtMost(width - margin * 2)
            val barPaint = Paint().apply {
                color = when (cat) {
                    ExpenseCategory.FODDER      -> Color.parseColor("#43A047")
                    ExpenseCategory.MEDICAL     -> Color.parseColor("#E53935")
                    ExpenseCategory.LABOR       -> Color.parseColor("#1E88E5")
                    ExpenseCategory.ELECTRICITY -> Color.parseColor("#FB8C00")
                    else                        -> Color.parseColor("#8E24AA")
                }
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(margin, y, margin + barWidth, y + 8f, 4f, 4f, barPaint)
            y += 16f
        }

        y += 4f
        canvas.drawLine(margin, y, width - margin, y, paintLine)
        y += 20f

        // ── Cow-wise Summary ─────────────────────────────────
        canvas.drawText("COW-WISE SUMMARY", margin, y, paintHeader)
        y += 18f

        val cowGroups = milkEntries.groupBy { it.cowName }
        cowGroups.forEach { (cowName, entries) ->
            val liters = entries.sumOf { it.liters }
            val income = entries.sumOf { it.totalIncome }
            canvas.drawText(cowName, margin, y, paintBody)
            val cowStr = "${"%.1f".format(liters)} L   Rs. ${"%.0f".format(income)}"
            canvas.drawText(cowStr, width - margin - paintBody.measureText(cowStr), y, paintBody)
            y += 18f
        }

        y += 4f
        canvas.drawLine(margin, y, width - margin, y, paintLine)
        y += 20f

        // ── Milk Entries Table ───────────────────────────────
        canvas.drawText("MILK ENTRIES", margin, y, paintHeader)
        y += 18f

        canvas.drawText("Date", margin, y, paintSmall)
        canvas.drawText("Cow", margin + 90f, y, paintSmall)
        canvas.drawText("Liters", margin + 190f, y, paintSmall)
        canvas.drawText("Fat%", margin + 260f, y, paintSmall)
        canvas.drawText("SNF%", margin + 320f, y, paintSmall)
        canvas.drawText("Amount", margin + 390f, y, paintSmall)
        y += 14f
        canvas.drawLine(margin, y, width - margin, y, paintLine)
        y += 12f

        milkEntries.take(15).forEach { e ->
            if (y > 760f) return@forEach
            canvas.drawText(e.date,                             margin,        y, paintSmall)
            canvas.drawText(e.cowName.take(12),                 margin + 90f,  y, paintSmall)
            canvas.drawText("${"%.1f".format(e.liters)}",       margin + 190f, y, paintSmall)
            canvas.drawText("${e.fatPercent}",                  margin + 260f, y, paintSmall)
            canvas.drawText("${e.snfPercent}",                  margin + 320f, y, paintSmall)
            canvas.drawText("Rs.${"%.0f".format(e.totalIncome)}",margin + 390f, y, paintSmall)
            y += 16f
        }

        // ── Footer ───────────────────────────────────────────
        canvas.drawText(
            "Generated by Ksheera-Sagara App",
            margin, 820f, paintSmall
        )

        document.finishPage(page)

        // Save file
        val pdfDir  = File(context.filesDir, "pdfs").also { it.mkdirs() }
        val pdfFile = File(pdfDir, "KsheeraSagara_${month}.pdf")
        pdfFile.outputStream().use { document.writeTo(it) }
        document.close()

        return pdfFile
    }
}