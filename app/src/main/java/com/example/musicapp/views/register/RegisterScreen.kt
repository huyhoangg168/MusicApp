package com.example.musicapp.views.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.musicapp.R
import com.example.musicapp.navigationpackage.Screen
import com.example.musicapp.views.components.InputField
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSignInClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo2_nobg),
            contentDescription = "Logo",
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 24.dp)
                .clip(CircleShape)
        )

        //Username field
        InputField(
            value = username,
            onValueChange = { username = it },
            placeholder = "Username",
            leadingIcon = R.drawable.ic_user
        )

        // Email field
        InputField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = R.drawable.ic_user
        )

        // Password field
        InputField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            leadingIcon = R.drawable.ic_lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggleClick = { passwordVisible = !passwordVisible }
        )

        // Confirm Password field
        InputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirm Password",
            leadingIcon = R.drawable.ic_lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggleClick = { passwordVisible = !passwordVisible }
        )

        // Sign Up button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            val user = result.user
                            user?.let {
                                viewModel.registerWithFirebase(
                                    uid = it.uid,
                                    email = it.email ?: "",
                                    name = username,
                                    avatar = it.photoUrl?.toString()
                                )
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00BBF9),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("SIGN UP", fontSize = 16.sp)
        }
        when (registerState) {
            is RegisterState.Loading -> CircularProgressIndicator()
            is RegisterState.Success -> Text("Đăng ký thành công!")
            is RegisterState.Error -> Text(
                (registerState as RegisterState.Error).message,
                color = Color.Red
            )
            else -> {}
        }

        // Sign In link
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                text = "SIGN IN",
                color = Color.Blue,
                modifier = Modifier.clickable {  navController.navigate(Screen.Login.route) }
            )
        }

        // Terms and Conditions
        Text(
            text = "Terms and Conditions",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    when (registerState) {
        is RegisterState.Loading -> CircularProgressIndicator()
        is RegisterState.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
        is RegisterState.Error -> {
            Text((registerState as RegisterState.Error).message, color = Color.Red)
        }
        else -> {}
    }
}
