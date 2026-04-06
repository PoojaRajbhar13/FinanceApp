package com.example.projectz.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.projectz.domain.model.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    initialGoal: Goal? = null,
    onDismissRequest: () -> Unit,
    onSaveGoal: (title: String, targetAmount: Double, dailyAmount: Double, deadline: LocalDate?) -> Unit
) {
    var title by remember { mutableStateOf(initialGoal?.title ?: "") }
    var targetAmount by remember { mutableStateOf(initialGoal?.targetAmount?.toString()?.removeSuffix(".0") ?: "") }
    var dailyAmount by remember { mutableStateOf(initialGoal?.dailyGoalAmount?.toString()?.removeSuffix(".0") ?: "") }
    var deadline by remember { mutableStateOf<LocalDate?>(initialGoal?.deadline) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = if (initialGoal != null) "Edit Savings Goal" else "Create Savings Goal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What are you saving for?") },
                    placeholder = { Text("e.g. New Car, Vacation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) targetAmount = it 
                    },
                    label = { Text("Target Amount (₹)") },
                    placeholder = { Text("e.g. 50000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = dailyAmount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) dailyAmount = it 
                    },
                    label = { Text("Daily Saving Target (₹)") },
                    placeholder = { Text("e.g. 100") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = deadline?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text("Deadline (Optional)") },
                    placeholder = { Text("Select Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    deadline = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (isError) {
                    Text(
                        text = "Please fill required fields properly",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetAmount.toDoubleOrNull()
                    val daily = dailyAmount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target != null && target > 0) {
                        onSaveGoal(title.trim(), target, daily, deadline)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Save Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}
