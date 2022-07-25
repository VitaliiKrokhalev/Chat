package com.sibsutis.chat.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sibsutis.chat.screens.ConversationsScreen
import com.sibsutis.chat.screens.LogInScreen
import com.sibsutis.chat.screens.MessagesScreen
import com.sibsutis.chat.screens.SignUpScreen
import com.sibsutis.chat.ui.theme.AppTheme
import com.sibsutis.chat.ui.typography.appTypography
import com.sibsutis.chat.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object Screens {
        const val LogIn = "log_in"
        const val SignUp = "sign_up"
        const val Conversations = "conversations"
        const val Messages = "messages"
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            val navController = rememberNavController()
            AppTheme(typography = appTypography()) {
                NavHost(navController, LogIn) {
                    composable(LogIn) { LogInScreen(navController, viewModel) }
                    composable(SignUp) { SignUpScreen(navController, viewModel) }
                    composable(Conversations) {
                        ConversationsScreen(navController, viewModel)
                    }
                    composable(Messages) { MessagesScreen(navController, viewModel) }
                }
            }
        }
//        firebase.auth().tenantId = ‘TENANT_PROJECT_ID’; //TODO:
//
//// All future sign-in request now include tenant ID.
//        firebase.auth().signInWithEmailAndPassword(email, password)
//            .then(function(result) {
//                // result.user.tenantId should be ‘TENANT_PROJECT_ID’.
//            }).catch(function(error) {
//                // Handle error.
//            });
//        firebase.auth().createUserWithEmailAndPassword(email, password)
//            .catch(function(error) {
//                // Handle Errors here.
//                var errorCode = error.code;
//                var errorMessage = error.message;
//                if (errorCode == 'auth/weak-password') {
//                    alert('The password is too weak.');
//                } else {
//                    alert(errorMessage);
//                }
//                console.log(error);
//            });
    }
}