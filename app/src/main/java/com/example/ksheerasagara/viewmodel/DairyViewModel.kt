package com.example.ksheerasagara.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksheerasagara.data.db.AppDatabase
import com.example.ksheerasagara.data.model.*
import com.example.ksheerasagara.data.repository.DairyRepository
import com.example.ksheerasagara.utils.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CowSummary(
    val name: String,
    val totalLiters: Double,
    val totalIncome: Double,
    val profitEstimate: Double
)

class DairyViewModel(application: Application) : AndroidViewModel(application) {

    private val session = SessionManager(application)

    private val repo = DairyRepository(
        db     = AppDatabase.getDatabase(application),
        userId = session.getUserId()    // ← scoped to this user
    )

    private val _selectedMonth = MutableStateFlow(
        LocalDate.now().toString().substring(0, 7)
    )
    val selectedMonth: StateFlow<String> = _selectedMonth

    val allMilkEntries = repo.getAllMilkEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses = repo.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCows = repo.getAllCows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyIncome: StateFlow<Double> = _selectedMonth.flatMapLatest { month ->
        repo.getIncomeByMonth(month).map { it ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyExpenses: StateFlow<Double> = _selectedMonth.flatMapLatest { month ->
        repo.getTotalExpensesByMonth(month).map { it ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netProfit: StateFlow<Double> = combine(monthlyIncome, monthlyExpenses) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expenseCategoryBreakdown: StateFlow<Map<ExpenseCategory, Double>> =
        combine(allExpenses, _selectedMonth) { expenses, month ->
            expenses
                .filter { it.date.startsWith(month) }
                .groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val cowSummaries: StateFlow<List<CowSummary>> =
        combine(allMilkEntries, allExpenses, _selectedMonth) { milk, expenses, month ->
            val monthMilk     = milk.filter { it.date.startsWith(month) }
            val monthExpTotal = expenses.filter { it.date.startsWith(month) }.sumOf { it.amount }
            val totalIncome   = monthMilk.sumOf { it.totalIncome }

            monthMilk
                .groupBy { it.cowName }
                .map { (cowName, entries) ->
                    val litrs   = entries.sumOf { it.liters }
                    val income  = entries.sumOf { it.totalIncome }
                    val expShare = if (totalIncome > 0)
                        (income / totalIncome) * monthExpTotal else 0.0
                    CowSummary(
                        name           = cowName,
                        totalLiters    = litrs,
                        totalIncome    = income,
                        profitEstimate = income - expShare
                    )
                }
                .sortedByDescending { it.profitEstimate }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMonth(month: String) { _selectedMonth.value = month }

    fun addMilkEntry(entry: MilkEntry) =
        viewModelScope.launch { repo.insertMilk(entry) }
    fun deleteMilkEntry(entry: MilkEntry) =
        viewModelScope.launch { repo.deleteMilk(entry) }

    fun addExpense(expense: Expense) =
        viewModelScope.launch { repo.insertExpense(expense) }
    fun deleteExpense(expense: Expense) =
        viewModelScope.launch { repo.deleteExpense(expense) }

    fun addCow(cow: Cow) =
        viewModelScope.launch { repo.insertCow(cow) }
    fun deleteCow(cow: Cow) =
        viewModelScope.launch { repo.deleteCow(cow) }
}