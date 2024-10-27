package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow
import kotlin.math.round


data class CardPageUIState(
    val remainingCards: List<card> = emptyList(),
    val askedCards: List<card> = emptyList(),
    val currentCard: card? = null,
    val endOfCardList: Boolean = false,
    val userinput: String = "",
    val displayResult: Boolean = false,
    val isCorrect: Boolean = false,
    val shuffle: Boolean = true,
    val isLoopEnabled: Boolean = false,
    val progress: Float = 0f,
    val accuracy: String="",
    val correctAnswers: Int = 0,
    val answers: Int = 0,
)

class CardsViewModel(
    private var cards: List<card>
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardPageUIState())
    val uiState: StateFlow<CardPageUIState> = _uiState.asStateFlow()

    fun enterText(text: String) {
        _uiState.update {
            it.copy(
                userinput = text
            )
        }
    }


    /* fun onBackClick() {
         if (_askedCards.value.isNotEmpty()) {
             // Add the current question back to the remaining questions
             _remainingCards.value = _remainingCards.value.toMutableList().apply {
                 add(0, _currentCard.value)
             }

             // Get the last asked question and set it as the current question
             val lastAskedQuestion = _askedCards.value.removeLast()
             _currentCard.value = lastAskedQuestion
         }
     }*/


    fun onNextClick() {
println("moving to next card")

        if (uiState.value.remainingCards.isNotEmpty()) {
            // Add the current question to the asked questions
            if (uiState.value.currentCard!=null&&uiState.value.isCorrect) {//lehet hogy itt feleslegesen van duplán ellenőrizve //ha az aktuális kártya nem null és a feladat helyesen lett megválaszolva az imént
                _uiState.update {
                    it.copy(
                        askedCards = it.askedCards.toMutableList().apply {
                            it.currentCard?.let { it1 -> add(it1) }
                        }
                    )
                }
            }
            val nextIndex =0  /* annyit módosítottunk, hogy a következő elem mindig az első a listában. a lista hátralevő részét viszont mindig randomizáljuk.*/
            /* if (uiState.value.shuffle) {
                uiState.value.remainingCards.indices.random()
            } else {
                0 // Always take the first question in non-random mode
            }*/
            // Remove the used question from the remaining questions
            _uiState.update {//ennek nyilván hamarabb kell lennie a shuflle-nél. hogy cserélhettem egyáltalán fel??
                //kivesszük a mostani kártyát a pakliból
                val newlist =if(uiState.value.isCorrect) it.remainingCards.toMutableList().filterIndexed { index, card -> index!=nextIndex } else it.remainingCards
                it.copy(remainingCards = newlist)
            }

            //megkeverjük a paklit
            if(uiState.value.shuffle){
                _uiState.update { it.copy(
                    remainingCards = it.remainingCards.shuffled()
                ) }
            }
            // Get the next question
            val nextCard =if(uiState.value.remainingCards.isNotEmpty()) {
                uiState.value.remainingCards[nextIndex];//nextindex nulla
            }else {
                uiState.value.currentCard//tetszőleges kártya, ha a pakli végére értünk, mert ekkor már bezárjuk ezt a felületet
            }



            val progress =uiState.value.askedCards.size.toFloat()/((uiState.value.remainingCards.size+uiState.value.askedCards.size).toFloat())//+-1 még kellhet

            // Update the current question
            _uiState.update {
                it.copy(currentCard = nextCard,
                    userinput = "",
                    displayResult = false,
                   progress = progress
                    )
            }




        }

        if(uiState.value.remainingCards.isEmpty()) {
            if (uiState.value.isLoopEnabled) {
                resetForLooping(preserveLastQuestion = true)
                if (uiState.value.remainingCards.isNotEmpty()) {
                    onNextClick()
                }
            } else {
                _uiState.update {
                    it.copy(endOfCardList = true)
                }
                // return "We reached the end of the question list" //todo handle separately
            }
        }


    }


    fun importCards(newCards: List<card>) {
        cards = newCards.toMutableList()
        resetForLooping(preserveLastQuestion = false)
    }

    fun resetForLooping(preserveLastQuestion: Boolean) {
        _uiState.update {
            it.copy(
                remainingCards = cards.toMutableList(),
                askedCards = if (!preserveLastQuestion) {
                    emptyList()
                } else {
                    it.askedCards
                },
                currentCard = cards.first(),
                endOfCardList = false
            )
        }

    }

    fun checkAnswer() {
        if(uiState.value.displayResult){
            return@checkAnswer
        }
        println("checking answer")
        val isCorrect: Boolean
        if (uiState.value.userinput == uiState.value.currentCard?.szoveg ?: "card not found") {
            isCorrect = true
        } else {
            isCorrect = false
        }

        _uiState.update {
            it.copy(
                isCorrect = isCorrect,
                displayResult = true,
                answers = it.answers + 1,
                correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers,
                accuracy = ((it.correctAnswers.toFloat() / it.answers.toFloat())*100f).formatToDecimals(1)
            )
        }
    }

    fun shuffleCards() {

    }

}
fun Double.formatToDecimals(decimals: Int=4): String {
    val factor = 10.0.pow(decimals.toDouble())
    val roundedValue = round(this * factor) / factor
    return roundedValue.toString().trimEnd { it == '0' }.trimEnd { it == '.' }
}
fun Float.formatToDecimals(decimals: Int=4): String {
    val factor = 10.0.pow(decimals.toDouble())
    val roundedValue = round(this * factor) / factor
    return roundedValue.toString().trimEnd { it == '0' }.trimEnd { it == '.' }
}

data class card(val szoveg: String, val kep: String)