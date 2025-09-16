package com.example.musicapp.views.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.musicapp.views.login.ForgotPasswordState
import com.example.musicapp.views.login.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    viewModel: LoginViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val forgotState by viewModel.forgotPasswordState.collectAsState()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Reset Password") },
        text = {
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent, // nền trong suốt
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.resetPassword(email)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00BBF9),
                    contentColor = Color.White
                )
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00BBF9),
                    contentColor = Color.White
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )

    // Observe state result
    when (forgotState) {
        is ForgotPasswordState.Success -> {
            Toast.makeText(LocalContext.current, "Check your email to reset password", Toast.LENGTH_LONG).show()
            onDismiss()
        }
        is ForgotPasswordState.Error -> {
            Toast.makeText(LocalContext.current, (forgotState as ForgotPasswordState.Error).message, Toast.LENGTH_LONG).show()
        }
        else -> {}
    }
}


