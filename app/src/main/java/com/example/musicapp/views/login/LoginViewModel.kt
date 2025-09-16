package com.example.musicapp.views.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.RetrofitInstance
import com.example.musicapp.model.AuthRequest
import com.example.musicapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    //Sau khi firebase trả về uid xác thực thì gọi API
    fun loginWithFirebase(uid: String, email: String, name: String?, avatar: String?) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val req = AuthRequest(uid, email, name, avatar)
                val res = RetrofitInstance.api.authWithFirebase(req)
                if (res.success && res.user != null) {
                    _loginState.value = LoginState.Success(res.user)
                } else {
                    _loginState.value = LoginState.Error("Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

    //Xử lý đăng nhập với email và mật khẩu
    fun loginWithEmail(email: String, password: String) {
        _loginState.value = LoginState.Loading
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let {
                    loginWithFirebase(it.uid, it.email ?: "", it.displayName, it.photoUrl?.toString())
                }
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Login thất bại")
            }
    }

    //Xử lý đăng nhập bằng GG
    fun loginWithGoogle(account: GoogleSignInAccount) {
        _loginState.value = LoginState.Loading
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                result.user?.let {
                    loginWithFirebase(
                        uid = it.uid,
                        email = it.email ?: "",
                        name = it.displayName,
                        avatar = it.photoUrl?.toString()
                    )
                }
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Google login failed: ${it.message}")
            }
    }

    //Xử lý quên password
    fun resetPassword(email: String) {
        _forgotPasswordState.value = ForgotPasswordState.Loading
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _forgotPasswordState.value = ForgotPasswordState.Success
            }
            .addOnFailureListener {
                _forgotPasswordState.value = ForgotPasswordState.Error(it.message ?: "Unknown error")
            }
    }

    //Xử lý đăng nhập có validate
    fun validateAndLogin(email: String, password: String) {
        when {
            email.isBlank() || password.isBlank() -> {
                _loginState.value = LoginState.Error("Vui lòng nhập đầy đủ Email và Mật khẩu")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _loginState.value = LoginState.Error("Email không hợp lệ")
            }
            else -> {
                loginWithEmail(email, password)
            }
        }
    }


}
