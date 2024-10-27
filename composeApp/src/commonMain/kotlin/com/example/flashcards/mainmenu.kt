package com.example.flashcards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import flashcards.composeapp.generated.resources.Res
import flashcards.composeapp.generated.resources.repeat
import flashcards.composeapp.generated.resources.repeat_on
import flashcards.composeapp.generated.resources.shuffleIcon
import flashcards.composeapp.generated.resources.shuffleIcon_on
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
fun MainScreen(
    onPlayClick: (String, Int, Boolean, Boolean,String) -> Unit
) {
    val cardsets = remember { loadCardsets("cardsets") } // Update with your actual path
    println("cardsets=$cardsets")
    var selectedCardsetID by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCardset by rememberSaveable { mutableStateOf<String?>(null) }
    var isMixEnabled by rememberSaveable { mutableStateOf(false) }
    var isLoopEnabled by rememberSaveable { mutableStateOf(false) }
    var typeorguess by rememberSaveable { mutableStateOf("") }

    BoxWithConstraints {
        val itemWidth = 160.dp
        val spacing = 16.dp
        val totalItemWidth = itemWidth + spacing

        val itemsPerRow = (maxWidth / totalItemWidth).toInt().coerceIn(1, 6)

        Column(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                cardsets.chunked(itemsPerRow).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        rowItems.forEachIndexed { index, cardset ->
                            CardsetSurface(
                                cardset = cardset,
                                isSelected = selectedCardsetID == index,
                                onSelect = {
                                    selectedCardsetID = index
                                    selectedCardset = cardset
                                }
                            )
                        }
                    }
                }
            }

            ControlButtons(
                selectedAudienceID = selectedCardsetID,
                isMixEnabled = isMixEnabled,
                isLoopEnabled = isLoopEnabled,
                onMixToggle = { isMixEnabled = !isMixEnabled },
                onLoopToggle = { isLoopEnabled = !isLoopEnabled },
                onTypeClick = {
                    typeorguess = "Type"
                },
                onGuessClick = {
                    typeorguess = "Guess"
                },
                onPlayClick = {
                    selectedCardsetID?.let { cardsetId ->
                        selectedCardset?.let { cardset -> onPlayClick(cardset, cardsetId, isMixEnabled, isLoopEnabled, typeorguess) }
                    }

                }
            )
        }
    }
}

fun loadCardsets(directoryPath: String): List<String> {

    val directory = File(directoryPath)
    println("directory.absolutePath=${directory.absolutePath}")
    return if (directory.exists() && directory.isDirectory) {
        directory.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
    } else {
        emptyList()
    }
}

@Composable
fun CardsetSurface(
    cardset: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val targetColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val animatedColor by animateColorAsState(targetColor)

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .size(160.dp)
            .clickable { onSelect() },
        shape = MaterialTheme.shapes.medium,
        color = animatedColor
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = cardset,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ControlButtons(
    selectedAudienceID: Int?,
    isMixEnabled: Boolean,
    isLoopEnabled: Boolean,
    onMixToggle: () -> Unit,
    onLoopToggle: () -> Unit,
    onPlayClick: () -> Unit,
    onTypeClick: () -> Unit,
    onGuessClick: () -> Unit
) {
    Surface(
        modifier = Modifier.wrapContentWidth(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier

                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMixToggle) {
                Icon(
                    painter = if (isMixEnabled) painterResource(Res.drawable.shuffleIcon_on) else painterResource(
                        Res.drawable.shuffleIcon
                    ),
                    contentDescription = "Mix"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onLoopToggle) {
                Icon(
                    painter = if (isLoopEnabled) painterResource(Res.drawable.repeat_on) else painterResource(
                        Res.drawable.repeat
                    ),
                    contentDescription = "Loop"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onPlayClick,
                enabled = selectedAudienceID != null
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }

            Button(onClick = {

            }){
                Text(text = "Type")
            }

            Button(onClick = {

            }){
                Text(text = "Guess")
            }
        }
    }
}

