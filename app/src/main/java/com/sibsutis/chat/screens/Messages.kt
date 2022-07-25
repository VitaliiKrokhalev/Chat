package com.sibsutis.chat.screens

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.sibsutis.chat.R
import com.sibsutis.chat.entities.Conversation
import com.sibsutis.chat.entities.Message
import com.sibsutis.chat.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(navController: NavController, viewModel: AuthViewModel) {
    val conversation = viewModel.conversation.value!!// TODO
    val generated = generateMessages(conversation.userId).reversed()
    val messages = generated.toMutableStateList()
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(
        constraintSet = constraints(),
        modifier = Modifier.fillMaxSize()
    ) {
        val listState = rememberLazyListState()

        AppBar(conversation) {
            navController.popBackStack()
        }
        Background()
        MessagesList(listState, messages)
        MessageField(viewModel)

        val isBlank = viewModel.message.value.isBlank()
        if (isBlank) AttachButton {
            val id = messages.size.toString()
            val message = Message(
                topicId = conversation.userId,
                id = id,
                isMine = true,
                isMedia = true
            )
            messages.add(0, message)
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
        else SendButton {
            val text = viewModel.message.value
            val id = (text.hashCode() + messages.size).toString()
            val message = Message(conversation.userId, id, text, true)
            messages.add(0, message)
            viewModel.message.value = ""
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }
}

@Composable
private fun AppBar(conversation: Conversation, onClick: () -> (Unit)) {
    SmallTopAppBar(
        modifier = Modifier.layoutId("appBar"),
        title = { AppBarTitle(conversation) },
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
private fun AppBarTitle(conversation: Conversation) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            bitmap = conversation.avatar.asImageBitmap(),
            contentDescription = stringResource(R.string.avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(42.dp)// TODO
                .clip(CircleShape)
        )
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = conversation.name
        )
    }
}

@Composable
private fun Background() {
    Image(
        painter = painterResource(R.drawable.ic_background),
        contentDescription = stringResource(R.string.messages_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .layoutId("background")
            .fillMaxWidth()
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MessagesList(listState: LazyListState, messages: List<Message>) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("messages")
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }
            items(messages, key = { it.id }) { message ->
                if (message.isMedia) MediaRow() else MessageRow(message)
            }
            item { Spacer(modifier = Modifier.height(2.dp)) }
        }
    }
}

@Composable
private fun MessageRow(message: Message) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .clickable { /*TODO*/ }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Box(
            contentAlignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                text = message.content,
                modifier = Modifier
                    .background(
                        color = if (message.isMine) {
                            MaterialTheme.colorScheme.inversePrimary
                        } else MaterialTheme.colorScheme.onSecondary,
                        shape = if (message.isMine) {
                            RoundedCornerShape(15.dp, 15.dp, 5.dp, 15.dp)
                        } else RoundedCornerShape(15.dp, 15.dp, 15.dp, 5.dp)
                    )
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun MediaRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .clickable { /*TODO*/ }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Player()
        }
    }
}

@Composable
private fun MessageField(viewModel: AuthViewModel) {
    TextField(
        value = viewModel.message.value,
        onValueChange = {
            if (it.length <= 1000) viewModel.message.value = it
        },
        placeholder = {
            Text(text = stringResource(R.string.message_hint))
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Default,
            keyboardType = KeyboardType.Text
        ),
        maxLines = 6,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .layoutId("messageField")
            .fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, end = 50.dp, bottom = 5.dp)
    )
}

@Composable
private fun AttachButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .layoutId("attach")
            .padding(5.dp)
    ) {
        Icon(
            contentDescription = stringResource(R.string.attach),
            imageVector = Icons.Filled.AttachFile,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun SendButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .layoutId("send")
            .padding(5.dp)
    ) {
        Icon(
            contentDescription = stringResource(R.string.send),
            imageVector = Icons.Filled.Send,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun Player() {
    val context = LocalContext.current
    val url = stringResource(R.string.video_url)
    val uri = Uri.parse(url)
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            setMediaSource(source)
            prepare()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.inversePrimary,
                shape = RoundedCornerShape(15.dp, 15.dp, 5.dp, 15.dp)
            )
            .padding(15.dp)
            .aspectRatio(1.0f)
            .clip(shape = RoundedCornerShape(5.dp))
    )
}

private fun constraints() = ConstraintSet {
    val appBar = createRefFor("appBar")
    val background = createRefFor("background")
    val messages = createRefFor("messages")
    val messageField = createRefFor("messageField")
    val attach = createRefFor("attach")
    val send = createRefFor("send")

    constrain(appBar) {
        centerHorizontallyTo(parent)
        top.linkTo(parent.top)
    }
    constrain(background) {
        centerHorizontallyTo(parent)
        linkTo(top = appBar.bottom, bottom = parent.bottom)
        height = Dimension.fillToConstraints
    }
    constrain(messages) {
        centerHorizontallyTo(parent)
        top.linkTo(appBar.bottom)
        bottom.linkTo(messageField.top)
        height = Dimension.fillToConstraints
    }
    constrain(messageField) {
        start.linkTo(parent.start)
        bottom.linkTo(parent.bottom)
    }
    constrain(attach) {
        end.linkTo(parent.end, 4.dp)
        bottom.linkTo(parent.bottom, 4.dp)
    }
    constrain(send) {
        end.linkTo(parent.end, 4.dp)
        bottom.linkTo(parent.bottom, 4.dp)
    }
}

@Composable
private fun generateMessages(topicId: String): List<Message> {
    val text = stringResource(R.string.placeholder)
    val sentences = text.split(".")
    return sentences.mapIndexed { i, sentence ->
        val message = sentence.trim()
        val id = (message.hashCode() + i).toString()
        Message(topicId, id, message)
    }
}