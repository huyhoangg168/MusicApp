package com.example.musicapp.utils

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.musicapp.R
import com.example.musicapp.views.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Helper class cho Google Sign-In
 */
object GoogleAuthHelper {

    /** Tạo GoogleSignInClient */
    fun getGoogleClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /** Xử lý kết quả login */
    fun handleResult(
        result: ActivityResult?,
        context: Context,
        viewModel: LoginViewModel
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
            viewModel.loginWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(context, "Google sign in error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Hook để dùng trong Composable (tạo client + launcher)
 */
@Composable
fun rememberGoogleAuthLauncher(
    viewModel: LoginViewModel
): Pair<GoogleSignInClient, ManagedActivityResultLauncher<android.content.Intent, ActivityResult>> {
    val context = LocalContext.current
    val googleClient = GoogleAuthHelper.getGoogleClient(context)

    // đây là @Composable nên KHÔNG bọc trong remember {}
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        GoogleAuthHelper.handleResult(result, context, viewModel)
    }

    return Pair(googleClient, launcher)
}
