package com.sibsutis.chat.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavController
import com.sibsutis.chat.R
import com.sibsutis.chat.activities.MainActivity.Screens
import com.sibsutis.chat.common.LetterTileGenerator
import com.sibsutis.chat.entities.Conversation
import com.sibsutis.chat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ConversationsScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val generator = LetterTileGenerator(context)
    viewModel.name.value = "Vitaliy"
    viewModel.avatar.value = generator.generate(
        context = context,
        userId = "User",
        letters = viewModel.name.value.take(1),
        width = 500,
        height = 500,
        dark = isSystemInDarkTheme()
    )
    val conversations = generateConversations(context, generator)
    val scope = rememberCoroutineScope()

    Menu(viewModel) { drawerState ->
        Column {
            AppBar {
                scope.launch {
                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                }
            }
            ConversationList(conversations) {
                viewModel.conversation.value = it
                navController.navigate(Screens.Messages) {
                    popUpTo(Screens.Messages)
                }
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
                text = stringResource(R.string.chats)
            )
        },
        navigationIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.menu)
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Menu(viewModel: AuthViewModel, content: @Composable (DrawerState) -> Unit) {
    val name = viewModel.name.value
    val avatar = viewModel.avatar.value!!// TODO
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContainerColor = Color.Transparent,
        drawerContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
            ) {
                Image(
                    bitmap = avatar.asImageBitmap(),
                    contentDescription = stringResource(R.string.avatar),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .layoutId("avatar")
                        .fillMaxWidth(0.4f)
                        .padding(top = 30.dp, bottom = 15.dp)
                        .clip(CircleShape)
                )
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = name
                )
            }
        },
        content = { content(drawerState) }
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ConversationList(
    conversations: List<Conversation>,
    onItemClick: (Conversation) -> (Unit)
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .layoutId("conversations")
        ) {
            items(conversations, key = { it.userId }) { conversation ->
                ConversationRow(conversation, onItemClick)
                if (conversation.userId != conversations.last().userId) {
                    Spacer(modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth(0.78f)
                        .height(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onItemClick: (Conversation) -> (Unit)
) {
    ConstraintLayout(
        constraintSet = constraints(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(conversation) }
    ) {
        Image(
            bitmap = conversation.avatar.asImageBitmap(),
            contentDescription = stringResource(R.string.avatar),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .layoutId("avatar")
                .fillMaxWidth(0.22f)
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                .clip(CircleShape)
        )
        Text(
            text = conversation.name,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .layoutId("name")
                .padding(start = 8.dp)
        )
        Text(
            text = conversation.lastMessage,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .layoutId("lastMessage")
                .padding(start = 8.dp)
        )
    }
}

private fun constraints() = ConstraintSet {
    val avatar = createRefFor("avatar")
    val name = createRefFor("name")
    val lastMessage = createRefFor("lastMessage")

    constrain(avatar) {
        start.linkTo(parent.start)
    }
    constrain(name) {
        start.linkTo(avatar.end)
        top.linkTo(avatar.top)
        bottom.linkTo(lastMessage.top)
    }
    constrain(lastMessage) {
        start.linkTo(avatar.end)
        top.linkTo(name.bottom)
        bottom.linkTo(avatar.bottom)
    }
}

@Composable
private fun generateConversations(context: Context, generator: LetterTileGenerator): List<Conversation> {
    val text = stringResource(R.string.names)
    val names = text.split("|")
    return names.map {
        val letters = it.split(" ")
            .joinToString("") { word -> word.take(1) }
        val avatar = generator.generate(
            context = context,
            userId = it,
            letters = letters,
            width = 500,
            height = 500,
            dark = isSystemInDarkTheme()
        )
        Conversation(
            userId = it,
            name = it,
            avatar = avatar,
            "Hello!"
        )
    }
}