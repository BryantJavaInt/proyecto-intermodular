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
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.ui.home.HomeScreen
import com.example.inmobiliacontrol.ui.login.LoginScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 PRUEBA DE BASE DE DATOS (TEMPORAL)
        lifecycleScope.launch {
            val db = InmobiliaDatabase.getInstance(applicationContext)
            val ticketDao = db.ticketDao()

            val ticket = Ticket(
                title = "Incidencia prueba",
                description = "Probando inserción desde MainActivity",
                category = "General",
                priority = "Alta",
                status = "Abierto",
                createdByUserId = 1
            )

            ticketDao.insertTicket(ticket)
        }

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