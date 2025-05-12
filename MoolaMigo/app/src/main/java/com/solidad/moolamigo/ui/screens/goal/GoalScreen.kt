package com.solidad.moolamigo.ui.screens.transaction


import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
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
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solidad.moolamigo.navigation.ROUT_HOME
import com.solidad.moolamigo.navigation.ROUT_PROFILE
import com.solidad.moolamigo.ui.theme.newgreen
import kotlinx.coroutines.launch


@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double
)

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: GoalEntity)

    @Query("SELECT * FROM goals")
    fun getAllGoals(): LiveData<List<GoalEntity>>

    @Query("UPDATE goals SET savedAmount = savedAmount + :amount WHERE id = :goalId")
    suspend fun addSavedAmount(goalId: Int, amount: Double)
}

@Database(entities = [GoalEntity::class], version = 1)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun dao(): GoalDao

    companion object {
        @Volatile private var INSTANCE: GoalDatabase? = null
        fun getDatabase(context: Context): GoalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, GoalDatabase::class.java, "goal_db").build().also {
                    INSTANCE = it
                }
            }
        }
    }
}

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GoalDatabase.getDatabase(application).dao()
    val goals: LiveData<List<GoalEntity>> = dao.getAllGoals()

    fun addGoal(name: String, target: Double) {
        viewModelScope.launch {
            dao.insert(GoalEntity(name = name, targetAmount = target, savedAmount = 0.0))
        }
    }

    fun saveToGoal(goalId: Int, amount: Double) {
        viewModelScope.launch {
            dao.addSavedAmount(goalId, amount)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: GoalViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GoalViewModel(context.applicationContext as Application) as T
        }
    })

    val mintGreen = newgreen
    val goals by viewModel.goals.observeAsState(emptyList())
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saving Goals") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = mintGreen)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = mintGreen) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUT_HOME)
                    }
                )
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Goal Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text("Target Amount (Ksh)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    val targetAmount = target.toDoubleOrNull()
                    if (!name.isBlank() && targetAmount != null) {
                        viewModel.addGoal(name, targetAmount)
                        name = ""
                        target = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mintGreen)
            ) {
                Text("Add Goal")
            }

            goals.forEach { goal ->
                var saveInput by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(color = mintGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("🎯 ${goal.name}", style = MaterialTheme.typography.titleMedium)
                    Text("Target: Ksh. ${String.format("%.2f", goal.targetAmount)}")
                    Text("Saved: Ksh. ${String.format("%.2f", goal.savedAmount)}")

                    OutlinedTextField(
                        value = saveInput,
                        onValueChange = { saveInput = it },
                        label = { Text("Save Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            val amount = saveInput.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                viewModel.saveToGoal(goal.id, amount)
                                saveInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = mintGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add to Savings")
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalScreenPreview() {
    GoalScreen(navController = rememberNavController())
}
