package com.example.musicapp.views.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.RetrofitInstance
import com.example.musicapp.model.AuthRequest
import com.example.musicapp.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

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
}
