package com.sibsutis.chat.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibsutis.chat.entities.Conversation
import com.sibsutis.chat.firebase.FirebaseSource
import com.sibsutis.chat.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseSource: FirebaseSource,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        const val maxEmailLength = 40
        const val maxNameLength = 30
        const val maxPasswordLength = 20
    }

    val logInEmail: MutableState<String> = mutableStateOf("")

    val logInPassword: MutableState<String> = mutableStateOf("")

    val email: MutableState<String> = mutableStateOf("")

    val emailPrompt: MutableState<String> = mutableStateOf("")

    val name: MutableState<String> = mutableStateOf("")

    val namePrompt: MutableState<String> = mutableStateOf("")

    val password: MutableState<String> = mutableStateOf("")

    val passwordPrompt: MutableState<String> = mutableStateOf("")

    val failureText: MutableState<String> = mutableStateOf("")

    val successText: MutableState<String> = mutableStateOf("")



    val conversation: MutableState<Conversation?> = mutableStateOf(null)

    val avatar: MutableState<Bitmap?> = mutableStateOf(null)

    val message: MutableState<String> = mutableStateOf("")

    fun getIndexOfFirstError(): Int {
        val prompts = arrayOf(
            namePrompt.value,
            emailPrompt.value,
            passwordPrompt.value
        )
        return prompts.indexOfFirst { it.isNotBlank() }
    }

    fun logIn(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val email = logInEmail.value.trim()
        val password = logInPassword.value.trim()
        firebaseSource.logIn(email, password)
            .onSuccess {
                withContext(Dispatchers.Main) { onSuccess() }
            }
            .onFailure { throwable ->
                throwable.cause?.let {
                    withContext(Dispatchers.Main) { onFailure(it) }
                }
            }
    }

    fun signUp(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val email = email.value.trim()
        val password = password.value.trim()
        firebaseSource.signUp(email, password)
            .onSuccess {
                withContext(Dispatchers.Main) { onSuccess() }
            }
            .onFailure { throwable ->
                throwable.cause?.let {
                    withContext(Dispatchers.Main) { onFailure(it) }
                }
            }
    }
}