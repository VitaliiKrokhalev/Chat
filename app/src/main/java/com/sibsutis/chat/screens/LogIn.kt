package com.sibsutis.chat.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.sibsutis.chat.R
import com.sibsutis.chat.activities.MainActivity.Screens
import com.sibsutis.chat.viewmodels.AuthViewModel

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun LogInScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val (first, second) = FocusRequester.createRefs()

    ConstraintLayout(
        constraintSet = constraints(),
        modifier = Modifier.fillMaxSize()
    ) {
        Title()
        Animation()
        EmailField(first, viewModel)
        PasswordField(second, viewModel)
        LogInButton {
            if (viewModel.logInEmail.value.isBlank()) {
                first.requestFocus()
            }
            else if (viewModel.logInPassword.value.isBlank()) {
                second.requestFocus()
            }
            else {
                keyboardController?.hide()
                viewModel.logIn({// TODO:
                    navController.popBackStack()
                    navController.navigate(Screens.Conversations)
                }, {
                    // TODO:
                })
            }
        }
        Link {
            navController.navigate(Screens.SignUp) {
                popUpTo(Screens.LogIn)
            }
        }
    }
}

@Composable
private fun Title() {
    Text(
        modifier = Modifier.layoutId("title"),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        text = stringResource(R.string.log_in),
    )
}

@Composable
private fun Animation() {
    val spec = LottieCompositionSpec.RawRes(R.raw.chat)
    val composition by rememberLottieComposition(spec)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .fillMaxSize(0.3f)
            .layoutId("animation")
    )
}

@Composable
private fun EmailField(
    focusRequester: FocusRequester,
    viewModel: AuthViewModel
) {
    var text by remember { viewModel.logInEmail }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= AuthViewModel.maxEmailLength) {
                text = it
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Email
        ),
        singleLine = true,
        label = {
            Text(text = stringResource(R.string.email_hint))
        },
        modifier = Modifier
            .layoutId("email")
            .fillMaxWidth(0.75f)
            .focusRequester(focusRequester)
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun PasswordField(
    focusRequester: FocusRequester,
    viewModel: AuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { viewModel.logInPassword }
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= AuthViewModel.maxPasswordLength) {
                text = it
            }
        },
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        label = {
            Text(text = stringResource(R.string.password_hint))
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (visible) R.string.hide_password else R.string.show_password
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = image,
                    contentDescription = stringResource(description)
                )
            }
        },
        modifier = Modifier
            .layoutId("password")
            .fillMaxWidth(0.75f)
            .focusRequester(focusRequester)
    )
}

@Composable
private fun LogInButton(onClick: () -> (Unit)) {
    Button(
        onClick = onClick,
        modifier = Modifier.layoutId("button")
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.log_in)
        )
    }
}

@Composable
private fun Link(onClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(MaterialTheme.colorScheme.onSurface)) {
            append(text = stringResource(R.string.sign_up_link))
        }
        append("  ")
        pushStringAnnotation(
            tag = "SignUp",
            annotation = "SignUp"
        )
        withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) {
            append(text = stringResource(R.string.sign_up))
        }
    }

    ClickableText(
        modifier = Modifier.layoutId("link"),
        style = MaterialTheme.typography.bodyLarge,
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "SignUp",
                start = offset,
                end = offset
            ).forEach { _ ->
                onClick()
            }
        }
    )
}

private fun constraints() = ConstraintSet {
    val title = createRefFor("title")
    val animation = createRefFor("animation")
    val email = createRefFor("email")
    val password = createRefFor("password")
    val button = createRefFor("button")
    val link = createRefFor("link")

    constrain(title) {
        centerHorizontallyTo(parent)
        top.linkTo(parent.top, 40.dp)
    }
    constrain(animation) {
        centerHorizontallyTo(parent)
        top.linkTo(title.bottom)
        bottom.linkTo(email.top)
    }
    constrain(email) {
        centerHorizontallyTo(parent)
        bottom.linkTo(password.top, 15.dp)
    }
    constrain(password) {
        centerHorizontallyTo(parent)
        top.linkTo(title.bottom)
        bottom.linkTo(parent.bottom)
    }
    constrain(button) {
        centerHorizontallyTo(parent)
        top.linkTo(password.bottom, 30.dp)
    }
    constrain(link) {
        centerHorizontallyTo(parent)
        top.linkTo(button.bottom, 30.dp)
    }
}