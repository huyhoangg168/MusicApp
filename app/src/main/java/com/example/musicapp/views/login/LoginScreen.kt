package com.example.musicapp.views.login

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R
import com.example.musicapp.views.components.InputField
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicapp.navigationpackage.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(
    navController : NavController,
    viewModel: LoginViewModel = viewModel(),
    onSignUpClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

        // Forgot password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                color = Color.Blue,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }

        // Sign In button
        Button(
            onClick = {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val user = result.user
                        user?.let {
                            viewModel.loginWithFirebase(
                                uid = it.uid,
                                email = it.email ?: "",
                                name = it.displayName,
                                avatar = it.photoUrl?.toString()
                            )
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Login thất bại", Toast.LENGTH_SHORT).show()
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
            Text("SIGN IN", fontSize = 16.sp)
        }

        when (loginState) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Success -> Text("Xin chào ${(loginState as LoginState.Success).user.email}")
            is LoginState.Error -> Text((loginState as LoginState.Error).message, color = Color.Red)
            else -> {}
        }

        // Divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "OR",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        // Google Sign-In button
        OutlinedButton(
            onClick = { onGoogleSignInClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Icon",
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified // để giữ màu gốc của icon Google
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng nhập với Google")
        }

        // Sign Up link
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", color = Color.Gray)
            Text(
                text = "SIGN UP NOW",
                color = Color.Blue,
                modifier = Modifier.clickable {  navController.navigate(Screen.Register.route) }
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

    // Observe state
    when (loginState) {
        is LoginState.Loading -> CircularProgressIndicator()
        is LoginState.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        is LoginState.Error -> {
            Text((loginState as LoginState.Error).message, color = Color.Red)
        }
        else -> {}
    }
}

