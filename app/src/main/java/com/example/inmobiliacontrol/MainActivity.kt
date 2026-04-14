package com.example.inmobiliacontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.ui.home.HomeScreen
import com.example.inmobiliacontrol.ui.login.LoginScreen
import com.example.inmobiliacontrol.ui.ticket.CreateTicketScreen
import com.example.inmobiliacontrol.ui.ticket.TicketListScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seedPropertiesIfNeeded()

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                composable("login") {
                    LoginScreen(
                        onLoggedIn = { role, userId ->
                            navController.navigate("home/${role.name}/$userId") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "home/{role}/{userId}",
                    arguments = listOf(
                        navArgument("role") { type = NavType.StringType },
                        navArgument("userId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->

                    val roleStr = backStackEntry.arguments?.getString("role") ?: Role.TENANT.name
                    val role = Role.valueOf(roleStr)
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 1

                    HomeScreen(
                        role = role,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("home/{role}/{userId}") { inclusive = true }
                            }
                        },
                        onNavigateToTickets = {
                            navController.navigate("ticket_list/${role.name}/$userId")
                        },
                        onNavigateToCreateTicket = {
                            navController.navigate("create_ticket/${role.name}/$userId")
                        }
                    )
                }

                composable(
                    route = "ticket_list/{role}/{userId}",
                    arguments = listOf(
                        navArgument("role") { type = NavType.StringType },
                        navArgument("userId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val roleStr = backStackEntry.arguments?.getString("role") ?: Role.TENANT.name
                    val role = Role.valueOf(roleStr)
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 1

                    TicketListScreen(role = role, userId = userId)
                }

                composable(
                    route = "create_ticket/{role}/{userId}",
                    arguments = listOf(
                        navArgument("role") { type = NavType.StringType },
                        navArgument("userId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val roleStr = backStackEntry.arguments?.getString("role") ?: Role.TENANT.name
                    val role = Role.valueOf(roleStr)
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 1

                    CreateTicketScreen(
                        role = role,
                        userId = userId,
                        onSubmit = {
                            navController.navigate("ticket_list/${role.name}/$userId") {
                                popUpTo("create_ticket/${role.name}/$userId") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun seedPropertiesIfNeeded() {
        lifecycleScope.launch {
            val db = InmobiliaDatabase.getInstance(applicationContext)
            val propertyDao = db.propertyDao()

            val existingProperties = propertyDao.getAllProperties()
            if (existingProperties.isEmpty()) {
                propertyDao.insertProperty(
                    Property(
                        address = "Calle Mayor 10",
                        reference = "2ºB"
                    )
                )
                propertyDao.insertProperty(
                    Property(
                        address = "Avenida Constitución 25",
                        reference = "3ºA"
                    )
                )
                propertyDao.insertProperty(
                    Property(
                        address = "Plaza España 7",
                        reference = "Bajo"
                    )
                )
            }
        }
    }
}
