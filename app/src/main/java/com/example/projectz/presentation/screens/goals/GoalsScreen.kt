package com.example.projectz.presentation.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import com.example.projectz.presentation.components.AddGoalDialog
import com.example.projectz.presentation.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val goal by viewModel.goalState.collectAsState()
    val isEditOpen by viewModel.isEditDialogVisible.collectAsState()
    val isDeleteOpen by viewModel.showDeleteDialog.collectAsState()
    val isEditAmountOpen by viewModel.showEditAmountDialog.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (isEditOpen) {
        AddGoalDialog(
            initialGoal = goal,
            onDismissRequest = { viewModel.hideEditDialog() },
            onSaveGoal = { title, targetAmount, dailyAmount, deadline -> 
                if (goal == null) {
                    viewModel.createGoal(title, targetAmount, dailyAmount, deadline)
                } else {
                    viewModel.updateGoal(title, targetAmount, dailyAmount, deadline)
                }
            }
        )
    }

    if (isDeleteOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteCancel() },
            title = { Text("Delete Goal") },
            text = { Text("Are you sure you want to delete this goal? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDeleteConfirm() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDeleteCancel() }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (isEditAmountOpen && goal != null) {
        EditSavedAmountDialog(
            currentAmount = goal!!.currentAmount,
            targetAmount = goal!!.targetAmount,
            onDismiss = { viewModel.hideEditAmountDialog() },
            onSave = { newAmount -> viewModel.updateSavedAmount(newAmount) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Savings Challenge") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {
                    if (goal != null) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Goal") },
                                onClick = { 
                                    showMenu = false
                                    viewModel.showEditDialog() 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Goal", color = MaterialTheme.colorScheme.error) },
                                onClick = { 
                                    showMenu = false
                                    viewModel.triggerDeleteDialog() 
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            goal?.let { g ->
                Text(
                    text = g.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                GoalCircularProgress(
                    progress = g.progress,
                    targetAmount = g.targetAmount,
                    currentAmount = g.currentAmount,
                    onEditAmount = { viewModel.triggerEditAmountDialog() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GoalMetric(title = "Days Left", value = if (g.daysLeft >= 0) "${g.daysLeft}" else "-")
                    GoalMetric(title = "Required / Day", value = "₹${String.format(Locale.US, "%.0f", g.requiredPerDay)}")
                }

                Spacer(modifier = Modifier.height(32.dp))

                val isSavedToday = g.lastSavedDate?.isEqual(java.time.LocalDate.now()) == true
                Button(
                    onClick = { viewModel.addDailyAmount() },
                    enabled = !isSavedToday,
                    modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
                ) {
                    Text(
                        text = if (isSavedToday) "Safely Saved Today \uD83D\uDC4D" else "Add Today's Amount (₹${String.format(Locale.US, "%.0f", g.dailyGoalAmount)})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                StreakCard(streakDays = g.streakDays, isActive = g.isStreakActive)
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(), 
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No savings goal yet. Start saving today! \uD83C\uDFAF",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.showEditDialog() }) {
                            Text("Create New Goal")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalMetric(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun GoalCircularProgress(
    progress: Float,
    targetAmount: Double,
    currentAmount: Double,
    onEditAmount: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(1500),
        label = "progress"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.size(240.dp)) {
            drawArc(
                color = trackColor,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = primaryColor,
                startAngle = 140f,
                sweepAngle = 260f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${String.format(Locale.US, "%.0f", currentAmount)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onEditAmount, modifier = Modifier.size(24.dp).padding(start = 4.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Amount", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }
            Text(
                text = "of ₹${String.format(Locale.US, "%.0f", targetAmount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EditSavedAmountDialog(
    currentAmount: Double,
    targetAmount: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(currentAmount.toString().removeSuffix(".0")) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Saved Amount") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                            amountText = it
                            isError = false
                        }
                    },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newAmt = amountText.toDoubleOrNull()
                if (newAmt == null || newAmt < 0) {
                    isError = true
                    errorMessage = "Invalid amount"
                } else if (newAmt > targetAmount) {
                    isError = true
                    errorMessage = "Cannot exceed target (₹${targetAmount})"
                } else {
                    onSave(newAmt)
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun StreakCard(streakDays: Int, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Saving Streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isActive) "You're on fire! Keep it up." else "Save today to start a streak",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (isActive) Color(0xFFF97316) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$streakDays Days",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color(0xFFF97316) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
