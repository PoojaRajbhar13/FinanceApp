package com.example.projectz.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projectz.domain.model.Goal
import com.example.projectz.presentation.components.SectionHeader
import com.example.projectz.presentation.components.TransactionItem
import com.example.projectz.presentation.components.AddGoalDialog
import com.example.projectz.presentation.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToInsight: () -> Unit,
    onNavigateToEditTransaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.showAddGoalDialog) {
        AddGoalDialog(
            onDismissRequest = { viewModel.hideAddGoalDialog() },
            onSaveGoal = { title, targetAmount, dailyAmount, deadline -> 
                viewModel.saveGoal(title, targetAmount, dailyAmount, deadline) 
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {

        item {
            BalanceCard(
                balance = state.balance,
                income = state.totalIncome,
                expense = state.totalExpenses
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        state.primaryGoal?.let { goal ->
            item {
                SectionHeader(title = "Savings Goal", actionText = "Details", onActionClick = onNavigateToInsight)
                GoalProgressCard(goal)
                Spacer(modifier = Modifier.height(24.dp))
            }
        } ?: run {
            item {
                SectionHeader(title = "Savings Goal", actionText = "", onActionClick = {})
                CreateGoalPlaceholderCard(onClick = { viewModel.showAddGoalDialog() })
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            SectionHeader(
                title = "Recent Transactions",
                actionText = "See All",
                onActionClick = onNavigateToTransactions
            )
        }

        items(state.recentTransactions) { transaction ->
            TransactionItem(
                transaction = transaction,
                onEditClick = { onNavigateToEditTransaction(transaction.id) },
                onDeleteClick = { viewModel.deleteTransaction(transaction.id) }
            )
        }
    }
}

/*@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Hello, MERE GUGLE", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "Welcome back!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
        IconButton(
            onClick = { *//* Handle notification *//* },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}*/

@Composable
private fun BalanceCard(balance: Double, income: Double, expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Total Balance", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${String.format(Locale.US, "%.2f", balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BalanceMetric(title = "Income", amount = income, icon = Icons.Default.ArrowUpward, tint = Color.White)
                BalanceMetric(title = "Expense", amount = expense, icon = Icons.Default.ArrowDownward, tint = Color.White)
            }
        }
    }
}

@Composable
private fun BalanceMetric(title: String, amount: Double, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Text(text = "₹${String.format(Locale.US, "%.2f", amount)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun GoalProgressCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Target: ₹${String.format(Locale.US, "%.0f", goal.targetAmount)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun CreateGoalPlaceholderCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Goal",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Set a Savings Goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track what you're saving for",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
