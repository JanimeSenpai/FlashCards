package com.example.flashcards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.CardsViewModel
import com.example.card
import flashcards.composeapp.generated.resources.Res
import flashcards.composeapp.generated.resources.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import java.io.File

@OptIn(ExperimentalResourceApi::class)
@Composable
fun QuestionsPage(
    cardset: String,
    audienceID: Int,
    isRandom: Boolean,
    isLoopEnabled: Boolean,
    navController: NavHostController,
    typeorguess: String
) {

    val cards = remember { mutableStateOf<List<card>>(emptyList()) }
    val viewModel: CardsViewModel = remember { CardsViewModel(cards.value) }

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()




    LaunchedEffect(cardset) {
        cards.value = getCardsFromFile(cardset)
        viewModel.importCards(cards.value)
        viewModel.onNextClick()
    }
    var platform = getPlatform().name
    println("platform=$platform")
    Scaffold(
        floatingActionButton = {
            if (uiState.endOfCardList || "Android" !in platform /*platform=="Web with Kotlin/Wasm"*/) {
                FloatingActionButton(
                    onClick = { navController.navigate("main") },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Main")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center, // Position the FAB at the center bottom
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                progressbar(uiState.progress)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = Shapes().large,
                            modifier = Modifier
                                .animateContentSize()
                                .wrapContentSize()
                        ) {

                            AnimatedVisibility(uiState.endOfCardList==false){
                                Column(
                                Modifier
                                    .padding(64.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {


                                    uiState.currentCard?.let {
                                        Stopwatch(
                                            it.szoveg,
                                            uiState.endOfCardList
                                        )
                                    }

                                    if (uiState.currentCard?.kep != null) {
                                        Image(
                                            bitmap = File("cardsets/$cardset/${uiState.currentCard?.kep}").readBytes()
                                                .decodeToImageBitmap(),
                                            contentDescription = "Card Image",
                                        )
                                    }
                                    var enterpressed by mutableStateOf(0)
                                    var displayresult: Boolean = false
                                    var actiondone = false

                                    OutlinedTextField(
                                        value = uiState.userinput,
                                        onValueChange = { viewModel.enterText(it) },
                                        label = { Text("Enter your answer here") },
                                        modifier = Modifier.onPreviewKeyEvent { it ->
                                            if ((it.key == Key.Enter || it.key == Key.Tab) && it.type == KeyEventType.KeyDown) {
                                                println("enter pressed")
                                                // viewModel.enterText(uiState.userinput.dropLast(1))
                                                enterpressed++

                                                coroutineScope.launch {
                                                    if (uiState.displayResult == false) {
                                                        viewModel.checkAnswer()
                                                        return@launch
                                                    }
                                                    // delay(10)
                                                    if (uiState.displayResult) viewModel.onNextClick()
                                                }
                                                /*

                                                 }
                                                if(!uiState.displayResult==false) {
                                                     println("checkAnswer")
                                                     viewModel.checkAnswer()
                                                    // return@onPreviewKeyEvent true
                                                 }

                                                 if(uiState.displayResult)  {
                                                     println("onNextClick")
                                                     viewModel.onNextClick()
                                                    // return@onPreviewKeyEvent true
                                                 }*/
                                                //println("whats goin on")

                                                //  println("why these wont run")
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    )
                                    LaunchedEffect(enterpressed) {


                                    }

                                    Button(onClick = { viewModel.checkAnswer() }) {
                                        Text("Check")
                                    }
                                }


                            }
                            AnimatedVisibility(uiState.endOfCardList) {
                                Column(Modifier.size(360.dp), horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.Center){
                                    Text("End of list\nAccuracy: ${uiState.accuracy}%", style = MaterialTheme.typography.headlineLarge,textAlign = TextAlign.Center)
                                }
                            }


                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedVisibility(uiState.displayResult) {
                            val modifier = Modifier.sizeIn(
                                minWidth = 160.dp,
                                minHeight = 160.dp,
                                maxWidth = 640.dp,
                                maxHeight = 640.dp
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = Shapes().large,
                                modifier = modifier
                                    .animateContentSize()
                                    .padding(30.dp)
                                //.wrapContentSize()

                            ) {
                                Column(
                                    modifier.padding(30.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val text = if (uiState.isCorrect) {
                                        "Correct!"
                                    } else {
                                        "Wrong!\n\nThe correct answer was: ${uiState.currentCard?.szoveg}"
                                    }
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.headlineSmall,
                                        textAlign = TextAlign.Center
                                    )


                                    Button(onClick = {
                                        viewModel.onNextClick()
                                    }) { Text("Next") }
                                }


                            }
                        }
                    }

                }
            }
        }
    )
}

fun getCardsFromFile(cardset: String): List<card> {
    val f = File("cardsets/$cardset/strings.txt")
    val lines = f.readLines()
    var list = mutableListOf<card>()
    lines.forEachIndexed { index, it ->
        list.add(card(it.toLowerCase(), "${index + 1}.png"))
    }
    return list
}


@Composable
fun Stopwatch(currentQuestion: String, isEndOfList: Boolean) {
    var timeInSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var isCollapsed by remember { mutableStateOf(false) }
    var totalTimeInSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning, isEndOfList) {
        while (isRunning && !isEndOfList) {
            delay(1000L)
            timeInSeconds++
            totalTimeInSeconds++

        }
    }

    LaunchedEffect(currentQuestion) {
        timeInSeconds = 0
        isRunning = true
    }
    LaunchedEffect(isEndOfList) {
        if (isEndOfList) {
            isRunning = false
        }
    }
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60

    val totalMinutes = totalTimeInSeconds / 60
    val totalSeconds = totalTimeInSeconds % 60
    Column(
        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !isCollapsed, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()
        ) {
            Surface(
                modifier = Modifier.width(150.dp) // Set width for rectangle
                    .height(60.dp) // Set height for rectangle
                    .clickable { isCollapsed = true },
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!isEndOfList) Text(
                        text = "${minutes.toString().padStart(2, '0')}:${
                            seconds.toString().padStart(2, '0')
                        }",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    if (isEndOfList) {
                        Text(
                            text = "${
                                totalMinutes.toString().padStart(2, '0')
                            }:${totalSeconds.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isCollapsed, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()
        ) {
            IconButton(onClick = { isCollapsed = false }) {
                Icon(
                    painterResource(Res.drawable.timer),
                    contentDescription = "Timer Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun QuestionControls(
    onBackClick: () -> Unit, onNextClick: () -> Unit, currentQuestion: String, isEndOfList: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button

        AnimatedVisibility(!isEndOfList) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
        }


        // Stopwatch (Placeholder for actual stopwatch implementation)
        Stopwatch(currentQuestion, isEndOfList)

        AnimatedVisibility(!isEndOfList) {
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }

    }
}
@Composable
fun progressbar(progress:Float){

    val animatedProgress by     animateFloatAsState(         targetValue = progress,         animationSpec = ProgressIndicatorDefaults. ProgressAnimationSpec     )
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier.fillMaxWidth(0.6f),
    )
}


/*
@Composable
fun DisplayImageFromFile(filePath: String, modifier: Modifier = Modifier) {
    // Load the image from the file path
    val imgFile = File(filePath)
    if (imgFile.exists()) {
        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        val imageBitmap = bitmap.asImageBitmap()

        // Display the image using Image composable
        Image(
            bitmap = imageBitmap,
            contentDescription = "Image from file",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Handle the case where the image file does not exist
        // You might want to show a placeholder or an error message
    }
}*/

