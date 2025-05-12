@file:Suppress("AndroidUnresolvedRoomSqlReference")

package com.solidad.moolamigo.ui.screens.manage
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.navigation.ROUT_HOME
import com.solidad.moolamigo.navigation.ROUT_PROFILE
import com.solidad.moolamigo.ui.theme.MoolaMigoTheme
import com.solidad.moolamigo.ui.theme.newblue
import com.solidad.moolamigo.ui.theme.newgreen
import com.solidad.moolamigo.ui.theme.newgreen1
import com.solidad.moolamigo.ui.theme.newred

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavController) {
    val categoryTotals = mapOf(
        "Clothes" to 15f,
        "Food" to 25f,
        "Transport" to 3f
    )

    Scaffold(
        // TopBar
        topBar = {
            TopAppBar(
                title = { Text("MoolaMigo Review") },
                navigationIcon = {
                    IconButton(onClick = { ROUT_HOME }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")

                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newgreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        // BottomBar
        bottomBar = {
            NavigationBar(
                containerColor = newgreen
            ) {

                var selectedIndex: Int? = null

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUT_PROFILE)
                    }
                )

            }
        },


        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Balance Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                 }


            // Pie Chart
            Text(
                text = "Spending Breakdown",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(15.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            PieChart(categoryTotals)
        }
    }
}

@Composable
fun PieChart(data: Map<String, Float>) {
    val colors = listOf(newblue, newred, newgreen1)
    val total = data.values.sum()
    var startAngle = 0f

    Canvas(
        modifier = Modifier
            .size(250.dp)
            .padding(20.dp)
    ) {
        data.values.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(150f, 50f),
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
Spacer(modifier = Modifier.height(50.dp))
    // Legend
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        data.keys.forEachIndexed { index, label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 6.dp)
                        .background(colors[index % colors.size])
                )
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    MoolaMigoTheme {
        ReviewScreen(rememberNavController())
    }
}

