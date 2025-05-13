package com.solidad.harakamall.ui.screens.about


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.navigation.ROUT_BUDGET
import com.solidad.moolamigo.navigation.ROUT_LOG
import com.solidad.moolamigo.navigation.ROUT_REPORT
import com.solidad.moolamigo.navigation.ROUT_USER
import com.solidad.moolamigo.ui.theme.newgreen


data class User(
    val id: Int,
    val name: String,
    val email: String
)

class AdminViewModel : ViewModel() {
    // Mocked user list
    var users = mutableStateListOf(
        User(1, "Alice Smith", "alice@example.com"),
        User(2, "Bob Johnson", "bob@example.com"),
        User(3, "Carol Davis", "carol@example.com")
    )
        private set

    fun deleteUser(user: User) {
        users.remove(user)
    }
}




// Budgeting theme colors
val MintGreen = Color(0xFFE4FDE1)
val MintGreenDark = Color(0xFF32B768)
val MintCardColor = Color(0xFFD3F2DC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MoolaMigo Admin",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = newgreen
                )
            )
        },
        containerColor = MintGreen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Admin Tools",
                fontSize = 20.sp,
                color = MintGreenDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AdminOptionCard(
                title = "User Accounts",
                icon = Icons.Default.Person,
                color = MintCardColor
            ) {
                navController.navigate(ROUT_USER)
            }

            AdminOptionCard(
                title = "Transaction Logs",
                icon = Icons.Default.AccountCircle,
                color = MintCardColor
            ) {
                navController.navigate(ROUT_LOG)
            }

            AdminOptionCard(
                title = "Budget Categories",
                icon = Icons.Default.Menu,
                color = MintCardColor
            ) {
                navController.navigate(ROUT_BUDGET)
            }

            AdminOptionCard(
                title = "Financial Reports",
                icon = Icons.Default.Star,
                color = MintCardColor
            ) {
                navController.navigate(ROUT_REPORT)
            }
        }
    }
}

@Composable
fun AdminOptionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MintGreenDark,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(navController = rememberNavController())
}
