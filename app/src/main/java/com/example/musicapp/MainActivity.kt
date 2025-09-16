package com.example.musicapp

import com.example.musicapp.navigationpackage.Screen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.views.login.LoginScreen
import com.example.musicapp.views.register.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onSignUpClick = { navController.navigate(Screen.Register.route) },
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onSignInClick = { navController.navigate(Screen.Login.route) },
//                onSignUpClick = {
//                    // TODO: gọi Firebase tạo tài khoản -> BE -> navigate Home nếu OK
//                }
            )
        }

        composable(Screen.Home.route) {
            // Sau khi login thành công thì navigate tới đây
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    androidx.compose.material3.Text("Welcome to Music App 🎵")
}
