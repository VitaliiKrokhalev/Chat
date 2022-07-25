package com.sibsutis.chat.firebase

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

class FirebaseSource {

    private companion object {
        private const val Timeout = 60L
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    suspend fun logIn(email: String, password: String) = coroutineScope {
        runCatching {
            val task = firebaseAuth.signInWithEmailAndPassword(email, password)
            val result = Tasks.await(task, Timeout, TimeUnit.SECONDS)
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            else result.user?.isEmailVerified?.let {// TODO: User = null кейсы
                if (!it) throw IllegalStateException("Email not verified")
            }
        }
    }

    suspend fun signUp(email: String, password: String) = coroutineScope {
        runCatching {
            val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val result = Tasks.await(task, Timeout, TimeUnit.SECONDS)
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            else result.user?.sendEmailVerification()
        }
    }

    suspend fun sendEmail() = coroutineScope {
        runCatching {
            firebaseAuth.currentUser?.sendEmailVerification()// TODO: null
        }
    }

//    private fun sendEmail(user: FirebaseUser) {
//        val task = user.sendEmailVerification()
//        Tasks.await(task, Timeout, TimeUnit.SECONDS)
////        if (!task.isSuccessful) {
////            task.exception?.let { throw it }
////        }
//    }
}