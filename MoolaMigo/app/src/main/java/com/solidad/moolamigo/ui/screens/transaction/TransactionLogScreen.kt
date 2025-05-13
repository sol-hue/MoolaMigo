package com.solidad.moolamigo.ui.screens.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.ui.theme.newgreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Transaction(
    val title: String,
    val category: String,
    val amount: Double,
    val date: Date
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionLogScreen(navController: NavController) {
    val transactionList = listOf(
        Transaction("Grocery Shopping", "Food", 54.00, Date()),
        Transaction("Bus Ticket", "Transport", 80.00, Date()),
        Transaction("New Shoes", "Clothing", 750.00, Date()),
        Transaction("Lunch", "Food", 120.00, Date()),
        Transaction("Uber Ride", "Transport", 225.00, Date())
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Logs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newgreen
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(transactionList) { transaction ->
                TransactionCard(transaction)
            }
        }
    }
}

@Composable

fun TransactionCard(transaction: Transaction) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    // Format amount to two decimal places with "Ksh"
    val formattedAmount = "Ksh%.2f".format(transaction.amount)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)) // Light pastel color
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedAmount,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amount < 0) Color.Red else Color(0xFF2E7D32) // green for income
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Category: ${transaction.category}", color = Color.Gray)
            Text(text = dateFormatter.format(transaction.date), fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TransactionLogScreenPreview() {
    TransactionLogScreen(rememberNavController())
}
