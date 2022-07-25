package com.sibsutis.chat.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.sibsutis.chat.R
import com.sibsutis.chat.viewmodels.AuthViewModel
import java.util.concurrent.TimeoutException

@Composable
@OptIn(ExperimentalComposeUiApi::class)// TODO: ВМЕСТО ADJUST_RESIZE сделать скролл на экран
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val (first, second, third) = FocusRequester.createRefs()
    var failureText by remember { viewModel.failureText }// TODO: rememberSaveable
    var successText by remember { viewModel.successText }

    ConstraintLayout(
        constraintSet = constraints(),
        modifier = Modifier.fillMaxSize()
    ) {
        AppBar { navController.popBackStack() }
        Animation()
        Description()
        Column(modifier = Modifier
            .layoutId("name")
            .fillMaxWidth(0.75f)
        ) {
            NameField(first, viewModel)
        }
        Column(modifier = Modifier
            .layoutId("email")
            .fillMaxWidth(0.75f)
        ) {
            EmailField(second, viewModel)
        }
        Column(modifier = Modifier
            .layoutId("password")
            .fillMaxWidth(0.75f)
        ) {
            PasswordField(third, viewModel)
        }
        SignUpButton {
            validateFields(context, viewModel)
            val i = viewModel.getIndexOfFirstError()
            if (i != -1) {
                arrayOf(first, second, third)[i].requestFocus()
            }
            else signUp(context, viewModel)
        }

        if (failureText.isNotBlank()) {
            Failure(failureText) { failureText = "" }
        }

        if (successText.isNotBlank()) {
            Success(successText) {
                successText = ""
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun AppBar(onClick: () -> (Unit)) {
    SmallTopAppBar(
        modifier = Modifier.layoutId("appBar"),
        title = {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.sign_up)
            )
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
private fun Animation() {
    val spec = LottieCompositionSpec.RawRes(R.raw.email)
    val composition by rememberLottieComposition(spec)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.75f
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .fillMaxSize(0.2f)
            .layoutId("animation")
    )
}

@Composable
private fun Description() {
    Text(
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        text = stringResource(R.string.sign_up_description),
        modifier = Modifier
            .layoutId("description")
            .padding(start = 20.dp)
            .width(0.dp)
    )
}

@Composable
private fun NameField(
    focusRequester: FocusRequester,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var text by remember { viewModel.name }
    var errorMessage by remember { viewModel.namePrompt }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= AuthViewModel.maxNameLength) {
                text = it
            }
        },
        keyboardActions = KeyboardActions(onNext = {
            errorMessage = validateName(context, text)
            focusManager.moveFocus(FocusDirection.Down)
        }),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        label = {
            Text(text = stringResource(R.string.name_hint))
        },
        isError = errorMessage.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )

    if (errorMessage.isNotBlank()) {
        Text(
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
            text = errorMessage
        )
    }
}

@Composable
private fun EmailField(
    focusRequester: FocusRequester,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var text by remember { viewModel.email }
    var errorMessage by remember { viewModel.emailPrompt }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= AuthViewModel.maxEmailLength) {
                text = it
            }
        },
        keyboardActions = KeyboardActions(onNext = {
            errorMessage = validateEmail(context, text)
            focusManager.moveFocus(FocusDirection.Down)
        }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        ),
        singleLine = true,
        label = {
            Text(text = stringResource(R.string.email_hint))
        },
        isError = errorMessage.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )

    if (errorMessage.isNotBlank()) {
        Text(
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
            text = errorMessage
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun PasswordField(
    focusRequester: FocusRequester,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { viewModel.password }
    var errorMessage by remember { viewModel.passwordPrompt }
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= AuthViewModel.maxPasswordLength) {
                text = it
            }
        },
        keyboardActions = KeyboardActions(onDone = {
            errorMessage = validatePassword(context, text)
            keyboardController?.hide()
        }),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        label = {
            Text(text = stringResource(R.string.create_password_hint))
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
        isError = errorMessage.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )

    if (errorMessage.isNotBlank()) {
        Text(
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
            text = errorMessage
        )
    }
}

@Composable
private fun SignUpButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.layoutId("button")
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.sign_up),
        )
    }
}

