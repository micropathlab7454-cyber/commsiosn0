package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.PdfReportGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LabViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = LabRepository(database.labDao())

    // --- Authentication State ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    val usernameInput = MutableStateFlow("")
    val passwordInput = MutableStateFlow("")

    // --- Navigation ---
    private val _currentTab = MutableStateFlow("Home")
    val currentTab = _currentTab.asStateFlow()

    // --- Search, Sort & Filters ---
    val entriesSearchText = MutableStateFlow("")
    val entriesFilterMonth = MutableStateFlow("") // format: "yyyy-MM", empty means All
    val entriesSortColumn = MutableStateFlow("Date") // Date, PatientName, Age, Test, Amount, DoctorAmount, Other
    val entriesSortAscending = MutableStateFlow(false)

    val doctorsSearchText = MutableStateFlow("")

    // --- Reports Screen State ---
    val reportsSelectedMonth = MutableStateFlow("") // format: "yyyy-MM"
    val reportsSelectedDoctorId = MutableStateFlow<Int?>(null)
    private val _generatedPdfFile = MutableStateFlow<File?>(null)
    val generatedPdfFile = _generatedPdfFile.asStateFlow()

    // --- Active Form/Dialog State ---
    val showAddEntryDialog = MutableStateFlow(false)
    val activeEntryToEdit = MutableStateFlow<Entry?>(null)

    val showAddDoctorDialog = MutableStateFlow(false)
    val activeDoctorToEdit = MutableStateFlow<Doctor?>(null)

    val showDeleteConfirmEntry = MutableStateFlow<Entry?>(null)
    val showDeleteConfirmDoctor = MutableStateFlow<Doctor?>(null)

    // --- Settings UI Inputs ---
    val settingsUsernameInput = MutableStateFlow("")
    val settingsPasswordInput = MutableStateFlow("")

    // --- Database Streams ---
    val allEntries = repository.allEntries.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allDoctors = repository.allDoctors.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val settings = repository.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), LabSettings()
    )

    // --- Reactive Commission Calculations for Doctors ---
    val doctorCommissions = allEntries.map { entries ->
        entries.filter { it.doctorId != null }
            .groupBy { it.doctorId!! }
            .mapValues { (_, entryList) -> entryList.sumOf { it.doctorAmount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // --- Reactive Filtered & Sorted Entries ---
    val filteredEntries = combine(
        allEntries,
        entriesSearchText,
        entriesFilterMonth,
        entriesSortColumn,
        entriesSortAscending
    ) { entries, search, month, sortCol, ascending ->
        var list = entries

        // Filter by month (yyyy-MM)
        if (month.isNotEmpty()) {
            list = list.filter { it.date.startsWith(month) }
        }

        // Search by patient name or date
        if (search.isNotEmpty()) {
            list = list.filter {
                it.patientName.contains(search, ignoreCase = true) ||
                it.date.contains(search, ignoreCase = true) ||
                it.test.contains(search, ignoreCase = true)
            }
        }

        // Sort columns
        list = when (sortCol) {
            "Date" -> if (ascending) list.sortedBy { it.date } else list.sortedByDescending { it.date }
            "PatientName" -> if (ascending) list.sortedBy { it.patientName.lowercase() } else list.sortedByDescending { it.patientName.lowercase() }
            "Age" -> if (ascending) list.sortedBy { it.age } else list.sortedByDescending { it.age }
            "Test" -> if (ascending) list.sortedBy { it.test.lowercase() } else list.sortedByDescending { it.test.lowercase() }
            "Amount" -> if (ascending) list.sortedBy { it.amount } else list.sortedByDescending { it.amount }
            "DoctorAmount" -> if (ascending) list.sortedBy { it.doctorAmount } else list.sortedByDescending { it.doctorAmount }
            "Other" -> if (ascending) list.sortedBy { it.otherAmount } else list.sortedByDescending { it.otherAmount }
            else -> list
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Monthly Totals (for currently active filter month) ---
    val activeMonthTotals = combine(allEntries, entriesFilterMonth) { entries, activeMonth ->
        // Use either the selected filter month, or the current month if filter is clear
        val targetMonth = activeMonth.ifEmpty { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }
        val monthEntries = entries.filter { it.date.startsWith(targetMonth) }
        
        MonthTotals(
            totalEntries = monthEntries.size,
            totalAmount = monthEntries.sumOf { it.amount },
            totalDoctorAmount = monthEntries.sumOf { it.doctorAmount },
            totalOtherAmount = monthEntries.sumOf { it.otherAmount },
            monthLabel = getFormattedMonthLabel(targetMonth)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthTotals())

    // --- Filtered Entries Monthly Totals ---
    val filteredEntriesTotals = filteredEntries.map { list ->
        MonthTotals(
            totalEntries = list.size,
            totalAmount = list.sumOf { it.amount },
            totalDoctorAmount = list.sumOf { it.doctorAmount },
            totalOtherAmount = list.sumOf { it.otherAmount },
            monthLabel = ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthTotals())

    init {
        // Initialize default dates
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        entriesFilterMonth.value = currentMonth
        reportsSelectedMonth.value = currentMonth

        viewModelScope.launch {
            // Load and pre-populate default settings
            val currentSettings = repository.getSettingsDirect()
            settingsUsernameInput.value = currentSettings.username
            settingsPasswordInput.value = currentSettings.password
        }
    }

    // --- Navigation Functions ---
    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    // --- Authentication ---
    fun login() {
        viewModelScope.launch {
            val s = repository.getSettingsDirect()
            if (usernameInput.value == s.username && passwordInput.value == s.password) {
                _isLoggedIn.value = true
                _loginError.value = null
            } else {
                _loginError.value = "Invalid username or password"
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        usernameInput.value = ""
        passwordInput.value = ""
    }

    // --- Entries Actions ---
    fun addEntry(date: String, name: String, age: Int, test: String, amount: Double, doctorAmount: Double, otherAmount: Double, doctorId: Int?) {
        viewModelScope.launch {
            repository.insertEntry(
                Entry(
                    date = date,
                    patientName = name,
                    age = age,
                    test = test,
                    amount = amount,
                    doctorAmount = doctorAmount,
                    otherAmount = otherAmount,
                    doctorId = doctorId
                )
            )
            Toast.makeText(getApplication(), "Entry added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun editEntry(entry: Entry) {
        viewModelScope.launch {
            repository.updateEntry(entry)
            Toast.makeText(getApplication(), "Entry updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
            Toast.makeText(getApplication(), "Entry deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Doctors Actions ---
    fun addDoctor(name: String, phone: String) {
        viewModelScope.launch {
            repository.insertDoctor(Doctor(name = name, phone = phone))
            Toast.makeText(getApplication(), "Doctor added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun editDoctor(doctor: Doctor) {
        viewModelScope.launch {
            repository.updateDoctor(doctor)
            Toast.makeText(getApplication(), "Doctor updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteDoctor(doctor: Doctor) {
        viewModelScope.launch {
            // Null out doctorId from any associated entries to keep integrity
            val entriesWithDoctor = allEntries.value.filter { it.doctorId == doctor.id }
            for (entry in entriesWithDoctor) {
                repository.updateEntry(entry.copy(doctorId = null))
            }
            repository.deleteDoctor(doctor)
            Toast.makeText(getApplication(), "Doctor deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Settings Actions ---
    fun updateCredentials() {
        viewModelScope.launch {
            val current = repository.getSettingsDirect()
            if (settingsUsernameInput.value.isBlank() || settingsPasswordInput.value.isBlank()) {
                Toast.makeText(getApplication(), "Username and Password cannot be blank", Toast.LENGTH_SHORT).show()
                return@launch
            }
            repository.updateSettings(
                current.copy(
                    username = settingsUsernameInput.value,
                    password = settingsPasswordInput.value
                )
            )
            Toast.makeText(getApplication(), "Credentials updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val current = repository.getSettingsDirect()
            repository.updateSettings(current.copy(isDarkMode = isDark))
        }
    }

    fun backupDB() {
        val success = repository.backupDatabase(getApplication())
        if (success) {
            Toast.makeText(getApplication(), "Database backup successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(getApplication(), "Database backup failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreDB() {
        val success = repository.restoreDatabase(getApplication())
        if (success) {
            Toast.makeText(getApplication(), "Database restore successful! App will load restored data.", Toast.LENGTH_LONG).show()
            // Force reload database content
            viewModelScope.launch {
                // Re-initialize settings values
                val s = repository.getSettingsDirect()
                settingsUsernameInput.value = s.username
                settingsPasswordInput.value = s.password
            }
        } else {
            Toast.makeText(getApplication(), "Backup file not found or corrupted", Toast.LENGTH_SHORT).show()
        }
    }

    // --- PDF Report Management ---
    fun generatePdfReport() {
        val docId = reportsSelectedDoctorId.value
        val doc = allDoctors.value.find { it.id == docId }
        val docName = doc?.name ?: "All Doctors"
        val mY = reportsSelectedMonth.value

        val filteredReportEntries = allEntries.value.filter {
            it.date.startsWith(mY) && (docId == null || it.doctorId == docId)
        }

        val monthLabel = getFormattedMonthLabel(mY)
        val file = PdfReportGenerator.generateDoctorReport(
            context = getApplication(),
            doctorName = docName,
            monthYear = monthLabel,
            entries = filteredReportEntries
        )

        if (file != null) {
            _generatedPdfFile.value = file
            Toast.makeText(getApplication(), "PDF generated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(getApplication(), "Failed to generate PDF", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGeneratedPdf() {
        val file = _generatedPdfFile.value
        if (file != null) {
            PdfReportGenerator.openPdf(getApplication(), file)
        } else {
            Toast.makeText(getApplication(), "Please generate PDF report first", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareGeneratedPdf() {
        val file = _generatedPdfFile.value
        if (file != null) {
            PdfReportGenerator.sharePdf(getApplication(), file)
        } else {
            Toast.makeText(getApplication(), "Please generate PDF report first", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper to format "2026-06" to "June 2026"
    private fun getFormattedMonthLabel(yearMonth: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val date = parser.parse(yearMonth)
            if (date != null) formatter.format(date) else yearMonth
        } catch (e: Exception) {
            yearMonth
        }
    }
}

data class MonthTotals(
    val totalEntries: Int = 0,
    val totalAmount: Double = 0.0,
    val totalDoctorAmount: Double = 0.0,
    val totalOtherAmount: Double = 0.0,
    val monthLabel: String = ""
)
