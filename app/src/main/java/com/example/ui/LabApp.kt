package com.example.ui

import com.example.ui.theme.MyApplicationTheme
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Doctor
import com.example.data.Entry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabApp(viewModel: LabViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        LoginScreen(viewModel)
    } else {
        DashboardScreen(viewModel)
    }
}

// ==========================================
// 1. LOGIN SCREEN
// ==========================================
@Composable
fun LoginScreen(viewModel: LabViewModel) {
    val username by viewModel.usernameInput.collectAsStateWithLifecycle()
    val password by viewModel.passwordInput.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("login_card"),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon / Logo representation
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Biotech,
                            contentDescription = "Lab Biotech Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MICRO PATHOLOGY",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Laboratory Management Portal",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.usernameInput.value = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.passwordInput.value = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        viewModel.login()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )

                if (loginError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Default Credentials: admin / admin",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// ==========================================
// 2. DASHBOARD & ADAPTIVE NAVIGATION
// ==========================================
@Composable
fun DashboardScreen(viewModel: LabViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    // Dialog controllers
    val showAddEntry by viewModel.showAddEntryDialog.collectAsStateWithLifecycle()
    val showAddDoctor by viewModel.showAddDoctorDialog.collectAsStateWithLifecycle()
    val editingEntry by viewModel.activeEntryToEdit.collectAsStateWithLifecycle()
    val editingDoctor by viewModel.activeDoctorToEdit.collectAsStateWithLifecycle()

    val confirmDeleteEntry by viewModel.showDeleteConfirmEntry.collectAsStateWithLifecycle()
    val confirmDeleteDoctor by viewModel.showDeleteConfirmDoctor.collectAsStateWithLifecycle()

    MyApplicationTheme(darkTheme = settings.isDarkMode) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            Scaffold(
                bottomBar = {
                    if (!isWideScreen) {
                        BottomNavBar(selectedTab = currentTab, onTabSelected = { viewModel.setTab(it) })
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideScreen) {
                        SidebarNav(
                            selectedTab = currentTab,
                            onTabSelected = { viewModel.setTab(it) },
                            onLogout = { viewModel.logout() }
                        )
                    }

                    // Content panel
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentTab) {
                            "Home" -> HomePage(viewModel)
                            "Entries" -> EntriesPage(viewModel)
                            "Reports" -> ReportsPage(viewModel)
                            "Doctors" -> DoctorsPage(viewModel)
                            "Settings" -> SettingsPage(viewModel)
                        }
                    }
                }
            }
        }

        // --- Global Modals & Dialogs ---
        if (showAddEntry || editingEntry != null) {
            EntryDialog(viewModel = viewModel, entry = editingEntry)
        }
        if (showAddDoctor || editingDoctor != null) {
            DoctorDialog(viewModel = viewModel, doctor = editingDoctor)
        }

        // Delete Confirmations
        if (confirmDeleteEntry != null) {
            AlertDialog(
                onDismissRequest = { viewModel.showDeleteConfirmEntry.value = null },
                title = { Text("Delete Entry") },
                text = { Text("Are you sure you want to delete the entry of patient '${confirmDeleteEntry?.patientName}'? This action is irreversible.") },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmDeleteEntry?.let { viewModel.deleteEntry(it) }
                            viewModel.showDeleteConfirmEntry.value = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showDeleteConfirmEntry.value = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (confirmDeleteDoctor != null) {
            AlertDialog(
                onDismissRequest = { viewModel.showDeleteConfirmDoctor.value = null },
                title = { Text("Delete Doctor") },
                text = { Text("Are you sure you want to delete Dr. '${confirmDeleteDoctor?.name}'? Note: All associated entries will lose their doctor association.") },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmDeleteDoctor?.let { viewModel.deleteDoctor(it) }
                            viewModel.showDeleteConfirmDoctor.value = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showDeleteConfirmDoctor.value = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ==========================================
// 3. HOME PAGE (DASHBOARD)
// ==========================================
@Composable
fun HomePage(viewModel: LabViewModel) {
    val activeMonthTotals by viewModel.activeMonthTotals.collectAsStateWithLifecycle()
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Upper banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "Diagnostic and billing analytics for ${activeMonthTotals.monthLabel}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Button(
                onClick = { viewModel.showAddEntryDialog.value = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Entry")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Total Entries",
                value = activeMonthTotals.totalEntries.toString(),
                subtitle = "Active this month",
                icon = Icons.Default.Assessment,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Total Amount",
                value = String.format("₹%.2f", activeMonthTotals.totalAmount),
                subtitle = "Total revenue",
                icon = Icons.Default.CurrencyRupee,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Doctor Commission",
                value = String.format("₹%.2f", activeMonthTotals.totalDoctorAmount),
                subtitle = "Payout due",
                icon = Icons.Default.Group,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            DashboardCard(
                title = "Total Other Amount",
                value = String.format("₹%.2f", activeMonthTotals.totalOtherAmount),
                subtitle = "Supplies/Other",
                icon = Icons.Default.Payments,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Entries Preview
        Text(
            text = "Recent Billing Entries",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val recentList = remember(allEntries) { allEntries.take(5) }

        if (recentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No billing entries found. Click '+ Add Entry' to start.", color = Color.Gray)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    recentList.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = entry.patientName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${entry.date}  •  ${entry.test}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Text(
                                text = String.format("₹%.2f", entry.amount),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. ENTRIES PAGE (DATA TABLE)
// ==========================================
@Composable
fun EntriesPage(viewModel: LabViewModel) {
    val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val search by viewModel.entriesSearchText.collectAsStateWithLifecycle()
    val filterMonth by viewModel.entriesFilterMonth.collectAsStateWithLifecycle()
    val sortCol by viewModel.entriesSortColumn.collectAsStateWithLifecycle()
    val sortAsc by viewModel.entriesSortAscending.collectAsStateWithLifecycle()
    val totals by viewModel.filteredEntriesTotals.collectAsStateWithLifecycle()

    var showMonthDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header and Floating Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Billing Entries",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )

            FloatingActionButton(
                onClick = { viewModel.showAddEntryDialog.value = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search and filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.entriesSearchText.value = it },
                placeholder = { Text("Search by Patient Name, Date, or Test...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("entries_search_input")
            )

            // Month Filter
            Box {
                Button(
                    onClick = { showMonthDropdown = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (filterMonth.isEmpty()) "All Months" else filterMonth)
                }

                DropdownMenu(
                    expanded = showMonthDropdown,
                    onDismissRequest = { showMonthDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Months") },
                        onClick = {
                            viewModel.entriesFilterMonth.value = ""
                            showMonthDropdown = false
                        }
                    )
                    // Generate list of past 12 months dynamically for convenient choice
                    val calendar = Calendar.getInstance()
                    val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    for (i in 0 until 12) {
                        val mStr = formatter.format(calendar.time)
                        DropdownMenuItem(
                            text = { Text(mStr) },
                            onClick = {
                                viewModel.entriesFilterMonth.value = mStr
                                showMonthDropdown = false
                            }
                        )
                        calendar.add(Calendar.MONTH, -1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Responsive Table
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeaderCell("Date", "Date", sortCol, sortAsc, Modifier.weight(1.2f)) {
                        viewModel.entriesSortColumn.value = "Date"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Patient Name", "PatientName", sortCol, sortAsc, Modifier.weight(2.0f)) {
                        viewModel.entriesSortColumn.value = "PatientName"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Age", "Age", sortCol, sortAsc, Modifier.weight(0.8f)) {
                        viewModel.entriesSortColumn.value = "Age"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Test", "Test", sortCol, sortAsc, Modifier.weight(1.5f)) {
                        viewModel.entriesSortColumn.value = "Test"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Amount", "Amount", sortCol, sortAsc, Modifier.weight(1.2f)) {
                        viewModel.entriesSortColumn.value = "Amount"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Comm", "DoctorAmount", sortCol, sortAsc, Modifier.weight(1.2f)) {
                        viewModel.entriesSortColumn.value = "DoctorAmount"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    TableHeaderCell("Other", "Other", sortCol, sortAsc, Modifier.weight(1.0f)) {
                        viewModel.entriesSortColumn.value = "Other"
                        viewModel.entriesSortAscending.value = !sortAsc
                    }
                    // Space for Actions header
                    Text(
                        text = "Actions",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1.0f),
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                // Table Rows
                if (filteredEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No billing entries match your search criteria.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filteredEntries, key = { it.id }) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(entry.date, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.2f))
                                Text(entry.patientName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(2.0f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(entry.age.toString(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.8f))
                                Text(entry.test, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(String.format("₹%.2f", entry.amount), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.2f))
                                Text(String.format("₹%.2f", entry.doctorAmount), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.2f))
                                Text(String.format("₹%.2f", entry.otherAmount), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.0f))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.weight(1.0f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { viewModel.activeEntryToEdit.value = entry },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel.showDeleteConfirmEntry.value = entry },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        }
                    }
                }

                // --- Summary Row at the bottom of the table ---
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MONTHLY SUMMARY",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.weight(3.2f)
                        )
                        Text(
                            text = "Entries: ${totals.totalEntries}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(2.3f)
                        )
                        Text(
                            text = String.format("₹%.2f", totals.totalAmount),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = String.format("₹%.2f", totals.totalDoctorAmount),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = String.format("₹%.2f", totals.totalOtherAmount),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1.0f)
                        )
                        Spacer(modifier = Modifier.weight(1.0f)) // Empty block for actions column align
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(
    label: String,
    columnId: String,
    activeSortCol: String,
    ascending: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val isActive = activeSortCol == columnId
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        )
        if (isActive) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ==========================================
// 5. REPORTS PAGE
// ==========================================
@Composable
fun ReportsPage(viewModel: LabViewModel) {
    val doctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val selectedDoctorId by viewModel.reportsSelectedDoctorId.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.reportsSelectedMonth.collectAsStateWithLifecycle()
    val generatedPdf by viewModel.generatedPdfFile.collectAsStateWithLifecycle()

    var showDoctorDropdown by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }

    val activeDoctor = doctors.find { it.id == selectedDoctorId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Financial & Commission Reports",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
        )
        Text(
            text = "Configure and export doctor commission and lab accounting ledgers securely to PDF.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Report Configuration",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Select Doctor Input Row
                Text(
                    text = "Select Referral Doctor",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showDoctorDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeDoctor?.name ?: "All Referral Doctors")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showDoctorDropdown,
                        onDismissRequest = { showDoctorDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Referral Doctors") },
                            onClick = {
                                viewModel.reportsSelectedDoctorId.value = null
                                showDoctorDropdown = false
                            }
                        )
                        doctors.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text(doc.name) },
                                onClick = {
                                    viewModel.reportsSelectedDoctorId.value = doc.id
                                    showDoctorDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Month Input Row
                Text(
                    text = "Billing Month & Year",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showMonthDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedMonth)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showMonthDropdown,
                        onDismissRequest = { showMonthDropdown = false }
                    ) {
                        val calendar = Calendar.getInstance()
                        val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                        for (i in 0 until 12) {
                            val mStr = formatter.format(calendar.time)
                            DropdownMenuItem(
                                text = { Text(mStr) },
                                onClick = {
                                    viewModel.reportsSelectedMonth.value = mStr
                                    showMonthDropdown = false
                                }
                            )
                            calendar.add(Calendar.MONTH, -1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Generate trigger button
                Button(
                    onClick = { viewModel.generatePdfReport() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_report_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Commission Statement (PDF)")
                }
            }
        }

        // Display results block if PDF is generated
        if (generatedPdf != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Report Generated Successfully!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "File: ${generatedPdf?.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Size: ${String.format("%.2f KB", (generatedPdf?.length() ?: 0L) / 1024.0)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openGeneratedPdf() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open PDF")
                        }

                        Button(
                            onClick = { viewModel.shareGeneratedPdf() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share / Print")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. DOCTORS PAGE
// ==========================================
@Composable
fun DoctorsPage(viewModel: LabViewModel) {
    val doctors by viewModel.allDoctors.collectAsStateWithLifecycle()
    val search by viewModel.doctorsSearchText.collectAsStateWithLifecycle()
    val commissions by viewModel.doctorCommissions.collectAsStateWithLifecycle()

    val filteredDoctors = remember(doctors, search) {
        if (search.isEmpty()) doctors else doctors.filter { it.name.contains(search, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Referral Doctors",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "Manage partner referral accounts and view outstanding commission totals.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Button(
                onClick = { viewModel.showAddDoctorDialog.value = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Doctor")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.doctorsSearchText.value = it },
            placeholder = { Text("Search doctors by name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doctors_search_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (filteredDoctors.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No referral doctors found. Click 'Add Doctor' to register partnership.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDoctors, key = { it.id }) { doc ->
                    val commVal = commissions[doc.id] ?: 0.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = doc.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (doc.phone.isNotEmpty()) {
                                        Text(
                                            text = "Phone: ${doc.phone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text("Total Commission", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(
                                        text = String.format("₹%.2f", commVal),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = { viewModel.activeDoctorToEdit.value = doc }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Doctor", tint = MaterialTheme.colorScheme.primary)
                                }

                                IconButton(onClick = { viewModel.showDeleteConfirmDoctor.value = doc }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Doctor", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SETTINGS PAGE
// ==========================================
@Composable
fun SettingsPage(viewModel: LabViewModel) {
    val usernameInput by viewModel.settingsUsernameInput.collectAsStateWithLifecycle()
    val passwordInput by viewModel.settingsPasswordInput.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "System Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Visual Customization Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Aesthetics & Appearance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode Theme", fontWeight = FontWeight.SemiBold)
                        Text("Activate modern low-light laboratory visual styles", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }

                    Switch(
                        checked = settings.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backup Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Local Database Operations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Unify clinical logs and patient ledgers safely completely offline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.backupDB() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup Database")
                    }

                    Button(
                        onClick = { viewModel.restoreDB() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Database")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Security & Access",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { viewModel.settingsUsernameInput.value = it },
                    label = { Text("Change Username") },
                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { viewModel.settingsPasswordInput.value = it },
                    label = { Text("Change Password") },
                    leadingIcon = { Icon(Icons.Default.Password, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.updateCredentials() },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Credentials")
                }
            }
        }
    }
}

// ==========================================
// 8. FORMS & ADD/EDIT DIALOGS
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDialog(viewModel: LabViewModel, entry: Entry?) {
    val doctors by viewModel.allDoctors.collectAsStateWithLifecycle()

    var date by remember { mutableStateOf(entry?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var patientName by remember { mutableStateOf(entry?.patientName ?: "") }
    var ageStr by remember { mutableStateOf(entry?.age?.toString() ?: "") }
    var test by remember { mutableStateOf(entry?.test ?: "") }
    var amountStr by remember { mutableStateOf(entry?.amount?.toString() ?: "") }
    var doctorAmountStr by remember { mutableStateOf(entry?.doctorAmount?.toString() ?: "") }
    var otherAmountStr by remember { mutableStateOf(entry?.otherAmount?.toString() ?: "") }
    var selectedDoctorId by remember { mutableStateOf(entry?.doctorId) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDocDropdown by remember { mutableStateOf(false) }

    val activeDoctor = doctors.find { it.id == selectedDoctorId }

    AlertDialog(
        onDismissRequest = {
            viewModel.showAddEntryDialog.value = false
            viewModel.activeEntryToEdit.value = null
        },
        title = {
            Text(if (entry == null) "Add Diagnostic Entry" else "Edit Diagnostic Entry")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date picker trigger field
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Date (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false, // Force click on field wrapper
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Actual click target for Date Field wrapper
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .offset(y = (-68).dp)
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(-40.dp)) // Offset height of invisible box

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("patient_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = test,
                        onValueChange = { test = it },
                        label = { Text("Test") },
                        singleLine = true,
                        modifier = Modifier.weight(2f)
                    )
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Select Doctor for commission attribution
                Text(
                    text = "Referral Doctor Attribution",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showDocDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeDoctor?.name ?: "Direct Patient (No Referral)")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showDocDropdown,
                        onDismissRequest = { showDocDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Direct Patient (No Referral)") },
                            onClick = {
                                selectedDoctorId = null
                                showDocDropdown = false
                            }
                        )
                        doctors.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text(doc.name) },
                                onClick = {
                                    selectedDoctorId = doc.id
                                    showDocDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = doctorAmountStr,
                        onValueChange = { doctorAmountStr = it },
                        label = { Text("Doctor Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = otherAmountStr,
                        onValueChange = { otherAmountStr = it },
                        label = { Text("Other Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ageVal = ageStr.toIntOrNull() ?: 0
                    val amountVal = amountStr.toDoubleOrNull() ?: 0.0
                    val docAmountVal = doctorAmountStr.toDoubleOrNull() ?: 0.0
                    val otherAmountVal = otherAmountStr.toDoubleOrNull() ?: 0.0

                    if (patientName.isBlank() || test.isBlank()) {
                        return@Button
                    }

                    if (entry == null) {
                        viewModel.addEntry(
                            date = date,
                            name = patientName,
                            age = ageVal,
                            test = test,
                            amount = amountVal,
                            doctorAmount = docAmountVal,
                            otherAmount = otherAmountVal,
                            doctorId = selectedDoctorId
                        )
                    } else {
                        viewModel.editEntry(
                            entry.copy(
                                date = date,
                                patientName = patientName,
                                age = ageVal,
                                test = test,
                                amount = amountVal,
                                doctorAmount = docAmountVal,
                                otherAmount = otherAmountVal,
                                doctorId = selectedDoctorId
                            )
                        )
                    }

                    viewModel.showAddEntryDialog.value = false
                    viewModel.activeEntryToEdit.value = null
                },
                modifier = Modifier.testTag("submit_entry_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.showAddEntryDialog.value = false
                    viewModel.activeEntryToEdit.value = null
                }
            ) {
                Text("Cancel")
            }
        }
    )

    // Calendar Picker dialog wrapper
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            date = formatter.format(Date(selectedMillis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DoctorDialog(viewModel: LabViewModel, doctor: Doctor?) {
    var name by remember { mutableStateOf(doctor?.name ?: "") }
    var phone by remember { mutableStateOf(doctor?.phone ?: "") }

    AlertDialog(
        onDismissRequest = {
            viewModel.showAddDoctorDialog.value = false
            viewModel.activeDoctorToEdit.value = null
        },
        title = {
            Text(if (doctor == null) "Register Doctor Partnership" else "Edit Partner Details")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Doctor Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("doctor_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) return@Button

                    if (doctor == null) {
                        viewModel.addDoctor(name, phone)
                    } else {
                        viewModel.editDoctor(doctor.copy(name = name, phone = phone))
                    }

                    viewModel.showAddDoctorDialog.value = false
                    viewModel.activeDoctorToEdit.value = null
                },
                modifier = Modifier.testTag("submit_doctor_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.showAddDoctorDialog.value = false
                    viewModel.activeDoctorToEdit.value = null
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================
// 9. REUSABLE SMALL UI CHUNKS
// ==========================================
@Composable
fun SidebarNav(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp)
        ) {
            // App Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Biotech,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MICRO PATH",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Diagnostics Lab",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                }
            }

            // Tabs List
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SidebarItem("Home", Icons.Default.Dashboard, Icons.Outlined.Dashboard, selectedTab == "Home") { onTabSelected("Home") }
                SidebarItem("Entries", Icons.Default.Assessment, Icons.Outlined.Assessment, selectedTab == "Entries") { onTabSelected("Entries") }
                SidebarItem("Reports", Icons.Default.PictureAsPdf, Icons.Outlined.PictureAsPdf, selectedTab == "Reports") { onTabSelected("Reports") }
                SidebarItem("Doctors", Icons.Default.MedicalServices, Icons.Outlined.MedicalServices, selectedTab == "Doctors") { onTabSelected("Doctors") }
                SidebarItem("Settings", Icons.Default.Settings, Icons.Outlined.Settings, selectedTab == "Settings") { onTabSelected("Settings") }
            }

            // Logout item
            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Portal", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SidebarItem(
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("nav_item_${label.lowercase()}"),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) activeIcon else inactiveIcon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium)
            )
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar {
        BottomNavItem("Home", Icons.Default.Dashboard, selectedTab == "Home") { onTabSelected("Home") }
        BottomNavItem("Entries", Icons.Default.Assessment, selectedTab == "Entries") { onTabSelected("Entries") }
        BottomNavItem("Reports", Icons.Default.PictureAsPdf, selectedTab == "Reports") { onTabSelected("Reports") }
        BottomNavItem("Doctors", Icons.Default.MedicalServices, selectedTab == "Doctors") { onTabSelected("Doctors") }
        BottomNavItem("Settings", Icons.Default.Settings, selectedTab == "Settings") { onTabSelected("Settings") }
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isActive,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 11.sp, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("bottom_nav_${label.lowercase()}")
    )
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
