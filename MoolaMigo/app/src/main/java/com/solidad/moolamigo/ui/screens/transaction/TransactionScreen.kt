package com.solidad.moolamigo.ui.screens.transaction


import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solidad.moolamigo.navigation.ROUT_HOME
import com.solidad.moolamigo.navigation.ROUT_PROFILE
import com.solidad.moolamigo.ui.theme.newgreen
import com.solidad.moolamigo.ui.theme.newgreen1
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis() // <-- Add timestamp
)

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions")
    fun getAll(): LiveData<List<TransactionEntity>>

    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category")
    fun getAmountByCategory(): LiveData<List<CategoryTotal>>

    @Query("SELECT SUM(amount) FROM transactions")
    fun getTotalAmount(): LiveData<Double?>

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}


fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy - hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


data class CategoryTotal(
    val category: String,
    val total: Double
)


@Database(entities = [TransactionEntity::class], version = 2)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    companion object {
        @Volatile private var INSTANCE: TransactionDatabase? = null

        // MIGRATION from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: android.content.Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_db"
                )
                    .addMigrations(MIGRATION_1_2) // 👈 Add this line
                    .build().also { INSTANCE = it }
            }
        }
    }

}



val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE transactions ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0")
    }
}



class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TransactionDatabase.getDatabase(application).transactionDao()
    val transactions = dao.getAll()
    val total = dao.getTotalAmount()
    val categoryTotals = dao.getAmountByCategory()

    fun addTransaction(category: String, amount: Double) {
        viewModelScope.launch {
            dao.insert(TransactionEntity(category = category, amount = amount))
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            dao.delete(transaction)
        }




    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TransactionViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionViewModel(context.applicationContext as Application) as T
        }
    })

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    val categories = listOf("Food", "Transport", "Clothes")

    val transactions by viewModel.transactions.observeAsState(emptyList())
    val total by viewModel.total.observeAsState(0.0)
    val categoryTotals by viewModel.categoryTotals.observeAsState(emptyList())

    val mintGreen = newgreen

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var recentlyDeleted by remember { mutableStateOf<TransactionEntity?>(null) }

    var expandedCard by remember { mutableStateOf(false) } // To toggle the visibility of saved transactions

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoolaMigo", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newgreen)
            )
        },

        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        bottomBar = {
            NavigationBar(containerColor = newgreen) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUT_HOME)
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUT_PROFILE)
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Transaction", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Select a Category", style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Button(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) mintGreen else Color.LightGray,
                            contentColor = if (isSelected) Color.Black else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(category)
                    }
                }
            }

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt != null && selectedCategory.isNotBlank()) {
                        viewModel.addTransaction(selectedCategory, amt)
                        amount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = mintGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Transaction")
            }

            Text("Total Spent: Ksh.${String.format("%.2f", total)}", style = MaterialTheme.typography.titleMedium)

            Text("Spending by Category:", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                categoryTotals.forEach {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(it.category, style = MaterialTheme.typography.bodyMedium)
                        Text("Ksh.${String.format("%.2f", it.total)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expandedCard = !expandedCard },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = newgreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (expandedCard) "Tap to Hide Transactions" else "Tap to View All Transactions",
                        color = newgreen1,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (expandedCard) {
                        transactions.forEach { txn ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Ksh.${String.format("%.2f", txn.amount)}", style = MaterialTheme.typography.bodyLarge)
                                    Text("Category: ${txn.category}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Time: ${formatTimestamp(txn.timestamp)}", style = MaterialTheme.typography.bodySmall)
                                }

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            viewModel.deleteTransaction(txn)
                                            recentlyDeleted = txn

                                            coroutineScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Transaction deleted",
                                                    actionLabel = "Undo"
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    recentlyDeleted?.let {
                                                        viewModel.addTransaction(it.category, it.amount)
                                                    }
                                                    recentlyDeleted = null
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun TransactionPreview() {
    TransactionScreen(rememberNavController())
}
