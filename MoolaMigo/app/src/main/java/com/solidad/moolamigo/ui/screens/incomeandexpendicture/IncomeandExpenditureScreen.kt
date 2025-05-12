package com.solidad.moolamigo.ui.screens.incomeandexpendicture

import android.app.Application
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solidad.moolamigo.ui.theme.newgreen
import kotlinx.coroutines.launch

// 1. Data Entity
@Entity(tableName = "income_expenditure")
data class IncomeExpenditureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Income" or "Expenditure"
    val amount: Double
)

// 2. DAO
@Dao
interface IncomeExpenditureDao {
    @Insert
    suspend fun insert(record: IncomeExpenditureEntity)

    @Query("SELECT * FROM income_expenditure")
    fun getAll(): LiveData<List<IncomeExpenditureEntity>>

    @Query("SELECT SUM(CASE WHEN type = 'Income' THEN amount ELSE 0 END) FROM income_expenditure")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(CASE WHEN type = 'Expenditure' THEN amount ELSE 0 END) FROM income_expenditure")
    fun getTotalExpenditure(): LiveData<Double?>

    @Delete
    suspend fun delete(record: IncomeExpenditureEntity)
}

// 3. Database
@Database(entities = [IncomeExpenditureEntity::class], version = 1)
abstract class IncomeExpenditureDatabase : RoomDatabase() {
    abstract fun dao(): IncomeExpenditureDao

    companion object {
        @Volatile private var INSTANCE: IncomeExpenditureDatabase? = null
        fun getDatabase(context: Context): IncomeExpenditureDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    IncomeExpenditureDatabase::class.java,
                    "income_expenditure_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

// 4. ViewModel
class IncomeExpenditureViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = IncomeExpenditureDatabase.getDatabase(application).dao()
    val records = dao.getAll()
    val totalIncome = dao.getTotalIncome()
    val totalExpenditure = dao.getTotalExpenditure()

    fun addRecord(type: String, amount: Double) {
        viewModelScope.launch {
            dao.insert(IncomeExpenditureEntity(type = type, amount = amount))
        }
    }

    fun deleteRecord(record: IncomeExpenditureEntity) {
        viewModelScope.launch {
            dao.delete(record)
        }
    }
}

// 5. UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeandExpenditureScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: IncomeExpenditureViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IncomeExpenditureViewModel(context.applicationContext as Application) as T
        }
    })

    val records by viewModel.records.observeAsState(emptyList())
    val income by viewModel.totalIncome.observeAsState(0.0)
    val expenditure by viewModel.totalExpenditure.observeAsState(0.0)
    val balance = expenditure?.let { income!! - it }

    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Income") }
    var expandedCard by remember { mutableStateOf(false) } // To track card expansion

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income & Expenses") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newgreen)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = newgreen) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, selected = false, onClick = { navController.navigate("home") })
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, selected = false, onClick = { navController.navigate("profile") })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount in Ksh") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Income", "Expenditure").forEach { option ->
                    val isSelected = option == type
                    Button(
                        onClick = { type = option },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) newgreen else Color.LightGray,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(option)
                    }
                }
            }

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null) {
                        viewModel.addRecord(type, amt)
                        amount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = newgreen)
            ) {
                Text("Save Record")
            }

            Text("Total Income: Ksh.${String.format("%.2f", income)}", style = MaterialTheme.typography.titleMedium)
            Text("Total Expenditure: Ksh.${String.format("%.2f", expenditure)}", style = MaterialTheme.typography.titleMedium)
            Text("Balance: Ksh.${String.format("%.2f", balance)}", style = MaterialTheme.typography.titleLarge)

            // All records in one card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expandedCard = !expandedCard }, // Toggle expansion
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = newgreen)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Income & Expenditure Records")

                    // Show records inside the card
                    if (expandedCard) {
                        records.forEach { record ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${record.type}: Ksh.${String.format("%.2f", record.amount)}", style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = {
                                    viewModel.deleteRecord(record)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Record", tint = Color.Black)
                                }
                            }
                        }

                        // Show delete button when the card is expanded
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Delete all records
                                records.forEach { record ->
                                    viewModel.deleteRecord(record)
                                }
                                expandedCard = false // Collapse the card after deletion
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Delete All Records", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IncomeandExpenditureScreenPreview() {
    IncomeandExpenditureScreen(rememberNavController())
}
