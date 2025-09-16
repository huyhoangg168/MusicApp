package com.example.musicapp.navigationpackage

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home") // sau khi login thành công
}
