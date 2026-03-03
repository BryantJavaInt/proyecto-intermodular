package com.example.inmobiliacontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inmobiliacontrol.ui.home.HomeScreen
import com.example.inmobiliacontrol.ui.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                composable("login") {
                    LoginScreen(
                        onLoggedIn = { role ->
                            navController.navigate("home/${role.name}") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "home/{role}",
                    arguments = listOf(navArgument("role") { type = NavType.StringType })
                ) { backStackEntry ->

                    val roleStr = backStackEntry.arguments?.getString("role") ?: Role.TENANT.name
                    val role = Role.valueOf(roleStr)

                    HomeScreen(
                        role = role,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("home/{role}") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}