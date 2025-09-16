package com.example.musicapp.views.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.RetrofitInstance
import com.example.musicapp.model.AuthRequest
import com.example.musicapp.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun registerWithFirebase(uid: String, email: String, name: String?, avatar: String?) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val req = AuthRequest(uid, email, name, avatar)
                val res = RetrofitInstance.api.authWithFirebase(req)
                if (res.success && res.user != null) {
                    _registerState.value = RegisterState.Success(res.user)
                } else {
                    _registerState.value = RegisterState.Error("Registration failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _registerState.value = RegisterState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}
