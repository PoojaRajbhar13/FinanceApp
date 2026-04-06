package com.example.projectz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.projectz.domain.model.Category
import com.example.projectz.domain.model.Transaction
import com.example.projectz.domain.model.TransactionType
import com.example.projectz.presentation.theme.ExpenseRed
import com.example.projectz.presentation.theme.IncomeGreen
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit = {},
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { 
                if (onEditClick != null || onDeleteClick != null) {
                    expanded = true
                } else {
                    onClick()
                }
            }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(transaction.category),
                    contentDescription = transaction.category.name,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.notes.ifEmpty { transaction.category.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) } },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            val amountColor = if (transaction.type == TransactionType.INCOME) IncomeGreen else MaterialTheme.colorScheme.onSurface
            val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"
            
            Text(
                text = "$sign₹${String.format(Locale.US, "%.2f", transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (onEditClick != null) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = { expanded = false; onEditClick() })
                }
                if (onDeleteClick != null) {
                    DropdownMenuItem(text = { Text("Delete") }, onClick = { expanded = false; onDeleteClick() })
                }
            }
        }
    }
}
}

fun getCategoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.FOOD -> Icons.Default.Restaurant
        Category.TRANSPORT -> Icons.Default.DirectionsCar
        Category.ENTERTAINMENT -> Icons.Default.Movie
        Category.SHOPPING -> Icons.Default.ShoppingCart
        Category.BILLS -> Icons.Default.Receipt
        Category.SALARY -> Icons.Default.AttachMoney
        Category.FREELANCE -> Icons.Default.Work
        Category.SAVINGS -> Icons.Default.Savings
        Category.OTHER -> Icons.Default.MoreHoriz
    }
}
