package com.solidad.moolamigo.ui.screens.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.ui.theme.newgreen

data class AdminReportSummary(
    val totalUsers: Int,
    val totalTransactions: Int,
    val totalIncome: Double,
    val totalExpenses: Double
)

data class CategoryStat(
    val name: String,
    val totalAmount: Double,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportScreen(navController: NavController) {
    val summary = AdminReportSummary(
        totalUsers = 3,
        totalTransactions = 20,
        totalIncome = 20000.0,
        totalExpenses = 7000.0
    )

    val categoryStats = listOf(
        CategoryStat("Food", 150000.0, Color(0xFFE57373)),
        CategoryStat("Transport", 80000.0, Color(0xFF64B5F6)),
        CategoryStat("Clothing", 65000.0, Color(0xFF81C784)),
        CategoryStat("Health", 35000.0, Color(0xFFBA68C8)),
        CategoryStat("Education", 25600.0, Color(0xFFFFD54F))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Admin Financial Report", fontWeight = FontWeight.Bold, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = newgreen
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("System Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            SummaryCard("Total Users", summary.totalUsers.toString(), Color(0xFFE0F7FA))
            SummaryCard("Transactions", summary.totalTransactions.toString(), Color(0xFFD1C4E9))
            SummaryCard("Total Income", "Ksh${summary.totalIncome}", Color(0xFFC8E6C9))
            SummaryCard("Total Expenses", "Ksh${summary.totalExpenses}", Color(0xFFFFCDD2))

            Spacer(modifier = Modifier.height(24.dp))

            Text("Top Spending Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            PieChart(categoryStats)

            Spacer(modifier = Modifier.height(24.dp))

            categoryStats.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category.name, fontSize = 16.sp)
                    Text("Ksh${category.totalAmount}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PieChart(data: List<CategoryStat>) {
    val total = data.sumOf { it.totalAmount }
    if (total == 0.0) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("No data available")
        }
        return
    }

    val proportions = data.map { it.totalAmount / total }
    val angles = proportions.map { it * 360f }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = -90f
            for (i in data.indices) {
                drawArc(
                    color = data[i].color,
                    startAngle = startAngle,
                    sweepAngle = angles[i].toFloat(),
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
                startAngle += angles[i].toFloat()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialReportScreenPreview() {
    FinancialReportScreen(rememberNavController())
}
