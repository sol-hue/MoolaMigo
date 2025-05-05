package com.solidad.moolamigo.navigation

import GoalScreen
import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.data.UserDatabase
import com.solidad.moolamigo.repository.UserRepository
import com.solidad.moolamigo.ui.screens.about.AboutScreen
import com.solidad.moolamigo.ui.screens.auth.LoginScreen
import com.solidad.moolamigo.ui.screens.contact.ContactScreen
import com.solidad.moolamigo.ui.screens.home.HomeScreen
import com.solidad.moolamigo.ui.screens.splash.SplashScreen
import com.solidad.moolamigo.ui.screens.transaction.TransactionScreen
import com.solidad.moolamigo.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_GOAL
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




        composable(ROUT_TRANSACTION) {
            TransactionScreen(navController)
        }

        composable(ROUT_GOAL) {
           GoalScreen(navController)
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

@Composable
fun PlaceScreen(x0: NavHostController) {
    TODO("Not yet implemented")
}

