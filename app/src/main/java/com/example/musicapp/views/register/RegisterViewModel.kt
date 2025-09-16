package com.example.musicapp.views.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.RetrofitInstance
import com.example.musicapp.model.AuthRequest
import com.example.musicapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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

    // Validate dữ liệu nhập vào trước khi gọi Firebase
    fun validateAndRegister(email: String, password: String, confirmPassword: String, username: String) {
        when {
            email.isBlank() || password.isBlank() || confirmPassword.isBlank() || username.isBlank()-> {
                _registerState.value = RegisterState.Error("Vui lòng nhập đầy đủ các trường")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _registerState.value = RegisterState.Error("Email không hợp lệ")
            }
            password.length < 6 -> {
                _registerState.value = RegisterState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            }
            password != confirmPassword -> {
                _registerState.value = RegisterState.Error("Mật khẩu xác nhận không khớp")
            }
            else -> {
                registerWithEmail(email, password, username)
            }
        }
    }

    //Xử lý đăng ký với email và password
    fun registerWithEmail(email: String, password: String, username: String) {
        _registerState.value = RegisterState.Loading
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let {
                    registerWithFirebase(
                        uid = it.uid,
                        email = it.email ?: "",
                        name = username,
                        avatar = it.photoUrl?.toString()
                    )
                }
            }
            .addOnFailureListener {
                _registerState.value = RegisterState.Error("Registration failed: ${it.message}")
            }
    }

    //Gọi API tới BE sau khi xác thực firebase
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
