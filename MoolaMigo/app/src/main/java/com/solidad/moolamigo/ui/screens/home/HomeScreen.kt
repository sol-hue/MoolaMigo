package com.solidad.moolamigo.ui.screens.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solidad.moolamigo.navigation.ROUT_ABOUT
import com.solidad.moolamigo.navigation.ROUT_GOAL
import com.solidad.moolamigo.navigation.ROUT_INCOME
import com.solidad.moolamigo.navigation.ROUT_PROFILE
import com.solidad.moolamigo.navigation.ROUT_REVIEW
import com.solidad.moolamigo.navigation.ROUT_TRANSACTION
import com.solidad.moolamigo.ui.theme.newgreen
import com.solidad.moolamigo.ui.theme.newgreen1
import kotlinx.coroutines.launch


@Entity(tableName = "balance_table")
data class Balance(
    @PrimaryKey val id: Int = 1,
    val amount: Double
)



@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance_table WHERE id = 1")
    suspend fun getBalance(): Balance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBalance(balance: Balance)
}



@Database(entities = [Balance::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceDao(): BalanceDao
}



class BalanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: BalanceDao

    var balance by mutableStateOf(0.0)
        private set

    init {
        val db = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "moolamigo-db"
        ).build()
        dao = db.balanceDao()

        viewModelScope.launch {
            val savedBalance = dao.getBalance()
            balance = savedBalance?.amount ?: 0.0
        }
    }

    fun updateBalance(newAmount: Double) {
        viewModelScope.launch {
            dao.insertOrUpdateBalance(Balance(amount = newAmount))
            balance = newAmount
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, userName: String, viewModel: BalanceViewModel = viewModel()) {
    val balance = viewModel.balance
    var isEditing by remember { mutableStateOf(false) }
    var newBalanceInput by remember { mutableStateOf(balance.toString()) }
    var selectedIndex by remember { mutableStateOf(0) }

    Button(onClick = {
        newBalanceInput.toDoubleOrNull()?.let {
            viewModel.updateBalance(it)
            isEditing = false
        }
    }) {
        Text("Save")
    }

    // Scaffold

    Scaffold(
        // TopBar
        topBar = {
            TopAppBar(
                title = { Text("MoolaMigo") },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(ROUT_ABOUT) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")

                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newgreen,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },

        // BottomBar
        bottomBar = {
            NavigationBar(
                containerColor = newgreen
            ) {

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Favorites") },
                    label = { Text("Review") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate(ROUT_REVIEW)
                    }
                )







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


        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Main Contents of the page

                Text(
                    text = "Hello, Solidad 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = newgreen1
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = newgreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditing) {
                            Column {
                                OutlinedTextField(
                                    value = newBalanceInput,
                                    onValueChange = { newBalanceInput = it },
                                    label = { Text("Enter new balance") },
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    Button(onClick = {
                                        newBalanceInput.toDoubleOrNull()?.let {
                                            viewModel.updateBalance(it)
                                            isEditing = false
                                        }
                                    }) {
                                        Text("Save")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { isEditing = false }) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ksh.${String.format("%.2f", balance)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Button(
                                    onClick = { isEditing = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Edit", color = MaterialTheme.colorScheme.onSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))




                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    DashboardButton("Transactions", navController, ROUT_TRANSACTION)
                    DashboardButton("Income", navController, ROUT_INCOME)
                    DashboardButton("Goals", navController, ROUT_GOAL)
                }



            }
        }
    )

    // End of scaffold

}

@Composable
fun DashboardButton(label: String, navController: NavController, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = newgreen
        ),
        modifier = Modifier
            .height(80.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController(), userName = "Solidad")
}