@Composable
private fun Failure(text: String, onClick: () -> Unit) {
    AlertDialog(
        title = {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (icon, title) = createRefs()

                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = stringResource(R.string.error),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(36.dp)
                        .constrainAs(icon) {
                            centerVerticallyTo(title)
                            end.linkTo(title.start, 10.dp)
                        }
                )
                Text(
                    text = stringResource(R.string.error),
                    modifier = Modifier.constrainAs(title) {
                        centerHorizontallyTo(parent)
                    }
                )
            }
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = text
            )
        },
        onDismissRequest = onClick,
        confirmButton = {
            Button(onClick = onClick) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.ok),
                )
            }
        }
    )
}

@Composable
private fun Success(text: String, onClick: () -> Unit) {
    AlertDialog(
        title = {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (icon, title) = createRefs()

                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = stringResource(R.string.email_hint),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .constrainAs(icon) {
                            centerVerticallyTo(title)
                            end.linkTo(title.start, 10.dp)
                        }
                )
                Text(
                    text = stringResource(R.string.account_created),
                    modifier = Modifier.constrainAs(title) {
                        centerTo(parent)
                    }
                )
            }
        },
        text = {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = text
            )
        },
        onDismissRequest = onClick,
        confirmButton = {
            Button(onClick = onClick) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.ok),
                )
            }
        }
    )
}

private fun constraints() = ConstraintSet {
    val appBar = createRefFor("appBar")
    val animation = createRefFor("animation")
    val description = createRefFor("description")
    val name = createRefFor("name")
    val email = createRefFor("email")
    val password = createRefFor("password")
    val button = createRefFor("button")

    constrain(appBar) {
        centerHorizontallyTo(parent)
        top.linkTo(parent.top)
    }
    constrain(animation) {
        start.linkTo(name.start)
        top.linkTo(appBar.bottom)
        bottom.linkTo(name.top)
    }
    constrain(description) {
        width = Dimension.fillToConstraints
        start.linkTo(animation.end)
        end.linkTo(name.end)
        top.linkTo(animation.top)
        bottom.linkTo(animation.bottom)
    }
    constrain(name) {
        centerHorizontallyTo(parent)
        bottom.linkTo(email.top, 15.dp)
    }
    constrain(email) {
        centerHorizontallyTo(parent)
        top.linkTo(appBar.bottom)
        bottom.linkTo(parent.bottom)
    }
    constrain(password) {
        centerHorizontallyTo(parent)
        top.linkTo(email.bottom, 15.dp)
    }
    constrain(button) {
        centerHorizontallyTo(parent)
        top.linkTo(password.bottom)
        bottom.linkTo(parent.bottom)
    }
}

private fun signUp(context: Context, viewModel: AuthViewModel) {
    viewModel.signUp({
        viewModel.successText.value = context.getString(R.string.confirmation_description)
    }, {
        val resId = when (it) {
            is FirebaseAuthUserCollisionException -> R.string.email_already_in_use
            is TimeoutException -> R.string.time_out
            else -> R.string.registration_failed// TODO: Доделать строку
        }
        viewModel.failureText.value = context.getString(resId)
    })
}

private fun validateFields(context: Context, viewModel: AuthViewModel) {
    viewModel.namePrompt.value = validateName(context, viewModel.name.value)
    viewModel.emailPrompt.value = validateEmail(context, viewModel.email.value)
    viewModel.passwordPrompt.value = validatePassword(context, viewModel.password.value)
}

private fun validateName(context: Context, text: String): String {
    val regex = "^([\\p{L}\\-'.]+\\p{Z}?){1,6}$".toRegex()// TODO: Проверить
    return when {
        text.isBlank() -> context.getString(R.string.field_is_required)
        regex matches text.trim() -> ""
        else -> context.getString(R.string.incorrect_name)
    }
}

private fun validateEmail(context: Context, text: String): String {
    val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
    return when {
        text.isBlank() -> context.getString(R.string.field_is_required)
        regex matches text.trim()-> ""
        else -> context.getString(R.string.incorrect_email)
    }
}

private fun validatePassword(context: Context, text: String): String {
    val regex = "^(?=.*[a-zA-Z])(?=\\S+\$).{8,}\$".toRegex()
    return when {
        text.isBlank() -> context.getString(R.string.field_is_required)
        text.length < 8 -> context.getString(R.string.password_requirements)
        regex matches text.trim() -> ""
        else -> context.getString(R.string.incorrect_password)
    }
}