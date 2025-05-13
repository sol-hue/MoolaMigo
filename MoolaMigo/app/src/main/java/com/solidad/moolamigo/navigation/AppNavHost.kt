package com.solidad.moolamigo.navigation

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solidad.harakamall.ui.screens.about.AdminScreen
import com.solidad.harakamall.ui.screens.about.AdminViewModel
import com.solidad.moolamigo.data.UserDatabase
import com.solidad.moolamigo.repository.UserRepository
import com.solidad.moolamigo.ui.screens.about.AboutScreen
import com.solidad.moolamigo.ui.screens.auth.LoginScreen
import com.solidad.moolamigo.ui.screens.budget.BudgetCategoryScreen
import com.solidad.moolamigo.ui.screens.contact.ContactScreen
import com.solidad.moolamigo.ui.screens.home.HomeScreen
import com.solidad.moolamigo.ui.screens.home.ProfileScreen
import com.solidad.moolamigo.ui.screens.incomeandexpendicture.IncomeandExpenditureScreen
import com.solidad.moolamigo.ui.screens.manage.ManageUsersScreen
import com.solidad.moolamigo.ui.screens.manage.ReviewScreen
import com.solidad.moolamigo.ui.screens.profile.EditprofileScreen
import com.solidad.moolamigo.ui.screens.report.FinancialReportScreen
import com.solidad.moolamigo.ui.screens.splash.SplashScreen
import com.solidad.moolamigo.ui.screens.transaction.GoalScreen
import com.solidad.moolamigo.ui.screens.transaction.TransactionLogScreen
import com.solidad.moolamigo.ui.screens.transaction.TransactionScreen
import com.solidad.moolamigo.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_HOME) {
            HomeScreen(navController = navController, userName = "")
        }

        composable(ROUT_ABOUT) {
            AboutScreen(navController)
        }
        composable(ROUT_CONTACT) {
            ContactScreen(navController)
        }
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }

        composable(ROUT_BUDGET) {
            BudgetCategoryScreen(navController)
        }

        composable(ROUT_REPORT) {
            FinancialReportScreen(navController)
        }

        composable(ROUT_LOG) {
            TransactionLogScreen( navController) // Pass both the ViewModel and NavController
        }







        composable(ROUT_USER) {
            val navController = rememberNavController() // Initialize the NavController
            val viewModel: AdminViewModel = viewModel() // Retrieve the ViewModel

            ManageUsersScreen(viewModel = viewModel, navController = navController) // Pass both the ViewModel and NavController
        }




        composable(ROUT_TRANSACTION) {
            TransactionScreen(navController)
        }

        composable(ROUT_GOAL) {
           GoalScreen(navController)
        }

        composable(ROUT_INCOME) {
            IncomeandExpenditureScreen(navController)
        }
        composable(ROUT_PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUT_EDIT) {
            EditprofileScreen(navController)
        }
        composable(ROUT_ADMIN) {
            AdminScreen(navController)
        }




        composable(ROUT_REVIEW) {
            val context = LocalContext.current
            ReviewScreen(navController)
        }







        //AUTHENTICATION

        // Initialize Room Database and Repository for Authentication
        val appDatabase = UserDatabase.getDatabase(context)
        val authRepository = UserRepository(appDatabase.userDao())
        val authViewModel: AuthViewModel = AuthViewModel(authRepository)
        composable(ROUT_REGISTER) {
            RegisterScreen(authViewModel, navController) {
                navController.navigate(ROUT_LOGIN) {
                    popUpTo(ROUT_REGISTER) { inclusive = true }
                }
            }
        }

        composable(ROUT_LOGIN) {
            LoginScreen(authViewModel, navController) {
                navController.navigate(ROUT_HOME) {
                    popUpTo(ROUT_LOGIN) { inclusive = true }
                }
            }
        }



    }
}



